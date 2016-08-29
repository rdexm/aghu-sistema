package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AgendamentoSessaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendamentoSessaoON.class);
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;
	
	private static final long serialVersionUID = -3596844515657446192L;
	
	public enum AgendamentoSessaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_SELECIONA_DIA_AGENDAR_SESSAO, MENSAGEM_ERRO_D1_NAO_INFORMADO_AGENDAR_SESSAO,
		MENSAGEM_ERRO_DATA_MENOR_QUE_ATUAL_AGENDAR_SESSAO, MENSAGEM_ERRO_DATA_FINAL_MAIOR_DATA_INICIAL_AGENDAR_SESSAO,
		MENSAGEM_DIAS_EXCEDIDOS_AGENDAR_SESSAO, MENSAGEM_ERRO_TURNO_NAO_CADASTRADO_AGENDAR_SESSAO, MENSAGEM_ERRO_TIPO_SESSAO_SEM_TURNO,
		MENSAGEM_ERRO_HORA_FORA_DO_TURNO_AGENDAR_SESSAO, MENSAGEM_ERRO_HORA_FORA_DOS_TURNOS_AGENDAR_SESSAO;
		
	}
	
	public void validarFiltrosAgendamentoSessao(List<CadIntervaloTempoVO> listaHorarios, Short tpsSeq,
			DominioTurno turno, Date dataInicio, Date dataFim, Date dthrHoraInicio) throws ApplicationBusinessException {
		
		this.validarListaHorarios(listaHorarios);
		this.validarPeriodoInformado(tpsSeq, dataInicio, dataFim);
		
		List<MptTurnoTipoSessao> turnos = this.mptTurnoTipoSessaoDAO.obterTurnosPorTipoSessaoETurno(tpsSeq, turno);
		
		if (turnos.isEmpty()) {
			if (turno != null) {
				throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_TURNO_NAO_CADASTRADO_AGENDAR_SESSAO);
				
			} else {
				throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_TIPO_SESSAO_SEM_TURNO);
			}
		}
		this.horaInicioRepeitaTurno(dthrHoraInicio, turnos);
	}
	
	private void validarListaHorarios(List<CadIntervaloTempoVO> listaHorarios) throws ApplicationBusinessException {
		List<CadIntervaloTempoVO> listaFiltrada = this.obterListaHorariosFiltrada(listaHorarios);
		
		if (listaFiltrada == null || listaFiltrada.isEmpty()) {
			throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_SELECIONA_DIA_AGENDAR_SESSAO);
		}
		boolean informouD1 = false;
		for (CadIntervaloTempoVO item : listaFiltrada) {
			if (item.getDiaReferencia().equals((short) 1)) {
				informouD1 = true;
				break;
			}
		}
		if (!informouD1) {
			throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_D1_NAO_INFORMADO_AGENDAR_SESSAO);
		}
	}
	
	private List<CadIntervaloTempoVO> obterListaHorariosFiltrada(List<CadIntervaloTempoVO> listaHorarios) {
		List<CadIntervaloTempoVO> listaRetorno = new ArrayList<CadIntervaloTempoVO>();
		for (CadIntervaloTempoVO item : listaHorarios) {
			if (item.isAgendar()) {
				listaRetorno.add(item);
			}
		}
		return listaRetorno;
	}
	
	private void validarPeriodoInformado(Short tpsSeq, Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		if (DateUtil.validaDataMenor(dataInicio, DateUtil.truncaData(new Date()))) {
			throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_DATA_MENOR_QUE_ATUAL_AGENDAR_SESSAO);
		} else if (DateUtil.validaDataMaior(dataInicio, dataFim)) {
			throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_DATA_FINAL_MAIOR_DATA_INICIAL_AGENDAR_SESSAO);
		} else {
			MptTipoSessao tipoSessao = this.mptTipoSessaoDAO.obterPorChavePrimaria(tpsSeq);
			if (tipoSessao.getTempoDisponivel() != null
					&& DateUtil.obterQtdDiasEntreDuasDatas(dataInicio, dataFim).compareTo(tipoSessao.getTempoDisponivel()) > 0) {
				throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_DIAS_EXCEDIDOS_AGENDAR_SESSAO);
			}
		}
	}
	
	private void horaInicioRepeitaTurno(Date dthrHoraInicio, List<MptTurnoTipoSessao> turnos) throws ApplicationBusinessException {
		
		MptTurnoTipoSessao turno = turnos.get(0);
		if (turno != null && dthrHoraInicio != null
				&& !DateUtil.entre(dthrHoraInicio, turno.getHoraInicio(), turno.getHoraFim())) {
			throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_HORA_FORA_DO_TURNO_AGENDAR_SESSAO);
		} else if (dthrHoraInicio != null) {
			boolean respeitaTurno = false;
			for (MptTurnoTipoSessao trn : turnos) {
				if (DateUtil.entre(dthrHoraInicio, trn.getHoraInicio(), trn.getHoraFim())) {
					respeitaTurno = true;
					break;
				}
			}
			if (!respeitaTurno) {
				throw new ApplicationBusinessException(AgendamentoSessaoONExceptionCode.MENSAGEM_ERRO_HORA_FORA_DOS_TURNOS_AGENDAR_SESSAO);
			}
		}
	}
}
