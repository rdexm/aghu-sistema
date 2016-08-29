package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaEspecialidadeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.dominio.DominioIndAtendimentoAnterior;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacFormaEspecialidade;
import br.gov.mec.aghu.model.AacNivelBusca;
import br.gov.mec.aghu.model.AacNivelBuscaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterFormasParaAgendamentoON extends BaseBusiness {





@EJB
private AmbulatorioRN ambulatorioRN;

private static final Log LOG = LogFactory.getLog(ManterFormasParaAgendamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@Inject
private AacConsultasDAO aacConsultasDAO;

@Inject
private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;

@Inject
private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;

@Inject
private AacFormaEspecialidadeDAO aacFormaEspecialidadeDAO;

@Inject
private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2728391071136231147L;

	public enum ManterFormasParaAgendamentoONExceptionCode implements BusinessExceptionCode {
		ERRO_INFORME_TEMPO_ANTERIOR, ERRO_EXISTEM_DEPENDENCIAS_TABELA, ERRO_FORMA_AGENDAMENTO_COM_CHAVE_JA_EXISTENTE, ERRO_REGRA_DATAS_SALVAR_FORMA_ESPECIALIDADE, ERRO_FORMA_ESPECIALIDADE_COM_CHAVE_JA_EXISTENTE
	}

	public void salvarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		AacFormaAgendamentoId id = new AacFormaAgendamentoId(formaAgendamento.getCondicaoAtendimento() != null ? formaAgendamento
				.getCondicaoAtendimento().getSeq() : null, formaAgendamento.getTipoAgendamento() != null ? formaAgendamento
				.getTipoAgendamento().getSeq() : null, formaAgendamento.getPagador() != null ? formaAgendamento.getPagador().getSeq()
				: null);

		AacFormaAgendamento auxFormaAgendamento = this.getAacFormaAgendamentoDAO().obterPorChavePrimaria(id);
		if (auxFormaAgendamento != null) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_FORMA_AGENDAMENTO_COM_CHAVE_JA_EXISTENTE);
		}

		formaAgendamento.setId(id);
		this.verNivelBusca(formaAgendamento);
		this.getAmbulatorioRN().salvarFormaAgendamento(formaAgendamento);
	}

	public void alterarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		this.verNivelBusca(formaAgendamento);
		this.getAmbulatorioRN().alterarFormaAgendamento(formaAgendamento);
	}

	/**
	 * ORADB VER_NIVEL_BUSCA
	 * 
	 * @param formaAgendamento
	 */
	private void verNivelBusca(AacFormaAgendamento formaAgendamento) throws BaseException {
		if ((DominioIndAtendimentoAnterior.A.equals(formaAgendamento.getAtendimentoAnterior()) || DominioIndAtendimentoAnterior.C
				.equals(formaAgendamento.getAtendimentoAnterior()))
				&& (formaAgendamento.getTempoAnterior() == null || formaAgendamento.getTempoAnterior() == 0)) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_INFORME_TEMPO_ANTERIOR);
		}
	}

	public void removerFormaAgendamento(AacFormaAgendamentoId id) throws ApplicationBusinessException {
		final AacFormaAgendamento afa = aacFormaAgendamentoDAO.obterPorChavePrimaria(id);
		preDeleteFormaAgendamento(afa);
		getAmbulatorioRN().removerFormaAgendamento(afa);
	}

	private void preDeleteFormaAgendamento(AacFormaAgendamento formaAgendamento) throws ApplicationBusinessException {
		Long count = this.getAacConsultasDAO().listarConsultasCount(formaAgendamento);
		if (count != null && count > 0) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_EXISTEM_DEPENDENCIAS_TABELA, "AAC_CONSULTAS");
		}

		count = this.getAacGradeAgendamenConsultasDAO().listarGradeAgendamenConsultasCount(formaAgendamento);
		if (count != null && count > 0) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_EXISTEM_DEPENDENCIAS_TABELA,"AAC_GRADE_AGENDAMEN_CONSULTAS");
		}

		// AAC_FORMA_ESPECIALIDADES
		count = this.getAacFormaEspecialidadeDAO().listarFormasEspecialidadeCount(formaAgendamento);
		if (count != null && count > 0) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_EXISTEM_DEPENDENCIAS_TABELA, "AAC_FORMA_ESPECIALIDADES");
		}

		// AAC_HORARIO_GRADE_CONSULTAS
		count = this.getAacHorarioGradeConsultaDAO().listarHorariosGradeConsultaCount(formaAgendamento);
		if (count != null && count > 0) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_EXISTEM_DEPENDENCIAS_TABELA, "AAC_HORARIO_GRADE_CONSULTAS");
		}

		if (formaAgendamento.getNiveisBusca() != null && !formaAgendamento.getNiveisBusca().isEmpty()) {
			for (AacNivelBusca nivelBusca : formaAgendamento.getNiveisBusca()) {
				this.removerNivelBusca(nivelBusca.getId());
			}
		}
	}

	public void removerNivelBusca(AacNivelBuscaId id) {
		this.getAmbulatorioRN().removerNivelBusca(id);
	}

	public void salvarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		this.getAmbulatorioRN().salvarNivelBusca(nivelBusca);
	}

	public void alterarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		this.getAmbulatorioRN().alterarNivelBusca(nivelBusca);
	}

	public void salvarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		AacFormaEspecialidade auxAacFormaEspecialidade = this.getAacFormaEspecialidadeDAO().obterPorChavePrimaria(
				formaEspecialidade.getId());
		if (auxAacFormaEspecialidade != null) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_FORMA_ESPECIALIDADE_COM_CHAVE_JA_EXISTENTE);
		}
		
		this.validarFormaEspecialidade(formaEspecialidade);
		
		this.getAmbulatorioRN().salvarFormaEspecialidade(formaEspecialidade);
	}

	public void alterarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		this.validarFormaEspecialidade(formaEspecialidade);
		this.getAmbulatorioRN().alterarFormaEspecialidade(formaEspecialidade);
	}

	private void validarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		if ((formaEspecialidade.getDtInicio() != null && formaEspecialidade.getDtFinal() != null && CoreUtil.isMaiorDatas(
				formaEspecialidade.getDtInicio(), formaEspecialidade.getDtFinal()))
				|| (formaEspecialidade.getDtInicio() == null && formaEspecialidade.getDtFinal() != null)) {
			throw new ApplicationBusinessException(ManterFormasParaAgendamentoONExceptionCode.ERRO_REGRA_DATAS_SALVAR_FORMA_ESPECIALIDADE);
		}
	}

	protected AmbulatorioRN getAmbulatorioRN() {
		return ambulatorioRN;
	}

	

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}

	protected AacFormaEspecialidadeDAO getAacFormaEspecialidadeDAO() {
		return aacFormaEspecialidadeDAO;
	}

	protected AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO() {
		return aacHorarioGradeConsultaDAO;
	}

	protected AacFormaAgendamentoDAO getAacFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}

}
