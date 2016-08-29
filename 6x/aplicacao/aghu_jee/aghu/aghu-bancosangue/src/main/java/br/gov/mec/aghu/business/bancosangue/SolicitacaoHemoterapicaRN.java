package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.bancosangue.dao.AbsItensSolHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsMotivosCancelaColetasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.VAbsMovimentoComponenteDAO;
import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.bancosangue.vo.AtualizaMotivoCancelamentoColetaVO;
import br.gov.mec.aghu.bancosangue.vo.SolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioTipoMotivoCancelaColeta;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsMotivosCancelaColetas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAbsMovimentoComponente;
import br.gov.mec.aghu.model.VAbsMovimentoComponenteId;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Implementação da package ABSK_SHE_RN.
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class SolicitacaoHemoterapicaRN extends BaseBusiness implements Serializable {
	
	private static final String NAO = "Não";

	private static final String SIM = "Sim";

	@EJB
	private SolicitacaoHemoterapicaJournalRN solicitacaoHemoterapicaJournalRN;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoHemoterapicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AbsMotivosCancelaColetasDAO absMotivosCancelaColetasDAO;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO absSolicitacoesHemoterapicasDAO;
	
	@Inject
	private AbsItensSolHemoterapicasDAO absItensSolHemoterapicasDAO;
	
	@Inject
	private VAbsMovimentoComponenteDAO vAbsMovimentoComponenteDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = -2528122644679527138L;

	public enum SolicitacaoHemoterapicaRNExceptionCode implements
			BusinessExceptionCode {
		ABS_00166, ABS_00391, ABS_00393, ABS_00395, ABS_00396, ABS_00479, ABS_00667, ABS_00668, ABS_00669, ABS_00670, ABS_00671, ABS_00672;
	}

	public void excluirSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacoesHemoterapicas)
			throws ApplicationBusinessException {
		solicitacoesHemoterapicas = getAbsSolicitacoesHemoterapicasDAO().obterPorChavePrimaria(solicitacoesHemoterapicas.getId());
		
		preRemoveSolicitacaoHemoterapica(solicitacoesHemoterapicas);

		getAbsSolicitacoesHemoterapicasDAO().remover(
				solicitacoesHemoterapicas);
	}

	public void inserirSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preInserir(solicitacaoHemoterapica, nomeMicrocomputador);

		getAbsSolicitacoesHemoterapicasDAO().persistir(
				solicitacaoHemoterapica);
	}

	public AbsSolicitacoesHemoterapicas atualizarSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preAtualizar(solicitacaoHemoterapica, nomeMicrocomputador);

		if (!getAbsSolicitacoesHemoterapicasDAO().contains(
				solicitacaoHemoterapica)) {
			solicitacaoHemoterapica = getAbsSolicitacoesHemoterapicasDAO()
					.merge(solicitacaoHemoterapica);
		}

		// Chamada de trigger "after each row" (journal)
		this.executarTriggerAposUpdadeDeSolicitacaoHemoterapica(solicitacaoHemoterapica);

		return solicitacaoHemoterapica;
	}

	/**
	 * ORADB ABST_SHE_BRU
	 * 
	 * Implementação da trigger de before UPDATE da tabela
	 * ABS_SOLICITACOES_HEMOTERAPICAS
	 * 
	 * @param newSolicitacaoHemoterapica
	 * @param oldSolicitacaoHemoterapica
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void preAtualizar(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws ApplicationBusinessException {

//		SolicitacaoHemoterapicaVO solicitacaoHemoterapicaVO = new SolicitacaoHemoterapicaVO();
//		solicitacaoHemoterapicaVO = getAbsSolicitacoesHemoterapicasDAO()
//				.obtemSolicitacaoHemoterapicaVO(
//						solicitacaoHemoterapica.getPrescricaoMedica().getId()
//								.getAtdSeq(),
//						solicitacaoHemoterapica.getId().getSeq());
		
		///AbsSolicitacoesHemoterapicas oldSolicitacaoHemoterapica = getAbsSolicitacoesHemoterapicasDAO().obterOriginal(solicitacaoHemoterapica);
		AbsSolicitacoesHemoterapicas oldSolicitacaoHemoterapica = getAbsSolicitacoesHemoterapicasDAO().obterPorChavePrimaria(solicitacaoHemoterapica.getId());

		if (CoreUtil.modificados(solicitacaoHemoterapica.getDthrFim(),
				oldSolicitacaoHemoterapica.getDthrFim())
				&& solicitacaoHemoterapica.getDthrFim() != null) {
			solicitacaoHemoterapica.setAlteradoEm(new Date());
		}

		if (CoreUtil.modificados(solicitacaoHemoterapica.getPrescricaoMedica()
				.getId().getAtdSeq(), oldSolicitacaoHemoterapica.getId().getAtdSeq())
				|| CoreUtil.modificados(solicitacaoHemoterapica.getId()
						.getSeq(), oldSolicitacaoHemoterapica.getId().getSeq())
				|| CoreUtil.modificados(
						solicitacaoHemoterapica.getDthrSolicitacao(),
						oldSolicitacaoHemoterapica.getDthrSolicitacao())
				|| CoreUtil.modificados(
						solicitacaoHemoterapica.getIndPacTransplantado(),
						oldSolicitacaoHemoterapica.getIndPacTransplantado())
				|| CoreUtil.modificados(
						solicitacaoHemoterapica.getJustificativa(),
						oldSolicitacaoHemoterapica.getJustificativa())
				|| CoreUtil.modificados(
						solicitacaoHemoterapica.getObservacao(),
						oldSolicitacaoHemoterapica.getObservacao())) {
			this.verificaIndPendenteSolicitacaoHemoterapica(solicitacaoHemoterapica
					.getIndPendente());
		}

		if (solicitacaoHemoterapica.getMotivoCancelaColeta() == null) {
			this.verificaAtendimentoSolicitacaoHemoterapica(oldSolicitacaoHemoterapica.getId()
					.getAtdSeq());
		}

		if (!CoreUtil.modificados(
				solicitacaoHemoterapica.getIndSituacaoColeta(),
				oldSolicitacaoHemoterapica.getIndSituacaoColeta())) {
			this.verificaPrescricaoSolicitacaoHemoterapica(
					solicitacaoHemoterapica.getPrescricaoMedica().getId()
							.getAtdSeq(),
					solicitacaoHemoterapica.getDthrSolicitacao(),
					solicitacaoHemoterapica.getDthrFim(),
					solicitacaoHemoterapica.getCriadoEm(),
					solicitacaoHemoterapica.getIndPendente(), "U", nomeMicrocomputador, new Date());

			this.getPrescricaoMedicaFacade().verificaPrescricaoMedicaUpdate(
					solicitacaoHemoterapica.getPrescricaoMedica().getId()
							.getAtdSeq(),
					solicitacaoHemoterapica.getDthrSolicitacao(), null,
					solicitacaoHemoterapica.getCriadoEm(),
					solicitacaoHemoterapica.getIndPendente(), "U", nomeMicrocomputador, new Date());
		}

		AtualizaMotivoCancelamentoColetaVO atualizaMotivoCancelamentoColetaVO = this
				.atualizaMotivoCancelamentoColetaSolicitacaoHemoterapica(
						solicitacaoHemoterapica.getPrescricaoMedica().getId()
								.getAtdSeq(),
						solicitacaoHemoterapica.getId().getSeq(),
						oldSolicitacaoHemoterapica.getMotivoCancelaColeta() != null ? oldSolicitacaoHemoterapica
								.getMotivoCancelaColeta().getSeq() : null,
						(solicitacaoHemoterapica.getMotivoCancelaColeta() != null) ? solicitacaoHemoterapica
								.getMotivoCancelaColeta().getSeq() : null,
								oldSolicitacaoHemoterapica.getIndSituacaoColeta(),
						solicitacaoHemoterapica.getIndSituacaoColeta(),
						oldSolicitacaoHemoterapica.getIndResponsavelColeta(),
						solicitacaoHemoterapica.getIndResponsavelColeta(),
						(solicitacaoHemoterapica.getServidorCancelaColeta() != null) ? solicitacaoHemoterapica
								.getServidorCancelaColeta().getId()
								.getMatricula()
								: null,
						(solicitacaoHemoterapica.getServidorCancelaColeta() != null) ? solicitacaoHemoterapica
								.getServidorCancelaColeta().getId()
								.getVinCodigo()
								: null, solicitacaoHemoterapica
								.getDthrCancelamentoColeta());

		if (atualizaMotivoCancelamentoColetaVO.getMatriculaCancelamentoColeta() == null
				|| atualizaMotivoCancelamentoColetaVO
						.getVinCodigoCancelamentoColeta() == null) {
			solicitacaoHemoterapica.setServidorCancelaColeta(null);
		} else {
			RapServidoresId idServidor = new RapServidoresId(
					atualizaMotivoCancelamentoColetaVO
							.getMatriculaCancelamentoColeta(),
					atualizaMotivoCancelamentoColetaVO
							.getVinCodigoCancelamentoColeta());
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(
					idServidor);
			solicitacaoHemoterapica.setServidorCancelaColeta(servidor);
		}

		solicitacaoHemoterapica
				.setDthrCancelamentoColeta(atualizaMotivoCancelamentoColetaVO
						.getDthrCancelamentoColeta());
	}

	public void preInserir(AbsSolicitacoesHemoterapicas solicitacoesHemoterapica, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		solicitacoesHemoterapica.setCriadoEm(new Date());

		// Insere servidor que cadastrou
		if (servidorLogado.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00166);
		}

		RapServidoresId idServidor = new RapServidoresId(
				servidorLogado.getId().getMatricula(),
				servidorLogado.getId().getVinCodigo());
		RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
		solicitacoesHemoterapica.setServidor(servidor);

		/* verifica a existencia de uma prescricao para esta solicitacao */
		this.verificaPrescricaoSolicitacaoHemoterapica(solicitacoesHemoterapica
				.getPrescricaoMedica().getId().getAtdSeq(),
				solicitacoesHemoterapica.getDthrSolicitacao(),
				solicitacoesHemoterapica.getDthrFim(),
				solicitacoesHemoterapica.getCriadoEm(),
				solicitacoesHemoterapica.getIndPendente(), "I", nomeMicrocomputador, new Date());

		this.verificaAtendimentoSolicitacaoHemoterapica(solicitacoesHemoterapica
				.getPrescricaoMedica().getId().getAtdSeq());

		this.verificaDatasSolicitacaoHemoterapica(null,
				solicitacoesHemoterapica.getDthrSolicitacao(), null,
				solicitacoesHemoterapica.getDthrFim());
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_VER_DELECAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDelecaoSolicitacaoHemoterapica()
			throws ApplicationBusinessException {
		// Solicitação Hemoterápica não deve ser excluída.
		throw new ApplicationBusinessException(
				SolicitacaoHemoterapicaRNExceptionCode.ABS_00479);
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_VER_ATENDIM
	 * 
	 * Verifica se o atendimento desta solicitação está vigente.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaAtendimentoSolicitacaoHemoterapica(
			Integer seqAtendimento) throws ApplicationBusinessException {
		AghAtendimentos atendimento = getAghuFacade()
				.obterAghAtendimentoPorChavePrimaria(seqAtendimento);

		if (atendimento != null && atendimento.getDthrFim() != null) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00396);
		}
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_VER_DATAS
	 * 
	 * Consiste as datas de inicio e desativação.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDatasSolicitacaoHemoterapica(Date oldDthrInicio,
			Date newDthrInicio, Date oldDthrDesativacao, Date newDthrDesativacao)
			throws ApplicationBusinessException {
		if (oldDthrInicio != null && newDthrInicio != null
				&& !CoreUtil.igual(oldDthrInicio, newDthrInicio)) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00393);
		}

		if (oldDthrDesativacao != null && newDthrDesativacao != null
				&& !CoreUtil.igual(oldDthrDesativacao, newDthrDesativacao)) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00393);
		} else if (CoreUtil.isMenorOuIgualDatas(oldDthrDesativacao,
				oldDthrInicio)) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00395);
		}
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_VER_PRESCR
	 * 
	 * A solicitação de sangue deve estar no período de uma prescrição para o
	 * mesmo atendimento.
	 * @param dataFimVinculoServidor 
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaPrescricaoSolicitacaoHemoterapica(
			Integer seqAtendimento, Date dthrInicio, Date dthrFim, Date criado,
			DominioIndPendenteItemPrescricao indPendente, String operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		getPrescricaoMedicaFacade().verificaPrescricaoMedica(seqAtendimento,
				dthrInicio, dthrFim, criado, indPendente, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_VER_IND_PEND
	 * 
	 * Consiste se ind pendente permite alteração ou exclusão na solicitação.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaIndPendenteSolicitacaoHemoterapica(
			DominioIndPendenteItemPrescricao indPendente)
			throws ApplicationBusinessException {
		if (!DominioIndPendenteItemPrescricao.P.equals(indPendente)) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00391);
		}
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_ATU_CANC_COL
	 * 
	 * No cancelamento da solicitação, validar condições de validação.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	public AtualizaMotivoCancelamentoColetaVO atualizaMotivoCancelamentoColetaSolicitacaoHemoterapica(
			Integer seqAtendimento, Integer seq,
			Short oldSeqMotivosCancelaColetas,
			Short newSeqMotivosCancelaColetas,
			DominioSituacaoColeta oldSituacaoColeta,
			DominioSituacaoColeta newSituacaoColeta,
			DominioResponsavelColeta oldResponsavelColeta,
			DominioResponsavelColeta newResponsavelColeta,
			Integer matriculaCancelamentoColeta,
			Short vinCodigoCancelamentoColeta, Date dthrCancelamentoColeta)
			throws ApplicationBusinessException {
		AtualizaMotivoCancelamentoColetaVO atualizaMotivoCancelamentoColetaVO = new AtualizaMotivoCancelamentoColetaVO(
				matriculaCancelamentoColeta, vinCodigoCancelamentoColeta,
				dthrCancelamentoColeta);

		if (CoreUtil.modificados(oldSeqMotivosCancelaColetas,
				newSeqMotivosCancelaColetas)
				|| CoreUtil.modificados(oldSituacaoColeta, newSituacaoColeta)) {
			if (newSeqMotivosCancelaColetas != null
					&& !DominioSituacaoColeta.P.equals(oldSituacaoColeta)
					&& !DominioSituacaoColeta.E.equals(oldSituacaoColeta)
					&& !DominioSituacaoColeta.C.equals(oldSituacaoColeta)) {
				throw new ApplicationBusinessException(
						SolicitacaoHemoterapicaRNExceptionCode.ABS_00667);
			}

			if (newSeqMotivosCancelaColetas == null
					&& DominioSituacaoColeta.C.equals(newSituacaoColeta)) {
				throw new ApplicationBusinessException(
						SolicitacaoHemoterapicaRNExceptionCode.ABS_00668);
			}

			if (newSeqMotivosCancelaColetas != null
					&& !DominioSituacaoColeta.C.equals(newSituacaoColeta)) {
				throw new ApplicationBusinessException(
						SolicitacaoHemoterapicaRNExceptionCode.ABS_00669);
			}

			if (oldSeqMotivosCancelaColetas != null
					&& newSeqMotivosCancelaColetas == null
					&& !DominioSituacaoColeta.E.equals(newSituacaoColeta)
					&& !DominioSituacaoColeta.P.equals(newSituacaoColeta)) {
				throw new ApplicationBusinessException(
						SolicitacaoHemoterapicaRNExceptionCode.ABS_00670);
			}

			if (DominioResponsavelColeta.S.equals(newResponsavelColeta)
					&& !DominioSituacaoColeta.E.equals(newSituacaoColeta)
					&& !DominioSituacaoColeta.R.equals(newSituacaoColeta)
					&& !DominioSituacaoColeta.C.equals(newSituacaoColeta)) {
				throw new ApplicationBusinessException(
						SolicitacaoHemoterapicaRNExceptionCode.ABS_00671);
			}

			if (oldSeqMotivosCancelaColetas != null) {
				AbsMotivosCancelaColetas motivoCancelaColeta = getAbsMotivosCancelaColetasDAO()
						.obterPorChavePrimaria(oldSeqMotivosCancelaColetas);
				if (motivoCancelaColeta != null
						&& DominioTipoMotivoCancelaColeta.S
								.equals(motivoCancelaColeta.getTipo())
						&& (CoreUtil.modificados(newSituacaoColeta,
								oldSituacaoColeta) || CoreUtil.modificados(
								newSeqMotivosCancelaColetas,
								oldSeqMotivosCancelaColetas))) {
					throw new ApplicationBusinessException(
							SolicitacaoHemoterapicaRNExceptionCode.ABS_00672);
				}
			}

			if (newSeqMotivosCancelaColetas != null
					&& oldSeqMotivosCancelaColetas == null
					|| (newSeqMotivosCancelaColetas != null && newSeqMotivosCancelaColetas != oldSeqMotivosCancelaColetas)) {
				AtualizaCartaoPontoVO atualizaCartaoPontoVO = getBancoDeSangueFacade()
						.atualizaCartaoPontoServidor();
				atualizaMotivoCancelamentoColetaVO
						.setMatriculaCancelamentoColeta(atualizaCartaoPontoVO
								.getMatricula());
				atualizaMotivoCancelamentoColetaVO
						.setVinCodigoCancelamentoColeta(atualizaCartaoPontoVO
								.getVinCodigo());
				atualizaMotivoCancelamentoColetaVO
						.setDthrCancelamentoColeta(new Date());
			}

			if (oldSeqMotivosCancelaColetas != null
					&& newSeqMotivosCancelaColetas == null) {
				atualizaMotivoCancelamentoColetaVO
						.setMatriculaCancelamentoColeta(null);
				atualizaMotivoCancelamentoColetaVO
						.setVinCodigoCancelamentoColeta(null);
				atualizaMotivoCancelamentoColetaVO
						.setDthrCancelamentoColeta(null);
			}
		}

		return atualizaMotivoCancelamentoColetaVO;
	}

	/**
	 * ORADB Procedure ABSK_SHE_RN.RN_SHEP_DEL_UOH_PROT
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void deletaMpaUsoOrdHemoterapiaSolicitacaoHemoterapica(
			Integer atdSeq, Integer seq) throws ApplicationBusinessException {
		// PROCEDURE RN_SHEP_DEL_UOH_PROT
		// (P_OLD_ATD_SEQ IN VARCHAR2
		// ,P_OLD_SEQ IN VARCHAR2
		// )
		// IS
		// BEGIN
		// -- Deleta o registro correspondente na tabela
		// mpa_uso_ord_hemoterapias
		// DECLARE
		// regra_negocio EXCEPTION;
		// PRAGMA EXCEPTION_INIT(regra_negocio, -20000);
		//
		// CURSOR uoh(c_she_atd_seq mpa_uso_ord_hemoterapias.she_atd_seq%type,
		// c_she_seq mpa_uso_ord_hemoterapias.she_seq%type)
		// IS
		// SELECT seq, uoh_seq, ume_usp_apa_atd_seq,
		// ume_usp_apa_seq, ume_usp_vpa_pta_seq,
		// ume_usp_vpa_seqp, ume_usp_seq,
		// ume_cam_cit_vpa_pta_seq, ume_cam_cit_vpa_seqp,
		// ume_cam_cit_seqp, ume_cam_seqp
		// FROM mpa_uso_ord_hemoterapias
		// WHERE she_atd_seq = c_she_atd_seq
		// AND she_seq = c_she_seq;
		// --
		// CURSOR c_uoh_uso (c_ume_usp_apa_atd_seq
		// mpa_uso_ord_hemoterapias.ume_usp_apa_atd_seq%type,
		// c_ume_usp_apa_seq mpa_uso_ord_hemoterapias.ume_usp_apa_seq%type,
		// c_ume_usp_vpa_pta_seq
		// mpa_uso_ord_hemoterapias.ume_usp_vpa_pta_seq%type,
		// c_ume_usp_vpa_seqp mpa_uso_ord_hemoterapias.ume_usp_vpa_seqp%type,
		// c_ume_usp_seq mpa_uso_ord_hemoterapias.ume_usp_seq%type,
		// c_ume_cam_cit_vpa_pta_seq
		// mpa_uso_ord_hemoterapias.ume_cam_cit_vpa_pta_seq%type,
		// c_ume_cam_cit_vpa_seqp
		// mpa_uso_ord_hemoterapias.ume_cam_cit_vpa_seqp%type,
		// c_ume_cam_cit_seqp mpa_uso_ord_hemoterapias.ume_cam_cit_seqp%type,
		// c_ume_cam_seqp mpa_uso_ord_hemoterapias.ume_cam_seqp%type,
		// c_seq mpa_uso_ord_hemoterapias.seq%type) IS
		// SELECT seq
		// FROM mpa_uso_ord_hemoterapias
		// WHERE ume_usp_apa_atd_seq = c_ume_usp_apa_atd_seq
		// AND ume_usp_apa_seq = c_ume_usp_apa_seq
		// AND ume_usp_vpa_pta_seq = c_ume_usp_vpa_pta_seq
		// AND ume_usp_vpa_seqp = c_ume_usp_vpa_seqp
		// AND ume_usp_seq = c_ume_usp_seq
		// AND ume_cam_cit_vpa_pta_seq = c_ume_cam_cit_vpa_pta_seq
		// AND ume_cam_cit_vpa_seqp = c_ume_cam_cit_vpa_seqp
		// AND ume_cam_cit_seqp = c_ume_cam_cit_seqp
		// AND ume_cam_seqp = c_ume_cam_seqp
		// AND NVL(seq,0) <> c_seq;
		// --
		// v_outras_ordens NUMBER;
		// --
		// BEGIN
		// FOR r_uoh IN uoh(p_old_atd_seq, p_old_seq)
		// LOOP
		// OPEN c_uoh_uso (r_uoh.ume_usp_apa_atd_seq, r_uoh.ume_usp_apa_seq,
		// r_uoh.ume_usp_vpa_pta_seq, r_uoh.ume_usp_vpa_seqp,
		// r_uoh.ume_usp_seq, r_uoh.ume_cam_cit_vpa_pta_seq,
		// r_uoh.ume_cam_cit_vpa_seqp, r_uoh.ume_cam_cit_seqp,
		// r_uoh.ume_cam_seqp, r_uoh.seq);
		// FETCH c_uoh_uso INTO v_outras_ordens;
		// IF c_uoh_uso%notfound THEN
		// v_outras_ordens := 0;
		// END IF;
		// CLOSE c_uoh_uso;
		// IF v_outras_ordens = 0 THEN
		// BEGIN
		// UPDATE mpa_uso_medicacoes
		// SET ind_marcada = 'N'
		// WHERE usp_apa_atd_seq = r_uoh.ume_usp_apa_atd_seq
		// AND usp_apa_seq = r_uoh.ume_usp_apa_seq
		// AND usp_vpa_pta_seq = r_uoh.ume_usp_vpa_pta_seq
		// AND usp_vpa_seqp = r_uoh.ume_usp_vpa_seqp
		// AND usp_seq = r_uoh.ume_usp_seq
		// AND cam_cit_vpa_pta_seq = r_uoh.ume_cam_cit_vpa_pta_seq
		// AND cam_cit_vpa_seqp = r_uoh.ume_cam_cit_vpa_seqp
		// AND cam_cit_seqp = r_uoh.ume_cam_cit_seqp
		// AND cam_seqp = r_uoh.ume_cam_seqp;
		// EXCEPTION
		// WHEN regra_negocio THEN
		// RAISE;
		// WHEN NO_DATA_FOUND THEN
		// NULL;
		// WHEN OTHERS THEN
		// raise_application_error (-20000,'MPA-00902#1'||SQLERRM);
		// END;
		// END IF;
		// -- ira deletar os registros da tabela mpa_uso_ord_item_hemoters
		// BEGIN
		// DELETE FROM mpa_uso_ord_item_hemoters
		// WHERE uoh_seq = r_uoh.seq;
		// EXCEPTION
		// WHEN REGRA_NEGOCIO THEN
		// RAISE;
		// WHEN NO_DATA_FOUND THEN
		// NULL;
		// WHEN OTHERS THEN
		// raise_application_error (-20000,'MPA-00904'||SQLERRM);
		// END;
		// -- ira deletar os registros da tabela mpa_uso_ord_nutricoes
		// BEGIN
		// -- antes de deletar verifica se a ordem esta autorelacionada
		// -- se estiver, reorganiza
		// UPDATE mpa_uso_ord_hemoterapias
		// SET uoh_seq = r_uoh.uoh_seq
		// WHERE uoh_seq = r_uoh.seq;
		// --
		// DELETE FROM mpa_uso_ord_hemoterapias
		// WHERE seq = r_uoh.seq;
		// EXCEPTION
		// WHEN REGRA_NEGOCIO THEN
		// RAISE;
		// WHEN NO_DATA_FOUND THEN
		// NULL;
		// WHEN OTHERS THEN
		// raise_application_error (-20000,'MPA-00903#1'||SQLERRM);
		// END;
		// END LOOP;
		// END;
		// END RN_SHEP_DEL_UOH_PROT;
	}

	/**
	 * ORADB: ABST_SHE_BRI TRIGGER de insert DA TABELA
	 * ABS_SOLICITACOES_HEMOTERAPICAS
	 */
	@SuppressWarnings("ucd")
	public void executarTriggerDeInsertSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica,
			String nomeMicrocomputador)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date criadoEm = new Date();

		Integer matriculaServidor = servidorLogado.getId().getMatricula();

		if (matriculaServidor == null) {
			throw new ApplicationBusinessException(
					SolicitacaoHemoterapicaRNExceptionCode.ABS_00166);
		}

		// ABSK_SHE_RN.RN_SHEP_VER_PRESCR
		this.verificaPrescricaoSolicitacaoHemoterapica(solicitacaoHemoterapica.getPrescricaoMedica()
				.getAtendimento().getSeq(), solicitacaoHemoterapica
				.getDthrSolicitacao(), solicitacaoHemoterapica.getDthrFim(),
				criadoEm, solicitacaoHemoterapica.getIndPendente(), "I", nomeMicrocomputador, 
				new Date());

		// ABSK_SHE_RN.RN_SHEP_VER_ATENDIM
		this.verificaAtendimentoSolicitacaoHemoterapica(solicitacaoHemoterapica.getPrescricaoMedica()
				.getAtendimento().getSeq());

		// ABSK_SHE_RN.RN_SHEP_VER_DATAS
		this.verificaDatasSolicitacaoHemoterapica(null,
				solicitacaoHemoterapica.getDthrSolicitacao(), null,
				solicitacaoHemoterapica.getDthrFim());
	}

	/**
	 * ORADB ABST_SHE_BRU
	 * 
	 * Implementação da trigger de before UPDATE da tabela
	 * ABS_SOLICITACOES_HEMOTERAPICAS
	 * 
	 * @param newSolicitacaoHemoterapica
	 * @param oldSolicitacaoHemoterapica
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.NPathComplexity", "ucd"})
	public void preUpdateSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas newSolicitacaoHemoterapica,
			AbsSolicitacoesHemoterapicas oldSolicitacaoHemoterapica, 
			String nomeMicrocomputador)
			throws ApplicationBusinessException {
		if (newSolicitacaoHemoterapica.getDthrFim() != null
				&& CoreUtil
						.modificados(
								newSolicitacaoHemoterapica.getDthrFim(),
								oldSolicitacaoHemoterapica != null ? oldSolicitacaoHemoterapica
										.getDthrFim() : null)) {
			newSolicitacaoHemoterapica.setAlteradoEm(new Date());
		}

		if ((newSolicitacaoHemoterapica != null && oldSolicitacaoHemoterapica != null)
				&& ((newSolicitacaoHemoterapica == null && oldSolicitacaoHemoterapica != null)
						|| (newSolicitacaoHemoterapica != null && oldSolicitacaoHemoterapica == null)
						|| CoreUtil.modificados(newSolicitacaoHemoterapica
								.getId().getAtdSeq(),
								oldSolicitacaoHemoterapica.getId().getAtdSeq())
						|| CoreUtil.modificados(newSolicitacaoHemoterapica
								.getId().getSeq(), oldSolicitacaoHemoterapica
								.getId().getSeq())
						|| CoreUtil
								.modificados(newSolicitacaoHemoterapica
										.getDthrSolicitacao(),
										oldSolicitacaoHemoterapica
												.getDthrSolicitacao())
						|| CoreUtil.modificados(newSolicitacaoHemoterapica
								.getIndPacTransplantado(),
								oldSolicitacaoHemoterapica
										.getIndPacTransplantado())
						|| CoreUtil.modificados(
								newSolicitacaoHemoterapica.getJustificativa(),
								oldSolicitacaoHemoterapica.getJustificativa()) || CoreUtil
						.modificados(
								newSolicitacaoHemoterapica.getObservacao(),
								oldSolicitacaoHemoterapica.getObservacao()))) {
			this.verificaIndPendenteSolicitacaoHemoterapica(newSolicitacaoHemoterapica
					.getIndPendente());
		}

		if (newSolicitacaoHemoterapica.getMotivoCancelaColeta() == null) {
			this.verificaAtendimentoSolicitacaoHemoterapica(oldSolicitacaoHemoterapica != null
					&& oldSolicitacaoHemoterapica.getId() != null ? oldSolicitacaoHemoterapica
					.getId().getAtdSeq() : null);
		}

		// Verifica a existencia de uma prescrição para esta solicitação.
		if (!CoreUtil.modificados(
				oldSolicitacaoHemoterapica.getIndSituacaoColeta(),
				newSolicitacaoHemoterapica.getIndSituacaoColeta())) {
			this.verificaPrescricaoSolicitacaoHemoterapica(
					newSolicitacaoHemoterapica.getId().getAtdSeq(),
					newSolicitacaoHemoterapica.getDthrSolicitacao(),
					newSolicitacaoHemoterapica.getDthrFim(),
					newSolicitacaoHemoterapica.getCriadoEm(),
					newSolicitacaoHemoterapica.getIndPendente(), "", nomeMicrocomputador,
					new Date() );

			this.getPrescricaoMedicaFacade().verificaPrescricaoMedicaUpdate(
					newSolicitacaoHemoterapica.getId().getAtdSeq(),
					newSolicitacaoHemoterapica.getDthrSolicitacao(), null,
					newSolicitacaoHemoterapica.getCriadoEm(),
					newSolicitacaoHemoterapica.getIndPendente(), "U", nomeMicrocomputador, new Date());
		}

		this.atualizaMotivoCancelamentoColetaSolicitacaoHemoterapica(
				newSolicitacaoHemoterapica.getId().getAtdSeq(),
				newSolicitacaoHemoterapica.getId().getSeq(),
				oldSolicitacaoHemoterapica.getMotivoCancelaColeta() != null ? oldSolicitacaoHemoterapica
						.getMotivoCancelaColeta().getSeq() : null,
				newSolicitacaoHemoterapica.getMotivoCancelaColeta() != null ? newSolicitacaoHemoterapica
						.getMotivoCancelaColeta().getSeq() : null,
				oldSolicitacaoHemoterapica.getIndSituacaoColeta(),
				newSolicitacaoHemoterapica.getIndSituacaoColeta(),
				oldSolicitacaoHemoterapica.getIndResponsavelColeta(),
				newSolicitacaoHemoterapica.getIndResponsavelColeta(),
				newSolicitacaoHemoterapica.getServidorCancelaColeta() != null
						&& newSolicitacaoHemoterapica
								.getServidorCancelaColeta().getId() != null ? newSolicitacaoHemoterapica
						.getServidorCancelaColeta().getId().getMatricula()
						: null,
				newSolicitacaoHemoterapica.getServidorCancelaColeta() != null
						&& newSolicitacaoHemoterapica
								.getServidorCancelaColeta().getId() != null ? newSolicitacaoHemoterapica
						.getServidorCancelaColeta().getId().getVinCodigo()
						: null, newSolicitacaoHemoterapica
						.getDthrCancelamentoColeta());
	}

	/**
	 * ORADB ABST_SHE_ARU
	 * 
	 * ON ABS_SOLICITACOES_HEMOTERAPICAS
	 */
	@SuppressWarnings("ucd")
	public void executarTriggerAposUpdadeDeSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solHemNew,
			AbsSolicitacoesHemoterapicas solHemOld,
			DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		// absK_she.push_she_row
		// Nao implementada pois a enforce de update está comentada.

		this.getSolicitacaoHemoterapicaJournalRN()
				.realizarSolicitacaoHemoterapicaJournal(solHemNew, solHemOld,
						DominioOperacoesJournal.UPD);
	}

	/**
	 * ORADB ABST_SHE_BRD
	 * 
	 * Implementação da trigger de before DELETE da tabela
	 * ABS_SOLICITACOES_HEMOTERAPICAS.
	 * 
	 * Só pode ser excluída se ind_pendente = 'P'.
	 * 
	 * @param newSolicitacoesHemoterapicas
	 * @param oldSolicitacoesHemoterapicas
	 * @throws ApplicationBusinessException
	 */
	public void preRemoveSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacoesHemoterapicas)
			throws ApplicationBusinessException {

		SolicitacaoHemoterapicaVO solicitacaoHemoterapicaVO = new SolicitacaoHemoterapicaVO();
		solicitacaoHemoterapicaVO = getAbsSolicitacoesHemoterapicasDAO()
				.obtemSolicitacaoHemoterapicaVO(
						solicitacoesHemoterapicas.getPrescricaoMedica().getId()
								.getAtdSeq(),
						solicitacoesHemoterapicas.getId().getSeq());

		if (solicitacaoHemoterapicaVO != null) {
			if (DominioIndPendenteItemPrescricao.N
					.equals(solicitacaoHemoterapicaVO.getIndPendente())) {
				this.verificaDelecaoSolicitacaoHemoterapica();
			}

			if (solicitacaoHemoterapicaVO.getAtdSeq() != null
					&& solicitacaoHemoterapicaVO.getSeq() != null) {
				this.deletaMpaUsoOrdHemoterapiaSolicitacaoHemoterapica(
						solicitacaoHemoterapicaVO.getAtdSeq(),
						solicitacaoHemoterapicaVO.getSeq());
			}
		}
	}

	/**
	 * ORADB ABST_SHE_ARU
	 * 
	 * ON ABS_SOLICITACOES_HEMOTERAPICAS
	 */
	public void executarTriggerAposUpdadeDeSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solHemNew)
			throws ApplicationBusinessException {
		// absK_she.push_she_row
		// Nao implementada pois a enforce de update está comentada.

		SolicitacaoHemoterapicaVO solicitacaoHemoterapicaVO = new SolicitacaoHemoterapicaVO();
		solicitacaoHemoterapicaVO = getAbsSolicitacoesHemoterapicasDAO()
				.obtemSolicitacaoHemoterapicaVO(
						solHemNew.getPrescricaoMedica().getId().getAtdSeq(),
						solHemNew.getId().getSeq());

		AbsSolicitacoesHemoterapicas solHemOld = new AbsSolicitacoesHemoterapicas();

		// Setar atributos da VO no Objeto
		solHemOld.setPrescricaoMedica(solHemNew.getPrescricaoMedica());
		solHemOld.setId(new AbsSolicitacoesHemoterapicasId(null,
				solicitacaoHemoterapicaVO.getSeq()));
		solHemOld.setDthrCancelamentoColeta(solicitacaoHemoterapicaVO
				.getDthrCancelamentoColeta());
		solHemOld.setMotivoCancelaColeta(new AbsMotivosCancelaColetas(
				solicitacaoHemoterapicaVO.getMotivoCancelaColetaSeq(), null,
				null, null, null, null));
		solHemOld.setServidorCancelaColeta(new RapServidores(
				new RapServidoresId(solicitacaoHemoterapicaVO
						.getMatriculaCancelaColeta(), solicitacaoHemoterapicaVO
						.getVinCodigoCancelaColeta())));
		solHemOld.setIndSituacaoColeta(solicitacaoHemoterapicaVO
				.getIndSituacaoColeta());
		solHemOld.setIndResponsavelColeta(solicitacaoHemoterapicaVO
				.getIndResponsavelColeta());

		this.getSolicitacaoHemoterapicaJournalRN()
				.realizarSolicitacaoHemoterapicaJournal(solHemNew, solHemOld,
						DominioOperacoesJournal.UPD);
	}

	protected AbsSolicitacoesHemoterapicasDAO getAbsSolicitacoesHemoterapicasDAO() {
		return absSolicitacoesHemoterapicasDAO;
	}

	protected AbsMotivosCancelaColetasDAO getAbsMotivosCancelaColetasDAO() {
		return absMotivosCancelaColetasDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	protected SolicitacaoHemoterapicaJournalRN getSolicitacaoHemoterapicaJournalRN() {
		return solicitacaoHemoterapicaJournalRN;
	}

	/**
	 * Remove todas as solicitações hemoterápicas de um atendimento.
	 * 
	 * @param atendimento
	 */
	public void excluirSolicitacoesHemoterapicasPorAtendimeto(
			AghAtendimentos atendimento) {
		AbsSolicitacoesHemoterapicasDAO dao = getAbsSolicitacoesHemoterapicasDAO();

		List<AbsSolicitacoesHemoterapicas> solicitacoesHemoterapicasDoAtendimento = dao
				.buscarSolicitacoesHemoterapicasPorAtendimento(atendimento);

		for (AbsSolicitacoesHemoterapicas solicitacao : solicitacoesHemoterapicasDoAtendimento) {
			dao.remover(solicitacao);
		}

	}
	
	/**
	 * ORADB: Procedure AGHK_ATD_RN.RN_ATDP_CANC_AMOSTRA
	 * 
	 * @param atendimento
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException 
	 */
	public void cancelaAmostrasHemoterapicas(final AghAtendimentos atendimento, final AinTiposAltaMedica tipoAltaMedica, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		AbsMotivosCancelaColetas motivosCancelaColetas = null;
		
		//parametro P_COD_TIPO_ALTA_OBITO
		AghParametrosVO tipoAltaObito = new AghParametrosVO();
		tipoAltaObito.setNome(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO.toString());
		getParametroFacade().getAghpParametro(tipoAltaObito);

		//parametro P_COD_TIPO_ALTA_OBITO_MAIS_48H
		AghParametrosVO tipoAltaObitoMaisHoras = new AghParametrosVO();
		tipoAltaObitoMaisHoras.setNome(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H.toString());
		getParametroFacade().getAghpParametro(tipoAltaObitoMaisHoras);	
		
		//parametro P_COD_TIPO_ALTA_NORMAL
		AghParametrosVO tipoAltaNormal = new AghParametrosVO();
		tipoAltaNormal.setNome(AghuParametrosEnum.P_COD_TIPO_ALTA_NORMAL.toString());
		getParametroFacade().getAghpParametro(tipoAltaNormal);	
		
		//parametro P_MCC_CANCELA_ALTA
		AghParametrosVO cancelaAlta = new AghParametrosVO();
		cancelaAlta.setNome(AghuParametrosEnum.P_MCC_CANCELA_ALTA.toString());
		getParametroFacade().getAghpParametro(cancelaAlta);	
		
		//parametro P_MCC_CANCELA_OBITO
		AghParametrosVO cancelaObito = new AghParametrosVO();
		cancelaObito.setNome(AghuParametrosEnum.P_MCC_CANCELA_OBITO.toString());
		getParametroFacade().getAghpParametro(cancelaObito);
		
		if (cancelaObito.getVlrNumerico() != null && (tipoAltaMedica.getCodigo().equals(tipoAltaObito.getVlrTexto())
				|| tipoAltaMedica.getCodigo().equals(tipoAltaObitoMaisHoras.getVlrTexto()))) {
			
			motivosCancelaColetas = getAbsMotivosCancelaColetasDAO().obterAbsMotivosCancelaColetas(cancelaObito.getVlrNumerico().shortValue());
			
		} else if (cancelaObito.getVlrNumerico() != null && tipoAltaMedica.getCodigo().equals(tipoAltaNormal.getVlrTexto())) {
			
			motivosCancelaColetas = getAbsMotivosCancelaColetasDAO().obterAbsMotivosCancelaColetas(cancelaAlta.getVlrNumerico().shortValue());
			
		}
		
		if (motivosCancelaColetas != null) {
			
			List<AbsSolicitacoesHemoterapicas> solicitacoesHemoterapicasDoAtendimento = getAbsSolicitacoesHemoterapicasDAO().buscarSolicitacoesHemoterapicasEmColeta(atendimento);
			
			for (AbsSolicitacoesHemoterapicas solicitacoesHemoterapicas : solicitacoesHemoterapicasDoAtendimento) {
				
				solicitacoesHemoterapicas.setMotivoCancelaColeta(motivosCancelaColetas);
				solicitacoesHemoterapicas.setIndSituacaoColeta(DominioSituacaoColeta.C);
				atualizarSolicitacaoHemoterapica(solicitacoesHemoterapicas, nomeMicrocomputador);
				
			}
			
		}
		
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	@SuppressWarnings("ucd")
	public boolean verificarExisteItemSolHemoterapica(Integer codigoPaciente) {

		AghParametros parametroBancoDeSangue = null;
		try {
			parametroBancoDeSangue = getParametroFacade()
					.obterAghParametro(
							AghuParametrosEnum.P_AGHU_DATA_IMPLANTACAO_SISTEMA_BANCO_DE_SANGUE);
		} catch (ApplicationBusinessException e) {
			// Este catch serve apenas para continuar o método (o parâmetro não
			// é obrigatório)
			super.logWarn("Parametro P_AGHU_DATA_IMPLANTACAO_SISTEMA_BANCO_DE_SANGUE nao encontrado");
		}

		Date dataBancoSangue = null;
		if (parametroBancoDeSangue != null
				&& parametroBancoDeSangue.getVlrData() != null) {
			dataBancoSangue = parametroBancoDeSangue.getVlrData();
		}

		if (dataBancoSangue != null) {
			if (this.getVAbsMovimentoComponenteDAO()
					.verificarExisteHemoterapiasPaciente(codigoPaciente,
							dataBancoSangue)) {
				return true;
			}
		}

		if (getAbsItensSolHemoterapicasDAO()
				.verificarExisteItensComponentesSanguineosPescricao(
						codigoPaciente, dataBancoSangue)) {
			return true;
		}

		if (getAbsItensSolHemoterapicasDAO()
				.verificarExisteItensProcedimentosPrescricao(codigoPaciente)) {
			return true;
		}

		return false;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "unchecked"})
	public List<VAbsMovimentoComponente> pesquisarItensSolHemoterapicasPOL(
			Integer codigoPaciente){
		
		AghParametros parametroBancoDeSangue = null;
		try {
			parametroBancoDeSangue = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_AGHU_DATA_IMPLANTACAO_SISTEMA_BANCO_DE_SANGUE);	
		} catch(ApplicationBusinessException e) {
			//Este catch serve apenas para continuar o método (o parâmetro não é obrigatório)
			super.logWarn("Parametro P_AGHU_DATA_IMPLANTACAO_SISTEMA_BANCO_DE_SANGUE nao encontrado");
		}
		
		Date dataBancoSangue = null;
		if (parametroBancoDeSangue != null && parametroBancoDeSangue.getVlrData() != null){
			dataBancoSangue = parametroBancoDeSangue.getVlrData();
		}
		
		List<VAbsMovimentoComponente> listaVAbsMovimentoComponente = new ArrayList<VAbsMovimentoComponente>();
		if (dataBancoSangue != null) {
			//Adiciona os componentes do banco de sangue à lista
			listaVAbsMovimentoComponente = this.getVAbsMovimentoComponenteDAO().listarHemoterapiasPaciente(
					codigoPaciente, dataBancoSangue);	
			//Seta a origem dos dados
			for (VAbsMovimentoComponente itemBancoSangue: listaVAbsMovimentoComponente){
				itemBancoSangue.setOrigemDados("Banco de Sangue");
				itemBancoSangue.setDescricaoComponenteProcedimento(itemBancoSangue.getComponenteSanguineo().getDescricao());
				
				if (StringUtils.isNotBlank(itemBancoSangue.getIndIrradiado())){
					if ("S".equalsIgnoreCase(itemBancoSangue.getIndIrradiado())){
						itemBancoSangue.setIndIrradiado(SIM);											
					}
					else{
						itemBancoSangue.setIndIrradiado(NAO);	
					}
				}

				if (StringUtils.isNotBlank(itemBancoSangue.getIndFiltrado())){
					if ("S".equalsIgnoreCase(itemBancoSangue.getIndFiltrado())){
						itemBancoSangue.setIndFiltrado(SIM);											
					}
					else{
						itemBancoSangue.setIndFiltrado(NAO);	
					}
				}
				
				if (StringUtils.isNotBlank(itemBancoSangue.getIndLavado())){
					if ("S".equalsIgnoreCase(itemBancoSangue.getIndLavado())){
						itemBancoSangue.setIndLavado(SIM);											
					}
					else{
						itemBancoSangue.setIndLavado(NAO);	
					}
				}
			}
		}
				
		List<AbsItensSolHemoterapicas> listaItensSolHemoterapicas = new ArrayList<AbsItensSolHemoterapicas>();
		
		//Adiciona os componentes à lista
		listaItensSolHemoterapicas.addAll(getAbsItensSolHemoterapicasDAO()
			.pesquisarItensComponentesSanguineosPrescricaoPOL(codigoPaciente,
					dataBancoSangue));
		
		//Adiciona os procedimentos à lista
		listaItensSolHemoterapicas.addAll(getAbsItensSolHemoterapicasDAO()
				.pesquisarItensProcedimentosPrescricaoPOL(codigoPaciente));
	
		//Adapta os itens aos objetos VAbsMovimentoComponente
		for (AbsItensSolHemoterapicas item: listaItensSolHemoterapicas){
			VAbsMovimentoComponente vAbsMovimentoComponente = new VAbsMovimentoComponente();
			VAbsMovimentoComponenteId id = new VAbsMovimentoComponenteId();
			id.setDthrMovimento(item.getSolicitacaoHemoterapica().getDthrSolicitacao());
			vAbsMovimentoComponente.setId(id);
			if (item.getComponenteSanguineo() != null){
				vAbsMovimentoComponente.setDescricaoComponenteProcedimento(item.getComponenteSanguineo().getDescricao());		
			}
			else{
				if (item.getProcedHemoterapico() != null){
					vAbsMovimentoComponente.setDescricaoComponenteProcedimento(item.getProcedHemoterapico().getDescricao());						
				}
			}
			vAbsMovimentoComponente.setQtdeUnidades(item.getQtdeUnidades());
			vAbsMovimentoComponente.setQtdeMl(item.getQtdeMl());
			if (item.getSolicitacaoHemoterapica().getDthrValida() != null){
				vAbsMovimentoComponente.getId().setDthrMovimento(item.getSolicitacaoHemoterapica().getDthrValida());					
			}
			else{
				vAbsMovimentoComponente.getId().setDthrMovimento(item.getSolicitacaoHemoterapica().getDthrSolicitacao());	
			}
			if (item.getComponenteSanguineo() != null){
				if (item.getIndIrradiado()){
					vAbsMovimentoComponente.setIndIrradiado(SIM);					
				}
				else{
					vAbsMovimentoComponente.setIndIrradiado(NAO);
				}
				if (item.getIndFiltrado()){
					vAbsMovimentoComponente.setIndFiltrado(SIM);		
				}
				else{
					vAbsMovimentoComponente.setIndFiltrado(NAO);
				}
				if (item.getIndLavado()){
					vAbsMovimentoComponente.setIndLavado(SIM);
				}
				else{
					vAbsMovimentoComponente.setIndLavado(NAO);
				}
			}
			//Seta a origem dos dados
			vAbsMovimentoComponente.setOrigemDados("Prescrição Médica");
			listaVAbsMovimentoComponente.add(vAbsMovimentoComponente);
		}
		
		final BeanComparator nomeSorter = new BeanComparator("id.dthrMovimento", new ReverseComparator(new NullComparator(false)));
		Collections.sort(listaVAbsMovimentoComponente, nomeSorter);
		
		return listaVAbsMovimentoComponente;
	}

	protected VAbsMovimentoComponenteDAO getVAbsMovimentoComponenteDAO() {
		return vAbsMovimentoComponenteDAO;
	}
	
	protected AbsItensSolHemoterapicasDAO getAbsItensSolHemoterapicasDAO() {
		return absItensSolHemoterapicasDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
