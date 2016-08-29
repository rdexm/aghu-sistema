package br.gov.mec.aghu.ambulatorio.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.VAacSiglaUnfSalaDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.SolicitanteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PesquisarPacientesAgendadosON extends BaseBusiness {

	private static final Log LOG = LogFactory
			.getLog(PesquisarPacientesAgendadosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private VAacSiglaUnfSalaDAO vAacSiglaUnfSalaDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private MamControlesDAO mamControlesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1806910942900134963L;

	private enum PesquisarPacientesAgendadosONExceptionCode implements
			BusinessExceptionCode {
		AAC_00197, MAM_01508, MAM_00943, MAM_01195;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public DominioTurno defineTurno(Date data)
			throws ApplicationBusinessException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setLenient(false);
		Date dtAtual;
		try {
			dtAtual = sdf.parse(sdf.format(data));
			// TESTE MANHÃ
			AghParametros dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO1);
			Date dtInicial1 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO1);
			Date dtFinal1 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial1, dtFinal1)) {
				return DominioTurno.M;
			}
			// TESTE TARDE
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO2);
			Date dtInicial2 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO2);
			Date dtFinal2 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial2, dtFinal2)) {
				return DominioTurno.T;
			}
			// TESTE NOITE
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO3);
			Date dtInicial3 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO3);
			Date dtFinal3 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial3, dtFinal3)) {
				return DominioTurno.N;
			}
		} catch (ParseException e) {
			PesquisarPacientesAgendadosONExceptionCode.AAC_00197
					.throwException();
		}
		return null;
	}

	public DataInicioFimVO definePeriodoTurno(DominioTurno turno)
			throws ApplicationBusinessException {
		if (turno == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setLenient(false);
		DataInicioFimVO datas = null;
		try {
			Date dtInicial = null;
			Date dtFinal = null;
			if (DominioTurno.M.equals(turno)) {
				AghParametros dtParam = getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_INI_TURNO1);
				dtInicial = sdf.parse(dtParam.getVlrTexto());
				dtParam = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_FIM_TURNO1);
				dtFinal = sdf.parse(dtParam.getVlrTexto());
			} else if (DominioTurno.T.equals(turno)) {
				AghParametros dtParam = getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_INI_TURNO2);
				dtInicial = sdf.parse(dtParam.getVlrTexto());
				dtParam = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_FIM_TURNO2);
				dtFinal = sdf.parse(dtParam.getVlrTexto());
			} else if (DominioTurno.N.equals(turno)) {
				AghParametros dtParam = getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_INI_TURNO3);
				dtInicial = sdf.parse(dtParam.getVlrTexto());
				dtParam = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_FIM_TURNO3);
				dtFinal = sdf.parse(dtParam.getVlrTexto());
			}
			datas = new DataInicioFimVO(dtInicial, dtFinal);
		} catch (ParseException e) {
			PesquisarPacientesAgendadosONExceptionCode.AAC_00197
					.throwException();
		}
		return datas;
	}

	public DataInicioFimVO definePeriodoTurno(Date data)
			throws ApplicationBusinessException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setLenient(false);
		Date dtAtual;
		try {
			dtAtual = sdf.parse(sdf.format(data));
			// TESTE MANHÃ
			AghParametros dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO1);
			Date dtInicial1 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO1);
			Date dtFinal1 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial1, dtFinal1)) {
				return new DataInicioFimVO(dtInicial1, dtFinal1);
			}
			// TESTE TARDE
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO2);
			Date dtInicial2 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO2);
			Date dtFinal2 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial2, dtFinal2)) {
				return new DataInicioFimVO(dtInicial2, dtFinal2);
			}
			// TESTE NOITE
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_INI_TURNO3);
			Date dtInicial3 = sdf.parse(dtParam.getVlrTexto());
			dtParam = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_FIM_TURNO3);
			Date dtFinal3 = sdf.parse(dtParam.getVlrTexto());
			if (DateUtil.entre(dtAtual, dtInicial3, dtFinal3)) {
				return new DataInicioFimVO(dtInicial3, dtFinal3);
			}
		} catch (ParseException e) {
			PesquisarPacientesAgendadosONExceptionCode.AAC_00197
					.throwException();
		}
		return null;
	}

	public void registraChegadaPaciente(AacConsultas consulta,	String nomeMicrocomputador)	throws BaseException {
		MamControles controle = mamControlesDAO.obterMamControlePorNumeroConsulta(consulta.getNumero());
		
		if (controle != null
				&& !controle.getSituacaoAtendimento()
						.getAgendado()) {
			PesquisarPacientesAgendadosONExceptionCode.MAM_01508
					.throwException();
		}
		AghParametros sitParam = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SEQ_SIT_AGUARDANDO);
		if (sitParam == null) {
			PesquisarPacientesAgendadosONExceptionCode.MAM_00943
					.throwException();
		}
		MamSituacaoAtendimentos situacao = getAmbulatorioFacade()
				.obterSituacaoAtendimentos(
						sitParam.getVlrNumerico().shortValue());
		getAmbulatorioFacade().atualizaControleConsultaSituacao(consulta,
				situacao, nomeMicrocomputador);
		getAmbulatorioFacade().atualizarConsultaRetorno(consulta);
		
	}

	public void desmarcaChegadaPaciente(Integer consultaNumero, String nomeMicrocomputador)throws ApplicationBusinessException {
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(consultaNumero);		
		
		if (consulta.getControle() != null	&& !consulta.getControle().getSituacaoAtendimento().getAguardando()) {
			PesquisarPacientesAgendadosONExceptionCode.MAM_01195.throwException();
		}
		AghParametros sitParam = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_AGENDADO);
		if (sitParam == null) {
			consulta.setChegou(true);
			PesquisarPacientesAgendadosONExceptionCode.MAM_00943.throwException();
		}
		MamSituacaoAtendimentos situacao = getAmbulatorioFacade().obterSituacaoAtendimentos(sitParam.getVlrNumerico().shortValue());

		getAmbulatorioFacade().atualizaControleConsultaSituacao(consulta, situacao, nomeMicrocomputador);
	}
	
	
	
	public void atualizarConsultaVO(ConsultaAmbulatorioVO vo){
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(vo.getNumero());
		if (consulta.getControle()!=null) {
			vo.setControleSituacao(consulta.getControle().getSituacao());
			vo.setControledtHrFim(consulta.getControle().getDtHrFim());
			vo.setControledtHrFim(consulta.getControle().getDtHrInicio());
			vo.setControledtHrFim(consulta.getControle().getDtHrInicio());
			vo.setMicroDoAtendimento(consulta.getControle().getMicroDoAtendimento());
			if (consulta.getControle().getSituacaoAtendimento()!=null) {			
				vo.setControleSituacaoAtendimentoSeq(consulta.getControle().getSituacaoAtendimento().getSeq());
				vo.setControleSituacaoAtendimentoAgendado(consulta.getControle().getSituacaoAtendimento().getAgendado());
				vo.setControleSituacaoAtendimentoPacAtend(consulta.getControle().getSituacaoAtendimento().getPacAtend());
			}	
		}	
		if (consulta.getRetorno()!=null) {
			vo.setRetornoDescricao(consulta.getRetorno().getDescricao());
			vo.setRetornoSeq(consulta.getRetorno().getSeq());
		}	
	}
	
	

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	public List<SolicitanteVO> executaCursorSolicitante(Short unfSeq,
			Byte sala, Date dtConsulta) throws ApplicationBusinessException {
		List<VAacSiglaUnfSala> listaCriteria =  this.getVAacSiglaUnfSalaDAO().executaCursorSolicitante(unfSeq, sala,
				dtConsulta);
		
		List<SolicitanteVO> lista = new ArrayList<SolicitanteVO>();

		for (VAacSiglaUnfSala vAacSiglaUnfSala : listaCriteria) {
			List<AipSolicitantesProntuario> listaSolicitanteProntuario = getPacienteFacade()
					.pesquisarSolicitantesProntuarioPorUnidadeFuncional(
							vAacSiglaUnfSala.getId().getUnfSeq());
			if (!listaSolicitanteProntuario.isEmpty()) {
				SolicitanteVO vo = new SolicitanteVO();
				vo.setSeq(listaSolicitanteProntuario.get(0).getSeq());
				vo.setVolumesManuseados(listaSolicitanteProntuario.get(0)
						.getVolumesManuseados());
				vo.setZonaSala(getAmbulatorioFacade().defineTurno(dtConsulta)
						+ "/" + vAacSiglaUnfSala.getSigla() + "/"
						+ vAacSiglaUnfSala.getId().getSala());
				lista.add(vo);
			}
		}
		return lista;
	}

	private IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	private VAacSiglaUnfSalaDAO getVAacSiglaUnfSalaDAO() {
		return vAacSiglaUnfSalaDAO;
	}

	public List<AacConsultas> executarCursorConsulta(Integer cConNumero) throws ApplicationBusinessException {
		AacConsultasDAO dao = this.getAacConsultasDAO();
		
		List<AacConsultas> lista = dao.pesquisaConsultasNumero(cConNumero);
		
		for (AacConsultas aacConsultas : lista) {
			List<Object[]> listaObjetos = dao.pesquisarConsultaMesmaData(aacConsultas);
			
			Iterator<Object[]> it = listaObjetos.listIterator();

			Date dtConsulta = null;
			while (it.hasNext()) { // obj[0] = numero, obj[1] = dtConsulta
				Object[] obj = it.next();

				if (obj[0] != null && obj[1] != null) { // testa se o resultado
														// nao é nullo
					dtConsulta = (Date) obj[1];
					// testa se o  turno é o mesmo das duas consultas
					if (this.getAmbulatorioFacade().defineTurno(dtConsulta).equals(getAmbulatorioFacade().defineTurno(aacConsultas.getDtConsulta()))) { 
						lista.remove(aacConsultas);
					}
				}
			}
			
			
		}		
		return lista;		
	}

	private AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

}