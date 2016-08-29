package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSinonimoProcedCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDiagTerapDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.faturamento.business.IFaturamentoBeanFacade;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcSinonimoProcCirg;
import br.gov.mec.aghu.model.MbcSinonimoProcCirgId;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Implementação das regras de negócio da package Oracle MBCK_PCI_RN, procedures e triggers
 * relacionadas a Procedimentos Cirúrgicos (MbcProcedimentoCirurgicos).
 * 
 * @author dpacheco
 */
@Stateless
public class ProcedimentoCirurgicoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ProcedimentoCirurgicoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSinonimoProcedCirurgicoDAO mbcSinonimoProcedCirurgicoDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private PdtProcDiagTerapDAO pdtProcDiagTerapDAO;


	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade iBlocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IFaturamentoBeanFacade iFaturamentoBeanFacade;

	@EJB
	private SinonimoRN sinonimoRN;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5334874730234624871L;
	
	private static final Integer TAMANHO_MAXIMO_DESCRICAO_PROCEDIMENTO = 99;
	
	public enum ProcedimentoCirurgicoRNExceptionCode implements	BusinessExceptionCode {
		MBC_00006, MBC_003411, MBC_00342, MBC_00345, MBC_00346, PDT_00119, PDT_00120
	}
	
	/**
	 * Insere instância de MbcProcedimentoCirurgicos.
	 * 
	 * @param procedimentoCirurgico
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	public void inserirProcedimentoCirurgico(
			MbcProcedimentoCirurgicos newProcedimentoCirurgico
			)
			throws BaseException {
		executarAntesDeInserir(newProcedimentoCirurgico);
		getMbcProcedimentoCirurgicoDAO().persistir(newProcedimentoCirurgico);
		executarEnforce(null, newProcedimentoCirurgico,	DominioOperacaoBanco.INS);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_PCI_BRI
	 * 
	 * @param newProcedimentoCirurgico
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesDeInserir(MbcProcedimentoCirurgicos newProcedimentoCirurgico) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		newProcedimentoCirurgico.setCriadoEm(new Date());
		
		/* Atualiza servidor que incluiu registro */
		newProcedimentoCirurgico.setServidor(servidorLogado);
		
		/* Verifica tempo minimo das unidades, tempo mínimo do procedimento deve ser maior */
		verificarTempoMinimoUnidadesFuncionais(newProcedimentoCirurgico.getTempoMinimo());
	}
	
	/**
	 * Atualiza instância de MbcProcedimentoCirurgicos.
	 * 
	 * @param newProcedimentoCirurgico
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	public void atualizarProcedimentoCirurgico(
			MbcProcedimentoCirurgicos newProcedimentoCirurgico
			)
			throws BaseException {
		MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO = getMbcProcedimentoCirurgicoDAO();
		MbcProcedimentoCirurgicos oldProcedimentoCirurgico = mbcProcedimentoCirurgicoDAO.obterOriginal(newProcedimentoCirurgico);
		executarAntesAtualizar(oldProcedimentoCirurgico, newProcedimentoCirurgico);
		mbcProcedimentoCirurgicoDAO.atualizar(newProcedimentoCirurgico);
		executarEnforce(oldProcedimentoCirurgico, newProcedimentoCirurgico,	DominioOperacaoBanco.UPD);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_PCI_BRU
	 * 
	 * @param newProcedimentoCirurgico
	 * @param oldProcedimentoCirurgico
	 * @param servidorLogado
	 */
	private void executarAntesAtualizar(MbcProcedimentoCirurgicos oldProcedimentoCirurgico, 
			MbcProcedimentoCirurgicos newProcedimentoCirurgico) throws ApplicationBusinessException {
		
		 /* Os atributos abaixo não podem ser alterados */
		 if (CoreUtil.modificados(newProcedimentoCirurgico.getSeq(), oldProcedimentoCirurgico.getSeq())
				 || CoreUtil.modificados(newProcedimentoCirurgico.getDescricao(), oldProcedimentoCirurgico.getDescricao())
				 || CoreUtil.modificados(newProcedimentoCirurgico.getCriadoEm(), oldProcedimentoCirurgico.getCriadoEm())) {
			 restringirAlteracaoProcedimento();
		 }
		
		/* Atualiza servidor que incluiu registro */
		newProcedimentoCirurgico.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		/* Verifica tempo minimo das unidades, tempo mínimo do procedimento deve ser maior*/
		verificarTempoMinimoUnidadesFuncionais(newProcedimentoCirurgico.getTempoMinimo());
	}

	/**
	 * Procedure
	 * 
	 * ORADB: MBCP_ENFORCE_PCI_RULES
	 * 
	 * @param oldProcedimentoCirurgico
	 * @param newProcedimentoCirurgico
	 * @param operacaoBanco
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	public void executarEnforce(
			MbcProcedimentoCirurgicos oldProcedimentoCirurgico,
			MbcProcedimentoCirurgicos newProcedimentoCirurgico,
			DominioOperacaoBanco operacaoBanco)
			throws BaseException {

		if (DominioOperacaoBanco.INS.equals(operacaoBanco)) {
			inserirProcedimentoHospitalarInterno(newProcedimentoCirurgico);

			// Inclui sinonimo do procedimento na tabela de sinonimos seqp=1
			atualizarSinonimo(newProcedimentoCirurgico.getSeq(),
					newProcedimentoCirurgico.getDescricao(),
					newProcedimentoCirurgico.getIndSituacao());

			// Qdo tipo = 2, inserir na tabela pdt_proc_diag_teraps
			inserirProcedimentoDiagnosticoTerapeutico(newProcedimentoCirurgico);
		}

		if (DominioOperacaoBanco.UPD.equals(operacaoBanco)) {
			if (CoreUtil.modificados(newProcedimentoCirurgico.getIndSituacao(),
					oldProcedimentoCirurgico.getIndSituacao())) {
				atualizarProcedimentoHospitalarInternoSituacao(
						newProcedimentoCirurgico);
				/*
				 * Altera situação dos sinonimos DO procedimento e altera
				 * situacao do procedimento no PDT(Proc Diagnóstico Terapêutico)
				 */
				atualizarSituacaoSinonimosProcedimentoDiagTerap(
						newProcedimentoCirurgico);
			}

			if (CoreUtil.modificados(newProcedimentoCirurgico.getDescricao(),
					oldProcedimentoCirurgico.getDescricao())) {
				atualizarProcedimentoHospitalarInternoDescricao(
						newProcedimentoCirurgico);
			}

			// Ao alterar o tipo para 2(Proc Diag Terap), inserir na
			// pdt_proc_diag_teraps
			if (CoreUtil.modificados(newProcedimentoCirurgico.getTipo(),
					oldProcedimentoCirurgico.getTipo())) {
				inserirProcedimentoDiagnosticoTerapeutico(
						newProcedimentoCirurgico);
			}

			if (CoreUtil.modificados(newProcedimentoCirurgico.getDescricao(),
					oldProcedimentoCirurgico.getDescricao())
					|| CoreUtil.modificados(newProcedimentoCirurgico.getTipo(),
							oldProcedimentoCirurgico.getTipo())
					|| CoreUtil.modificados(
							newProcedimentoCirurgico.getIndContaminacao(),
							oldProcedimentoCirurgico.getIndContaminacao())
					|| CoreUtil.modificados(
							newProcedimentoCirurgico.getTempoMinimo(),
							oldProcedimentoCirurgico.getTempoMinimo())) {
				atualizarProcedimentoDiagnosticoTerapeutico(newProcedimentoCirurgico, oldProcedimentoCirurgico);
			}
		}
	}
	

	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_INS_PHI
	 * 
	 * @param procedimentoCirurgico
	 * @param descricao
	 * @param situacao
	 * @throws ApplicationBusinessException
	 */
	private void inserirProcedimentoHospitalarInterno(
			MbcProcedimentoCirurgicos procedimentoCirurgico)
			throws ApplicationBusinessException {
		String descricao = procedimentoCirurgico.getDescricao();
		if (descricao.length() > TAMANHO_MAXIMO_DESCRICAO_PROCEDIMENTO) {
			descricao = descricao.substring(0, TAMANHO_MAXIMO_DESCRICAO_PROCEDIMENTO);
		}
		getFaturamentoBeanFacade().inserirProcedimentoHospitalarInterno(null,
				procedimentoCirurgico, null, null, null, descricao,
				procedimentoCirurgico.getIndSituacao(), null, null, null, null);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_SIT_PHI
	 * 
	 * @param procedimentoCirurgico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarProcedimentoHospitalarInternoSituacao(
			MbcProcedimentoCirurgicos procedimentoCirurgico
			)
			throws ApplicationBusinessException, ApplicationBusinessException {
		getFaturamentoBeanFacade()
				.atualizarProcedimentoHospitalarInternoSituacao(null,
						procedimentoCirurgico, null, null, null,
						procedimentoCirurgico.getIndSituacao(), null, null,
						null, null);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_INATIVA
	 * 
	 * @param procedimentoCirurgico
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void atualizarSituacaoSinonimosProcedimentoDiagTerap(
			MbcProcedimentoCirurgicos procedimentoCirurgico
			) throws BaseException {
		Integer pciSeq = procedimentoCirurgico.getSeq();
		DominioSituacao situacao = procedimentoCirurgico.getIndSituacao();

		/*
		 * Ao atualizar a situação de um procedimento, atualizar também a
		 * situação de seus sinônimos
		 */
		List<MbcSinonimoProcCirg> listaSinonimoProcCirg = getMbcSinonimoProcedCirurgicoDAO()
				.pesquisarSinonimosPorPciSeq(pciSeq);
		SinonimoRN sinonimoRN = getSinonimoRN();
		try {
			for (MbcSinonimoProcCirg sinonimoProcCirg : listaSinonimoProcCirg) {
				sinonimoProcCirg.setSituacao(situacao);
				sinonimoRN.atualizar(sinonimoProcCirg);
			}
		} catch (BaseException e) {
			logError(e);
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoRNExceptionCode.MBC_00346, e);
		} catch (PersistenceException e) {
			logError(e);
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoRNExceptionCode.MBC_00346, e);
		}

		// Altera situacao do proc diag terapêutico qdo tipo = 2
		if (DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO
				.equals(procedimentoCirurgico.getTipo())) {
			IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();
			
			List<PdtProcDiagTerap> listaProcDiagTerap = getPdtProcDiagTerapDAO().pesquisarProcDiagTerapPorPciSeq(pciSeq);
			try {
				for (PdtProcDiagTerap procDiagTerap : listaProcDiagTerap) {
					procDiagTerap.setSituacao(situacao);
					blocoCirurgicoProcDiagTerapFacade.atualizarProcDiagTerap(procDiagTerap);
				}
			} catch (PersistenceException e) {
				logError(e);
				throw new ApplicationBusinessException(
						ProcedimentoCirurgicoRNExceptionCode.PDT_00119, e);
			}
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_SINONIMO
	 * 
	 * @param pciSeq
	 * @param descricao
	 * @param situacao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void atualizarSinonimo(Integer pciSeq, String descricao,
			DominioSituacao situacao) throws BaseException {
		MbcProcedimentoCirurgicos procedimentoCirurgico = getMbcProcedimentoCirurgicoDAO()
				.obterPorChavePrimaria(pciSeq);

		/* Inserir um sinonimo com descrição igual ao procedimento incluido */
		MbcSinonimoProcCirgId sinonimoProcCirgId = new MbcSinonimoProcCirgId(
				pciSeq, Short.valueOf("1"));
		MbcSinonimoProcCirg sinonimoProcCirg = new MbcSinonimoProcCirg();
		sinonimoProcCirg.setId(sinonimoProcCirgId);
		sinonimoProcCirg.setAlteradoPor(null);
		sinonimoProcCirg.setDescricao(descricao);
		sinonimoProcCirg.setCriadoEm(null);
		sinonimoProcCirg.setSituacao(situacao);
		sinonimoProcCirg.setMbcProcedimentoCirurgicos(procedimentoCirurgico);

		try {
			getSinonimoRN().inserir(sinonimoProcCirg);
		} catch (PersistenceException pe) {
			logError(pe);
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoRNExceptionCode.MBC_00345, pe);
		}
	}
	

	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_INS_PDT
	 * 
	 * @param procedimentoCirurgico
	 * @param servidorLogado
	 */
	private void inserirProcedimentoDiagnosticoTerapeutico(
			MbcProcedimentoCirurgicos procedimentoCirurgico
			) {
		
		IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();
		
		Long countProcDiagTerap = getPdtProcDiagTerapDAO()
				.obterCountPdtProcDiagTerapPorPciSeq(procedimentoCirurgico
						.getSeq());

		if (DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO
				.equals(procedimentoCirurgico.getTipo())
				&& countProcDiagTerap == 0) {
			PdtProcDiagTerap procDiagTerap = new PdtProcDiagTerap();
			procDiagTerap.setDescricao(procedimentoCirurgico.getDescricao());
			procDiagTerap.setContaminacao(procedimentoCirurgico
					.getIndContaminacao());
			procDiagTerap
					.setTempoMinimo(procedimentoCirurgico.getTempoMinimo());
			procDiagTerap.setExame(null);
			procDiagTerap.setProcedimentoCirurgico(procedimentoCirurgico);
			procDiagTerap.setSituacao(procedimentoCirurgico.getIndSituacao());
			procDiagTerap.setCriadoEm(procedimentoCirurgico.getCriadoEm());
			procDiagTerap.setServidor(procedimentoCirurgico.getServidor());
			blocoCirurgicoProcDiagTerapFacade.inserirProcDiagTerap(
					procDiagTerap);
		}
	}	
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_DESC_PHI
	 * 
	 * @param procedimentoCirurgico
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void atualizarProcedimentoHospitalarInternoDescricao(
			MbcProcedimentoCirurgicos procedimentoCirurgico
			) throws ApplicationBusinessException,
			ApplicationBusinessException {

		getFaturamentoBeanFacade()
				.atualizarProcedimentoHospitalarInternoDescricao(null,
						procedimentoCirurgico, null, null, null,
						procedimentoCirurgico.getDescricao(), null, null, null,
						null);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_ATU_ALT_PDT 
	 * 
	 * @param newProcedimentoCirurgico
	 * @param oldProcedimentoCirurgico
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void atualizarProcedimentoDiagnosticoTerapeutico(
			MbcProcedimentoCirurgicos newProcedimentoCirurgico,
			MbcProcedimentoCirurgicos oldProcedimentoCirurgico
			)
			throws ApplicationBusinessException {

		IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();

		try {
			if (DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO
					.equals(newProcedimentoCirurgico.getTipo())) {

				List<PdtProcDiagTerap> listaProcDiagTerap = getPdtProcDiagTerapDAO()
						.pesquisarProcDiagTerapPorPciSeq(newProcedimentoCirurgico
								.getSeq());
				for (PdtProcDiagTerap procDiagTerap : listaProcDiagTerap) {
					procDiagTerap.setDescricao(newProcedimentoCirurgico
							.getDescricao());
					procDiagTerap.setContaminacao(newProcedimentoCirurgico
							.getIndContaminacao());
					procDiagTerap.setTempoMinimo(newProcedimentoCirurgico
							.getTempoMinimo());
					procDiagTerap.setSituacao(newProcedimentoCirurgico
							.getIndSituacao());
					blocoCirurgicoProcDiagTerapFacade.atualizarProcDiagTerap(procDiagTerap);
				}
			} else if (DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO
					.equals(oldProcedimentoCirurgico.getTipo())
					&& !DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO
							.equals(newProcedimentoCirurgico.getTipo())) {

				List<PdtProcDiagTerap> listaProcDiagTerap = getPdtProcDiagTerapDAO()
						.pesquisarProcDiagTerapPorPciSeq(newProcedimentoCirurgico
								.getSeq());
				for (PdtProcDiagTerap procDiagTerap : listaProcDiagTerap) {
					procDiagTerap.setSituacao(DominioSituacao.I);
					blocoCirurgicoProcDiagTerapFacade.atualizarProcDiagTerap(procDiagTerap);
				}
			}
		} catch (PersistenceException e) {
			logError(e);
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoRNExceptionCode.PDT_00120, e);
		}
	}		
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_VER_TEMPO_MI
	 * 
	 * @param novoTempoMinimo
	 * @throws ApplicationBusinessException 
	 */
	private void verificarTempoMinimoUnidadesFuncionais(Short novoTempoMinimo) throws ApplicationBusinessException {
		/*
		 * O tempo mínimo do procedimento cirúrgico não pode ser menor que o
		 * tempo mínimo cadastral da unidade funcional. Obs.: não foi utilizado
		 * aqui a function migrada como aghc_ver_caract_unf(...) por motivo de
		 * performance (seria necessario um Full Scan na tabela
		 * agh_unidades_funcionais para depois aplicar essa function).
		 */
		Short menorTempoUnidFuncCirurgias = getAghuFacade()
				.obterMenorTempoMinimoUnidadeFuncionalCirurgia(
						ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS);
		if(menorTempoUnidFuncCirurgias == null){
			throw new ApplicationBusinessException(ProcedimentoCirurgicoRNExceptionCode.MBC_003411);
		}else if (novoTempoMinimo < menorTempoUnidFuncCirurgias) {
			throw new ApplicationBusinessException(ProcedimentoCirurgicoRNExceptionCode.MBC_00342);
		}
	}
	
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PCI_RN.RN_PCIP_VER_ALTERA
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void restringirAlteracaoProcedimento() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(ProcedimentoCirurgicoRNExceptionCode.MBC_00006);
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	
	protected MbcSinonimoProcedCirurgicoDAO getMbcSinonimoProcedCirurgicoDAO() {
		return mbcSinonimoProcedCirurgicoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected IFaturamentoBeanFacade getFaturamentoBeanFacade() {
		return this.iFaturamentoBeanFacade;
	}

	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return iBlocoCirurgicoProcDiagTerapFacade;
	}	
	
	protected SinonimoRN getSinonimoRN() {
		return sinonimoRN;
	}

	protected PdtProcDiagTerapDAO getPdtProcDiagTerapDAO(){
		return pdtProcDiagTerapDAO;
	}
}
