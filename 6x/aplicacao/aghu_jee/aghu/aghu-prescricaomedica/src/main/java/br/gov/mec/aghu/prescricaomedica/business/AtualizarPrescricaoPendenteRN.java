package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @ORADB: mpmk_pendente
 * 
 * @author gmneto
 * 
 */
@Stateless
public class AtualizarPrescricaoPendenteRN extends BaseBusiness {


@EJB
private PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN;

private static final Log LOG = LogFactory.getLog(AtualizarPrescricaoPendenteRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IBancoDeSangueFacade bancoDeSangueFacade;

@Inject
private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;

@Inject
private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;

@Inject
private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;

@Inject
private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

@Inject
private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4307025924398715560L;

	public enum AtualizarPrescricaoPendenteExceptionCode implements
			BusinessExceptionCode {
		MPM_02065, MPM_02066, MPM_02067, PRESCRICAO_NULA;
	}

	/**
	 * Método que realiza o processo de tornar uma prescrição pendente a partir
	 * dos dados de sua chave primária na base de dados.
	 * 
	 * @ORADB mpmk_pendente.mpmp_pendente
	 * 
	 * @param idAtendimento
	 * @param seqPrescricao
	 * @throws BaseException 
	 */
	public void atualizarPrescricaoPendente(Integer idAtendimento,
			Integer seqPrescricao, String nomeMicrocomputador) throws BaseException {

		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(idAtendimento);
		id.setSeq(seqPrescricao);

		MpmPrescricaoMedica prescricao = this.getPrescricaoMedicaDAO()
				.obterPorChavePrimaria(id);

		this.atualizarPrescricaoPendente(prescricao, nomeMicrocomputador);

	}

	
	/**
	 * Método que realiza o processo de tornar uma prescrição pendente.
	 * 
	 * @ORADB mpmk_pendente.mpmp_pendente
	 * 
	 * @param prescricao
	 * @throws BaseException 
	 */
	public void atualizarPrescricaoPendente(MpmPrescricaoMedica prescricao, String nomeMicrocomputador)
			throws BaseException {

		if (prescricao == null) {
			throw new ApplicationBusinessException(
					AtualizarPrescricaoPendenteExceptionCode.PRESCRICAO_NULA);
		}
		
		this.getPrescricaoMedicaDAO().refresh(prescricao);

		if (!DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())){
			return ;
		}

		
		Date data = null;

		if (prescricao.getDthrInicioMvtoPendente() != null) {
			data = prescricao.getDthrInicioMvtoPendente();
		} else {
			data = prescricao.getDthrMovimento();
		}

		this.atualizarDietaPendente(prescricao, data);

		this.atualizarCuidadoPendente(prescricao, data);

		this.atualizarMedicamentoPendente(prescricao, data, nomeMicrocomputador);

		this.atualizarProcedimentosPrescricaoPendente(prescricao, data, nomeMicrocomputador);

		this.atualizarHemoterapiaPrescricaoPendente(prescricao, data);

		this.atualizarPrescricaoMedicaPendente(prescricao);

		this.getPrescricaoMedicaDAO().flush();

	}

	/**
	 * Atualiza o estado da prescricao médica para pendente.
	 * 
	 * @ORADB mpmk_pendente.MPMP_PENDENTE_PRCR
	 * @param prescricao
	 */
	private void atualizarPrescricaoMedicaPendente(
			MpmPrescricaoMedica prescricao) {

		if (prescricao.getSituacao() == DominioSituacaoPrescricao.U) {
			prescricao.setSituacao(DominioSituacaoPrescricao.L);
			if (prescricao.getDthrInicioMvtoPendente() == null) {
				prescricao.setDthrInicioMvtoPendente(prescricao
						.getDthrMovimento());
			}
			prescricao.setDthrMovimento(null);

		}

	}

	/**
	 * Método que atualiza os procedimentos de uma prescrição quando esta
	 * torna-se pendente.
	 * 
	 * 
	 * @ORADB mpmk_pendente.MPMP_PENDENTE_PROC
	 * 
	 * @param atendimento
	 * @param data
	 * @param dataInicial
	 * @param dataFinal
	 * @throws BaseException 
	 */
	private void atualizarProcedimentosPrescricaoPendente(
			MpmPrescricaoMedica prescricao, Date data, String nomeMicrocomputador) throws BaseException {

		List<MpmPrescricaoProcedimento> procedimentosPrescricao = this
				.getPrescricaoProcedimentoDAO()
				.listarProcedimentosPrescricaoPendente(prescricao, data);

		for (MpmPrescricaoProcedimento procedimento : procedimentosPrescricao) {
			procedimento.setIndPendente(DominioIndPendenteItemPrescricao.P);

			getPrescreverProcedimentoEspecialRN().atualizarPrescricaoProcedimento(procedimento, nomeMicrocomputador);
		}
	}

	/**
	 * Método utilizado para fazer as atualizações necessárias na homeoterapia
	 * quando a prescrição fica pendente.
	 * 
	 * @ORADB mpmk_pendente.MPMP_PENDENTE_HEMO
	 * 
	 */
	private void atualizarHemoterapiaPrescricaoPendente(
			MpmPrescricaoMedica prescricao, Date data) {

		List<AbsSolicitacoesHemoterapicas> solicitacoesHemoterapicas = this
				.getBancoDeSangueFacade()
				.buscarSolicitacoesHemoterapicasPendentes(prescricao, data);

		for (AbsSolicitacoesHemoterapicas solicitacao : solicitacoesHemoterapicas) {
			this.validarJustificativa(solicitacao);
		}

	}

	/**
	 * ORADB: Procedure MPMP_PENDENTE_CUID
	 * 
	 * @param atendimento
	 * @param dthrTrabalho
	 * @param dthrInicio
	 * @param dthrFim
	 */
	private void atualizarCuidadoPendente(MpmPrescricaoMedica prescricao,
			Date dthrTrabalho) throws ApplicationBusinessException {
		try {
			List<MpmPrescricaoCuidado> listaPrescricaoCuidado = this
					.getPrescricaoCuidadoDAO().listarPrescricaoCuidadoPendente(
							prescricao, dthrTrabalho);
			for (MpmPrescricaoCuidado prescricaoCuidado : listaPrescricaoCuidado) {
				prescricaoCuidado.setIndPendente(DominioIndPendenteItemPrescricao.P);
			}

			// TODO chamar método que atualiza lista de prescricao cuidado
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					AtualizarPrescricaoPendenteExceptionCode.MPM_02065);
		}
	}

	/**
	 * ORADB: Procedure MPMP_PENDENTE_DIET
	 * 
	 * @param atendimento
	 * @param dthrTrabalho
	 * @param dthrInicio
	 * @param dthrFim
	 */
	private void atualizarDietaPendente(MpmPrescricaoMedica prescricao,
			Date dthrTrabalho) throws ApplicationBusinessException {
		try {
			List<MpmPrescricaoDieta> listaPrescricaoDieta = this
					.getPrescricaoDietaDAO().listarPrescricaoDietaPendente(
							prescricao, dthrTrabalho);
			for (MpmPrescricaoDieta prescricaoDieta : listaPrescricaoDieta) {
				prescricaoDieta.setIndPendente(DominioIndPendenteItemPrescricao.P);
			}
			// TODO chamar método que atualiza lista de prescricao dieta
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					AtualizarPrescricaoPendenteExceptionCode.MPM_02066);
		}
	}

	/**
	 * ORADB: Procedure MPMP_PENDENTE_MDTO
	 * 
	 * @param atendimento
	 * @param dthrTrabalho
	 * @param dthrInicio
	 * @param dthrFim
	 */
	private void atualizarMedicamentoPendente(MpmPrescricaoMedica prescricao,
			Date dthrTrabalho, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			List<MpmPrescricaoMdto> listaPrescricaoMdtoOriginal = this
					.getPrescricaoMdtoDAO()
					.listarPrescricaoMedicamentoPendente(prescricao,
							dthrTrabalho);
			for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoMdtoOriginal) {
				getPrescricaoMdtoDAO().desatachar(prescricaoMdto);
			}
			List<MpmPrescricaoMdto> listaPrescricaoMdto = this
					.getPrescricaoMdtoDAO()
					.listarPrescricaoMedicamentoPendente(prescricao,
							dthrTrabalho);

			for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoMdto) {
				prescricaoMdto.setIndPendente(DominioIndPendenteItemPrescricao.P);
			}
			this.getPrescricaoMedicaFacade().persistirPrescricaoMedicamentos(
					listaPrescricaoMdto, nomeMicrocomputador, listaPrescricaoMdtoOriginal);

		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					AtualizarPrescricaoPendenteExceptionCode.MPM_02067);
		}
	}

	/**
	 * Método que valida as justificativas de uma solicitação hemoterápicas. De
	 * acordo com conversa do vicente com o vacaro, estas regras não serão
	 * inclusas nesta versão do sistema.
	 * 
	 * @param solicitacao
	 */
	private void validarJustificativa(AbsSolicitacoesHemoterapicas solicitacao) {
		// TODO Auto-generated method stub

	}

	/**
	 * Retorna o DAO de prescrição médica.
	 * 
	 * @return
	 */
	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected IBancoDeSangueFacade getBancoDeSangueFacade(){
		return bancoDeSangueFacade;
	}

	/**
	 * Retorna o DAO de Prescricao Procedimento.
	 * 
	 * @return
	 */
	protected MpmPrescricaoProcedimentoDAO getPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	/**
	 * 
	 * @return
	 */
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	/**
	 * 
	 * @return
	 */
	protected MpmPrescricaoCuidadoDAO getPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

	protected MpmPrescricaoDietaDAO getPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}

	protected MpmPrescricaoMdtoDAO getPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}
	
	protected PrescreverProcedimentoEspecialRN getPrescreverProcedimentoEspecialRN() {
		return prescreverProcedimentoEspecialRN;
	}
}
