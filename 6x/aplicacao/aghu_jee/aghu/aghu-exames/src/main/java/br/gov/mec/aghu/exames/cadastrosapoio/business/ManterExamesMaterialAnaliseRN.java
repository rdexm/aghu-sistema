package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelCopiaResultadosDAO;
import br.gov.mec.aghu.exames.dao.AelExameConselhoProfsDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDependentesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMatAnaliseJnDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelOrdExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelCopiaResultados;
import br.gov.mec.aghu.model.AelCopiaResultadosId;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.AelExameForaAgh;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.AelExamesMatAnaliseJn;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelGrupoExamePrioriza;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelProjetoExame;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnidExameSignificativo;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author lalegre
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class ManterExamesMaterialAnaliseRN extends BaseBusiness {

	@EJB
	private ManterTipoAmostraExameRN manterTipoAmostraExameRN;
	
	private static final Log LOG = LogFactory.getLog(ManterExamesMaterialAnaliseRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	
	@Inject
	private AelExameConselhoProfsDAO aelExameConselhoProfsDAO;
	
	@Inject
	private AelIntervaloColetaDAO aelIntervaloColetaDAO;
	
	@Inject
	private AelExamesMatAnaliseJnDAO aelExamesMatAnaliseJnDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelOrdExameMatAnaliseDAO aelOrdExameMatAnaliseDAO;
	
	@Inject
	private AelCopiaResultadosDAO aelCopiaResultadosDAO;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@Inject
	private AelExamesDependentesDAO aelExamesDependentesDAO;
	
	@Inject
	private AelResultadosPadraoDAO aelResultadosPadraoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5644965260779284112L;

	public enum ManterExamesMaterialAnaliseRNExceptionCode implements
			BusinessExceptionCode {

		AEL_00360, AEL_00362, AEL_00363, AEL_00364, AEL_00369, AEL_01096, AEL_00432, AEL_01214, AEL_01661, CHK_MCO_RESULTADO_EXAME_SIGNIFS, CHK_AEL_UNF_EXECUTA_EXAMES, CHK_AEL_GRUPO_RECOMENDACAO_EXAMES, CHK_AEL_ORD_EXAME_MAT_ANALISES, CHK_AEL_TIPOS_AMOSTRA_EXAMES, CHK_AEL_GRUPO_EXAME_PRIORIZAS, CHK_AEL_EXAMES_LIMITADO_ATEND, CHK_AEL_EXAMES_NOTIFICACAO, CHK_AEL_UNID_EXAME_SIGNIFICATIVOS, CHK_AEL_EXAME_FORA_AGHS, CHK_AEL_VERSAO_LAUDOS, CHK_AEL_RESULTADOS_PADROES, CHK_AEL_PROJETO_EXAMES, ERRO_INTEGRIDADE_REFERENCIAL, AEL_EMA_CK1, AEL_EMA_CK19, AEL_EMA_CK2, AEL_EMA_CK20, AEL_EMA_CK3, AEL_EMA_CK4, AEL_EMA_CK5, AEL_EMA_CK6, AEL_EMA_CK28, AEL_EMA_CK29, CHK_FAT_PROCED_HOSP_INTERNOS, FRM_40600, MENSAGEM_ERRO_MATERIAL_ANALISE;

	}

	/**
	 * Inserção em AelExamesMaterialAnalise
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void inserirAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		preInserirAelExamesMaterialAnalise(aelExamesMaterialAnalise, null);
		getAelExamesMaterialAnaliseDAO().persistir(aelExamesMaterialAnalise);
		getAelExamesMaterialAnaliseDAO().flush();
		posInserirAelExamesMaterialAnalise(aelExamesMaterialAnalise);

	}

	protected void validaDuplicadosListaTiposAmostraExame(
			List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {

		if (listaTiposAmostraExame == null || listaTiposAmostraExame.isEmpty()) {
			return;
		}

		for (AelTipoAmostraExame e1 : listaTiposAmostraExame) {
			for (AelTipoAmostraExame e2 : listaTiposAmostraExame) {
				if (e1 != e2
						&& e1.getMaterialAnalise().getSeq()
								.equals(e2.getMaterialAnalise().getSeq())
						&& e1.getOrigemAtendimento().equals(
								e2.getOrigemAtendimento())) {
					throw new ApplicationBusinessException(
							ManterExamesMaterialAnaliseRNExceptionCode.FRM_40600,
							e1.getOrigemAtendimento().getDescricao(), e1
									.getMaterialAnalise().getDescricao());
				}
			}
		}
	}

	/**
	 * 
	 * @param aelExamesMaterialAnalise
	 * @param listaTiposAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void inserirAelExamesMaterialAnaliseTipoAmostraExame(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {

		// Pré
		preInserirAelExamesMaterialAnalise(aelExamesMaterialAnalise,
				listaTiposAmostraExame);

		for (AelTipoAmostraExame itemAelTipoAmostraExame : listaTiposAmostraExame) {
			itemAelTipoAmostraExame
					.setExamesMaterialAnalise(aelExamesMaterialAnalise);
			getManterTipoAmostraExameRN().preInserirAelTipoAmostraExame(
					itemAelTipoAmostraExame, listaTiposAmostraExame);
		}

		// Persistir
		getAelExamesMaterialAnaliseDAO().persistir(aelExamesMaterialAnalise);
		getAelExamesMaterialAnaliseDAO().flush();
		for (AelTipoAmostraExame itemAelTipoAmostraExame : listaTiposAmostraExame) {
			getManterTipoAmostraExameRN().inserirAelTipoAmostraExame(
					itemAelTipoAmostraExame);
		}

		// Pós
		posInserirAelExamesMaterialAnalise(aelExamesMaterialAnalise);

	}

	/**
	 * Update em AelExamesMaterialAnalise
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void atualizarAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			AelExamesMaterialAnalise aelExamesMaterialAnaliseAux)
			throws ApplicationBusinessException {
		preAtualizarAelExamesMaterialAnalise(aelExamesMaterialAnalise, null);
		getAelExamesMaterialAnaliseDAO().atualizar(aelExamesMaterialAnalise);
		getAelExamesMaterialAnaliseDAO().flush();
		posAtualizaAelExamesMaterialAnalise(aelExamesMaterialAnalise,
				aelExamesMaterialAnaliseAux);
	}

	/**
	 * 
	 * @param aelExamesMaterialAnalise
	 * @param aelExamesMaterialAnaliseAux
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAelExamesMaterialAnaliseTiposAmostraExame(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			AelExamesMaterialAnalise aelExamesMaterialAnaliseAux,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {

		// Pré
		// AelExamesMaterialAnalise maOld =
		// getAelExamesMaterialAnaliseDAO().buscarAelExamesMaterialAnalisePorId(aelExamesMaterialAnalise.getAelExames().getSigla(),
		// aelExamesMaterialAnalise.getAelMateriaisAnalises().getSeq());
		preAtualizarAelExamesMaterialAnalise(aelExamesMaterialAnalise,
				listaTiposAmostraExame);
		for (AelTipoAmostraExame itemAelTipoAmostraExame : listaTiposAmostraExame) {
			if (itemAelTipoAmostraExame.getId() == null) {
				itemAelTipoAmostraExame
						.setExamesMaterialAnalise(aelExamesMaterialAnalise);
				getManterTipoAmostraExameRN().preInserirAelTipoAmostraExame(
						itemAelTipoAmostraExame, listaTiposAmostraExame);
			} else {
				getManterTipoAmostraExameRN().preAtualizarAelTipoAmostraExame(
						itemAelTipoAmostraExame);
			}
		}

		// Persistir
		getAelExamesMaterialAnaliseDAO().merge(aelExamesMaterialAnalise);
		getAelExamesMaterialAnaliseDAO().flush();
		for (AelTipoAmostraExame itemAelTipoAmostraExame : listaTiposAmostraExame) {
			if (itemAelTipoAmostraExame.getId() == null) {
				getManterTipoAmostraExameRN().inserirAelTipoAmostraExame(
						itemAelTipoAmostraExame);
			} else {
				getManterTipoAmostraExameRN().atualizarAelTipoAmostraExame(
						itemAelTipoAmostraExame);
			}
		}

		// Pós
		posAtualizaAelExamesMaterialAnalise(aelExamesMaterialAnalise,
				aelExamesMaterialAnaliseAux);
	}

	/**
	 * @param AelExamesMaterialAnalise
	 * @throws BaseException
	 */
	public void removerAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws BaseException {
		
		aelExamesMaterialAnalise = getAelExamesMaterialAnaliseDAO().obterPorChavePrimaria(aelExamesMaterialAnalise.getId());
		preRemoverAelExamesMaterialAnalise(aelExamesMaterialAnalise);
		getAelExamesMaterialAnaliseDAO().remover(aelExamesMaterialAnalise);
		getAelExamesMaterialAnaliseDAO().flush();

	}

	/**
	 * ORADB AELT_EMA_BRI
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInserirAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (aelExamesMaterialAnalise.getAelMateriaisAnalises() == null
				|| aelExamesMaterialAnalise.getAelMateriaisAnalises().getSeq() == null) {

			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.MENSAGEM_ERRO_MATERIAL_ANALISE);

		}

		this.validaDuplicadosListaTiposAmostraExame(listaTiposAmostraExame);

		aelExamesMaterialAnalise.setCriadoEm(new Date());
		aelExamesMaterialAnalise.setServidor(
				servidorLogado);

		this.validarAelExamesMaterialAnalise(aelExamesMaterialAnalise);

		this.verificarIntegridadeReferencial(aelExamesMaterialAnalise);
		this.verificarMaterialAnalise(aelExamesMaterialAnalise);
		this.verificarTiposAmostraExame(aelExamesMaterialAnalise,
				listaTiposAmostraExame);
		this.verificarMaterialColetavel(aelExamesMaterialAnalise);
		this.verificarDependentes(aelExamesMaterialAnalise);
		this.verificarIntervaloCadastrado(aelExamesMaterialAnalise);

	}

	/**
	 * ORADB AELT_EMA_BRU
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws BaseException
	 */
	public void preAtualizarAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {

		if (aelExamesMaterialAnalise.getAelMateriaisAnalises() == null
				|| aelExamesMaterialAnalise.getAelMateriaisAnalises().getSeq() == null) {

			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.MENSAGEM_ERRO_MATERIAL_ANALISE);

		}

		this.validaDuplicadosListaTiposAmostraExame(listaTiposAmostraExame);

		this.verificarMaterialColetavel(aelExamesMaterialAnalise);
		this.validarAelExamesMaterialAnalise(aelExamesMaterialAnalise);

		this.validarDesativacaoMaterialExame(aelExamesMaterialAnalise);
		this.verificarTiposAmostraExame(aelExamesMaterialAnalise,
				listaTiposAmostraExame);

		this.verificarDependentes(aelExamesMaterialAnalise);
		this.verificarTornaNaoDependente(aelExamesMaterialAnalise);
		this.verificarAlteracoesMaterial(aelExamesMaterialAnalise);
		this.validarDesativacaoMaterialExame(aelExamesMaterialAnalise);
		this.verificarIntervaloCadastrado(aelExamesMaterialAnalise);
	}

	/**
	 * Pré-remover
	 * 
	 * @param aelExames
	 * @throws BaseException
	 */
	public void preRemoverAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws BaseException {

		validarDependencias(aelExamesMaterialAnalise);

		removerExameConselho(aelExamesMaterialAnalise);
		removerCopiaResultado(aelExamesMaterialAnalise);
		removerFatProcedHospInternos(aelExamesMaterialAnalise);
		removerAelOrdExameMatAnalise(aelExamesMaterialAnalise);
	}

	/**
	 * ORADB AELT_EMA_ASI - AELP_ENFORCE_EMA_RULES
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void posInserirAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		this.inserirExameConselho(aelExamesMaterialAnalise);
		this.inserirCopiaResultado(aelExamesMaterialAnalise);
		this.inserirFatProcedHospInternos(aelExamesMaterialAnalise);
		this.inserirAelOrdExameMatAnalise(aelExamesMaterialAnalise);

	}

	/**
	 * ORADB TRIGGERS AELT_EMA_ARU
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void posAtualizaAelExamesMaterialAnalise(
			AelExamesMaterialAnalise materialNew,
			AelExamesMaterialAnalise materialOld)
			throws ApplicationBusinessException {

		atualizarIndSumarioSemMascara(materialNew, materialOld);

		inserirAelExamesMaterialAnaliseJn(materialOld);

	}

	private void atualizarIndSumarioSemMascara(
			AelExamesMaterialAnalise materialNew,
			AelExamesMaterialAnalise materialOld) {

		if ((materialOld.getPertenceSumario().equals(DominioSumarioExame.B)
				|| materialOld.getPertenceSumario().equals(
						DominioSumarioExame.E)
				|| materialOld.getPertenceSumario().equals(
						DominioSumarioExame.G)
				|| materialOld.getPertenceSumario().equals(
						DominioSumarioExame.H) || materialOld
				.getPertenceSumario().equals(DominioSumarioExame.S))
				&& materialNew.getPertenceSumario().equals(
						DominioSumarioExame.N)) {

			atualizarCamposLaudo(materialNew, Boolean.FALSE);

		}
		if ((materialOld.getPertenceSumario().equals(DominioSumarioExame.B)
				|| materialOld.getPertenceSumario().equals(
						DominioSumarioExame.E)
				|| materialOld.getPertenceSumario().equals(
						DominioSumarioExame.G)
				|| materialOld.getPertenceSumario().equals(
						DominioSumarioExame.H) || materialOld
				.getPertenceSumario().equals(DominioSumarioExame.N))
				&& materialNew.getPertenceSumario().equals(
						DominioSumarioExame.S)) {
			atualizarCamposLaudo(materialNew, Boolean.TRUE);

		}

	}

	/**
	 * oradb atu_campos_laudo
	 * 
	 * @param materialNew
	 * @param sumarioSemMascara
	 */
	private void atualizarCamposLaudo(AelExamesMaterialAnalise materialNew,
			Boolean sumarioSemMascara) {

		List<AelParametroCamposLaudo> aelParametroCamposLaudos = getAelParametroCamposLaudoDAO()
				.buscarAelParametroCamposLaudoComVersaoLaudoAtivaPorAelExamesMaterialAnalise(
						materialNew);
		for (AelParametroCamposLaudo campo : aelParametroCamposLaudos) {
			campo.setSumarioSemMascara(sumarioSemMascara);
			getAelParametroCamposLaudoDAO().atualizar(campo);
			getAelParametroCamposLaudoDAO().flush();
		}

	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void inserirAelExamesMaterialAnaliseJn(
			AelExamesMaterialAnalise materialOld) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExamesMatAnaliseJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.UPD, AelExamesMatAnaliseJn.class, servidorLogado.getUsuario());

		jn.setExaSigla(materialOld.getAelExames().getSigla());
		jn.setIndCci(DominioSimNao.getInstance(materialOld.getIndCci())
				.toString());
		jn.setIndComedi(DominioSimNao.getInstance(materialOld.getIndComedi())
				.toString());
		jn.setIndDependente(DominioSimNao.getInstance(
				materialOld.getIndDependente()).toString());
		jn.setIndDietaDiferenciada(DominioSimNao.getInstance(
				materialOld.getIndDietaDiferenciada()).toString());
		jn.setIndExigeRegiaoAnatomica(DominioSimNao.getInstance(
				materialOld.getIndExigeRegiaoAnatomica()).toString());
		jn.setIndFormaRespiracao(DominioSimNao.getInstance(
				materialOld.getIndFormaRespiracao()).toString());
		jn.setIndGeraItemPorColetas(DominioSimNao.getInstance(
				materialOld.getIndGeraItemPorColetas()).toString());
		jn.setIndImpTicketPaciente(DominioSimNao.getInstance(
				materialOld.getIndImpTicketPaciente()).toString());
		jn.setIndJejum(DominioSimNao.getInstance(materialOld.getIndJejum())
				.toString());
		jn.setIndLimitaSolic(DominioSimNao.getInstance(
				materialOld.getIndLimitaSolic()).toString());
		jn.setIndNpo(DominioSimNao.getInstance(materialOld.getIndNpo())
				.toString());
		jn.setIndPermiteSolicAlta((materialOld.getIndPermiteSolicAlta() == null) ? null
				: materialOld.getIndPermiteSolicAlta().toString());
		jn.setIndPertenceContador(DominioSimNao.getInstance(
				materialOld.getIndPertenceContador()).toString());
		jn.setIndPreparo(DominioSimNao.getInstance(materialOld.getIndPreparo())
				.toString());
		jn.setIndSituacao(materialOld.getIndSituacao().toString());
		jn.setIndSolicInformaColetas(DominioSimNao.getInstance(
				materialOld.getIndSolicInformaColetas()).toString());
		jn.setIndTipoTelaLiberaResu(DominioSimNao.getInstance(
				materialOld.getIndTipoTelaLiberaResu()).toString());
		jn.setIndUsaIntervaloCadastrado(DominioSimNao.getInstance(
				materialOld.getIndUsaIntervaloCadastrado()).toString());
		jn.setIndVerificaMedicacao(DominioSimNao.getInstance(
				materialOld.getIndVerificaMedicacao()).toString());
		jn.setIntervaloMinTempoSolic(materialOld.getIntervaloMinTempoSolic());
		jn.setManSeq(materialOld.getId().getManSeq());
		jn.setNatureza(materialOld.getNatureza().getCodigo());
		jn.setNroAmostraDefault(materialOld.getNroAmostraDefault());
		jn.setNroAmostrasSolic(materialOld.getNroAmostrasSolic());
		jn.setNroAmostraTempo(materialOld.getNroAmostraTempo());
		jn.setPertenceSumario(materialOld.getPertenceSumario().toString());
		jn.setQtdeDiasValidade(materialOld.getQtdeDiasValidade());
		jn.setSerMatriculaAlterado(materialOld.getServidorAlterado().getId()
				.getMatricula());
		jn.setSerVinCodigoAlterado(materialOld.getServidorAlterado().getId()
				.getVinCodigo());
		jn.setTempoDiaAmostraDefault((materialOld.getTempoDiaAmostraDefault() == null) ? null
				: materialOld.getTempoDiaAmostraDefault().shortValue());
		jn.setTempoHoraAmostraDefault((materialOld.getTempoHoraAmostraDefault() == null) ? null
				: materialOld.getTempoHoraAmostraDefault());
		jn.setTempoJejumNpo(materialOld.getTempoJejumNpo());
		jn.setTempoLimitePeriodo(materialOld.getTempoLimitePeriodo());
		jn.setTempoLimiteSolic(materialOld.getTempoLimiteSolic());
		jn.setTempoMinParaAgenda(materialOld.getTempoMinParaAgenda());
		jn.setTempoSolicAlta(materialOld.getTempoSolicAlta());
		jn.setTempoSolicAltaCompl(materialOld.getTempoSolicAltaCompl());
		jn.setUnidTempoColetaAmostras((materialOld.getUnidTempoColetaAmostras() == null) ? null
				: materialOld.getUnidTempoColetaAmostras().toString());
		jn.setUnidTempoLimitePeriodo((materialOld.getUnidTempoLimitePeriodo() == null) ? null
				: materialOld.getUnidTempoLimitePeriodo().toString());
		jn.setUnidTempoLimiteSol((materialOld.getUnidTempoLimiteSol() == null) ? null
				: materialOld.getUnidTempoLimiteSol().toString());

		getAelExamesMatAnaliseJnDAO().persistir(jn);
		getAelExamesMatAnaliseJnDAO().flush();
	}

	/**
	 * ORADB aelk_ael_rn.rn_aelp_ver_man_ativ
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	protected void verificarMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		if (!aelExamesMaterialAnalise.getAelMateriaisAnalises()
				.getIndSituacao().equals(DominioSituacao.A)) {

			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_00362);

		}

	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_ver_mat_cole
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	protected void verificarMaterialColetavel(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		if (aelExamesMaterialAnalise.getAelMateriaisAnalises() != null
				&& !aelExamesMaterialAnalise.getAelMateriaisAnalises()
						.getIndColetavel()) {

			if (aelExamesMaterialAnalise.getIndSolicInformaColetas()) {

				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_00363);

			} else if (aelExamesMaterialAnalise.getIndUsaIntervaloCadastrado()) {

				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_00364);

			}

		}

	}

	/**
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	protected void verificarIntegridadeReferencial(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {
		if (getAelExamesMaterialAnaliseDAO().existeAelExamesMaterialAnalise(
				aelExamesMaterialAnalise.getAelExames().getSigla(),
				aelExamesMaterialAnalise.getAelMateriaisAnalises().getSeq())) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.ERRO_INTEGRIDADE_REFERENCIAL);
		}
	}

	/**
	 * Valida material de análise de exames coletáveis
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	public void verificarTiposAmostraExame(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			List<AelTipoAmostraExame> listaTiposAmostraExame)
			throws ApplicationBusinessException {

		if (aelExamesMaterialAnalise.getAelMateriaisAnalises()
				.getIndColetavel()) {

			if (listaTiposAmostraExame == null
					|| listaTiposAmostraExame.isEmpty()) {

				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_01661,
						aelExamesMaterialAnalise.getAelMateriaisAnalises()
								.getDescricao());

			}

		}
	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_ver_dependen
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	protected void verificarDependentes(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		if (aelExamesMaterialAnalise.getIndDependente()
				&& (aelExamesMaterialAnalise.getAelMateriaisAnalises() != null && aelExamesMaterialAnalise
						.getAelMateriaisAnalises().getIndExigeDescMatAnls())) {

			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_01096);

		}

	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_ver_upd_dept
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	protected void verificarTornaNaoDependente(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		if (!aelExamesMaterialAnalise.getIndDependente()) {
			List<AelExamesDependentes> examesDependentes = getAelExamesDependentesDAO()
					.obterAelExamesDependentesSiglaEhDependenteESeqEhDependente(
							aelExamesMaterialAnalise);
			if (examesDependentes != null && examesDependentes.size() > 0) {
				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_01214);
			}
		}
	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_ver_int_col
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	protected void verificarIntervaloCadastrado(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		if (!aelExamesMaterialAnalise.getIndUsaIntervaloCadastrado()) {

			if (getAelIntervaloColetaDAO()
					.existeItem(
							aelExamesMaterialAnalise.getAelExames().getSigla(),
							aelExamesMaterialAnalise.getAelMateriaisAnalises()
									.getSeq())) {

				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_00432);

			}

		}

	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_atu_conselho
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void inserirExameConselho(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		AelExameConselhoProfs aelExameConselhoProfs = new AelExameConselhoProfs();
		aelExameConselhoProfs
				.setExamesMaterialAnalise(aelExamesMaterialAnalise);

		AghParametros parametro = null;
		try {
			parametro = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_COD_CONSELHO_MED);
		} catch (ApplicationBusinessException e) {
			throw new IllegalArgumentException(
					"Parâmetro P_COD_CONSELHO_MED não encontrado.");
		}

		RapConselhosProfissionais conselhosProfissionais = getCadastrosBasicosFacade()
				.obterConselhoProfissional(
						parametro.getVlrNumerico().shortValue());
		aelExameConselhoProfs.setConselhosProfissionais(conselhosProfissionais);

		getExamesFacade().inserirAelExameConselhoProfs(aelExameConselhoProfs);

	}

	protected void removerExameConselho(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {
		getAelExameConselhoProfsDAO().removerAelExameConselhoProfsPorMaterial(
				aelExamesMaterialAnalise.getId());
	}

	protected void removerCopiaResultado(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		List<FatConvenioSaude> convenios = getFaturamentoFacade()
				.obterConveniosSaudeAtivos();

		for (FatConvenioSaude convenioSaude : convenios) {

			AelCopiaResultadosId id = new AelCopiaResultadosId();
			id.setEmaExaSigla(aelExamesMaterialAnalise.getId().getExaSigla());
			id.setEmaManSeq(aelExamesMaterialAnalise.getId().getManSeq());
			id.setCnvCodigo(convenioSaude.getCodigo());
			id.setOrigemAtendimento(DominioOrigemAtendimento.T);

			List<AelCopiaResultados> aelCopiaResultados = getAelCopiaResultadosDAO()
					.obterAelCopiaResultados(id);
			for (AelCopiaResultados resultados : aelCopiaResultados) {

				getAelCopiaResultadosDAO().remover(resultados);
				getAelCopiaResultadosDAO().flush();

			}

		}

	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_atu_copia_re
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void inserirCopiaResultado(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		List<FatConvenioSaude> convenios = getFaturamentoFacade()
				.obterConveniosSaudeAtivos();
		AghParametros parametro = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_EXAME_NUMERO_COPIAS);

		for (FatConvenioSaude convenioSaude : convenios) {

			AelCopiaResultadosId id = new AelCopiaResultadosId();
			id.setEmaExaSigla(aelExamesMaterialAnalise.getId().getExaSigla());
			id.setEmaManSeq(aelExamesMaterialAnalise.getId().getManSeq());
			id.setCnvCodigo(convenioSaude.getCodigo());
			id.setOrigemAtendimento(DominioOrigemAtendimento.T);

			AelCopiaResultados aelCopiaResultados = new AelCopiaResultados();
			aelCopiaResultados.setId(id);
			aelCopiaResultados
					.setExamesMaterialAnalise(aelExamesMaterialAnalise);
			aelCopiaResultados.setCriadoEm(new Date());
			aelCopiaResultados.setServidor(aelExamesMaterialAnalise
					.getServidor());
			aelCopiaResultados.setServidorAlterado(aelExamesMaterialAnalise
					.getServidor());
			if (parametro != null && parametro.getVlrNumerico() != null) {
				aelCopiaResultados.setNumero(parametro.getVlrNumerico()
						.byteValue());
			}

			getExamesFacade().inserirAelCopiaResultados(aelCopiaResultados);

		}

	}

	/**
	 * ORADB Constraints de AEL_EXAMES_MATERIAL_ANALISE:
	 * AEL_EMA_CK1,AEL_EMA_CK19
	 * ,AEL_EMA_CK2,AEL_EMA_CK20,AEL_EMA_CK3,AEL_EMA_CK4,
	 * AEL_EMA_CK5,AEL_EMA_CK6,AEL_EMA_CK28,AEL_EMA_CK29
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void validarAelExamesMaterialAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		Boolean indSolicInformaColetas = aelExamesMaterialAnalise
				.getIndSolicInformaColetas();
		DominioUnidTempo unidTempoColetaAmostras = aelExamesMaterialAnalise
				.getUnidTempoColetaAmostras();
		Boolean indNpo = aelExamesMaterialAnalise.getIndNpo();
		Boolean indJejum = aelExamesMaterialAnalise.getIndJejum();
		Short tempoJejumNpo = aelExamesMaterialAnalise.getTempoJejumNpo();
		Boolean indDietaDiferenciada = aelExamesMaterialAnalise
				.getIndDietaDiferenciada();
		Boolean indDependente = aelExamesMaterialAnalise.getIndDependente();
		Boolean indExigeRegiaoAnatomica = aelExamesMaterialAnalise
				.getIndExigeRegiaoAnatomica();
		Boolean indUsaIntervaloCadastrado = aelExamesMaterialAnalise
				.getIndUsaIntervaloCadastrado();
		Boolean indPermiteSolicAlta = aelExamesMaterialAnalise
				.getIndPermiteSolicAlta().isSim();
		Short intervaloMinTempoSolic = aelExamesMaterialAnalise
				.getIntervaloMinTempoSolic();
		Short qtdeDiasValidade = aelExamesMaterialAnalise.getQtdeDiasValidade();
		Short tempoMinParaAgenda = aelExamesMaterialAnalise
				.getTempoMinParaAgenda();
		Short tempoSolicAlta = aelExamesMaterialAnalise.getTempoSolicAlta();
		Short tempoSolicAltaCompl = aelExamesMaterialAnalise
				.getTempoSolicAltaCompl();

		// AEL_EMA_CK1
		if (!(!indSolicInformaColetas || (indSolicInformaColetas && unidTempoColetaAmostras != null))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK1);
		}

		// AEL_EMA_CK19
		if (!(((indNpo || indJejum) && tempoJejumNpo != null) || (!indNpo
				&& !indJejum && tempoJejumNpo == null))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK19);
		}

		// AEL_EMA_CK2
		if (!((indJejum && !indNpo && !indDietaDiferenciada)
				|| (!indJejum && indNpo && !indDietaDiferenciada)
				|| (!indJejum && !indNpo && indDietaDiferenciada) || (!indJejum
				&& !indNpo && !indDietaDiferenciada))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK2);
		}

		// AEL_EMA_CK20
		if (!((!indDependente || (indDependente && !indSolicInformaColetas
				&& !indExigeRegiaoAnatomica && !indUsaIntervaloCadastrado)))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK20);
		}

		// AEL_EMA_CK28
		if (!((!indPermiteSolicAlta && tempoSolicAlta == null) || (indPermiteSolicAlta && (tempoSolicAlta == null || tempoSolicAlta != null)))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK28);
		}

		// AEL_EMA_CK29
		if (!((!indPermiteSolicAlta && tempoSolicAltaCompl == null) || (indPermiteSolicAlta && (tempoSolicAltaCompl == null || tempoSolicAltaCompl != null)))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK29);
		}

		// AEL_EMA_CK3
		if (!(!indUsaIntervaloCadastrado || (indUsaIntervaloCadastrado && !indSolicInformaColetas))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK3);
		}

		// AEL_EMA_CK4
		if (!(intervaloMinTempoSolic == null || (intervaloMinTempoSolic != null && intervaloMinTempoSolic > 0))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK4);
		}

		// AEL_EMA_CK5
		if (!(qtdeDiasValidade == null || (qtdeDiasValidade != null && qtdeDiasValidade > 0))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK5);
		}

		// AEL_EMA_CK6
		if (!(tempoMinParaAgenda == null || (tempoMinParaAgenda != null && tempoMinParaAgenda > 0))) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.AEL_EMA_CK6);
		}
	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_atu_ph_inter
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	public FatProcedHospInternos inserirFatProcedHospInternos(AelExamesMaterialAnalise aelExamesMaterialAnalise) throws ApplicationBusinessException {

		FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
		fatProcedHospInternos.setDescricao(aelExamesMaterialAnalise.getAelExames().getDescricao());
		fatProcedHospInternos.setSituacao(DominioSituacao.A);
		fatProcedHospInternos.setEmaExaSigla(aelExamesMaterialAnalise.getAelExames());
		fatProcedHospInternos.setEmaManSeq(aelExamesMaterialAnalise.getAelMateriaisAnalises().getSeq());

		getFaturamentoFacade().inserirFatProcedHospInternos(fatProcedHospInternos);
		
		return fatProcedHospInternos;
	}

	protected void removerFatProcedHospInternos(
			AelExamesMaterialAnalise aelExamesMaterialAnalise)
			throws ApplicationBusinessException {

		FatProcedHospInternos fatProcedHospInternos = getFaturamentoFacade()
				.obterFatProcedHospInternosPorMaterial(
						aelExamesMaterialAnalise.getId());

		if (fatProcedHospInternos != null
				&& getFaturamentoFacade().existeProcedimentohospitalarInterno(
						fatProcedHospInternos, FatConvGrupoItemProced.class,
						FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO)) {
			throw new ApplicationBusinessException(
					ManterExamesMaterialAnaliseRNExceptionCode.CHK_FAT_PROCED_HOSP_INTERNOS);
		}

		getFaturamentoFacade().removerFatProcedHospInternosPorMaterial(
				aelExamesMaterialAnalise.getId());
	}

	/**
	 * ORADB aelk_ema_rn.rn_emap_atu_ord_exa
	 * 
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException  
	 */
	protected void inserirAelOrdExameMatAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise) throws ApplicationBusinessException {

		AelOrdExameMatAnalise aelOrdExameMatAnalise = new AelOrdExameMatAnalise();
		aelOrdExameMatAnalise
				.setExamesMaterialAnalise(aelExamesMaterialAnalise);

		// TODO usar aghParametros para buscar esses valores do banco. variáveis
		// estáticas não é uma solução aceitável.
		aelOrdExameMatAnalise.setOrdemNivel1(Short.valueOf("99"));
		aelOrdExameMatAnalise.setOrdemNivel2(Short.valueOf("0"));

		getExamesFacade().inserirAelOrdExameMatAnalise(aelOrdExameMatAnalise);

	}

	protected void removerAelOrdExameMatAnalise(
			AelExamesMaterialAnalise aelExamesMaterialAnalise) {

		getAelOrdExameMatAnaliseDAO().removerAelOrdExameMatAnalisePorMaterial(
				aelExamesMaterialAnalise.getId());

	}

	/**
	 * oradb aelk_ema_rn.rn_emap_ver_situacao
	 * 
	 * @param aelMaterialExame
	 * @throws BaseException
	 */
	protected void validarDesativacaoMaterialExame(
			AelExamesMaterialAnalise aelMaterialExame)
			throws ApplicationBusinessException {
		if (DominioSituacao.I.equals(aelMaterialExame.getIndSituacao())) {
			List<AelUnfExecutaExames> unidadesExecutoras = getAelExamesMaterialAnaliseDAO()
					.buscarAelExamesMaterialAelUnfExecutaExames(
							aelMaterialExame, DominioSituacao.A);
			if (unidadesExecutoras != null && unidadesExecutoras.size() > 0) {
				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_00360);
			}
		}
	}

	/**
	 * oradb aelk_ema_rn.rn_emap_ver_update, aelk_ema_rn.rn_emap_ver_int_col
	 * 
	 * @param aelMaterialExame
	 * @throws BaseException
	 */
	protected void verificarAlteracoesMaterial(
			AelExamesMaterialAnalise aelMaterialExame)
			throws ApplicationBusinessException {

		AelExamesMaterialAnalise exameAux = getAelExamesMaterialAnaliseDAO()
				.buscarAelExamesMaterialAnalisePorId(
						aelMaterialExame.getId().getExaSigla(),
						aelMaterialExame.getId().getManSeq());
		// O if se refere a aelk_ema_rn.rn_emap_ver_int_col
		if (aelMaterialExame.getIndUsaIntervaloCadastrado().equals(
				exameAux.getIndUsaIntervaloCadastrado())) {
			// aelk_ema_rn.rn_emap_ver_update
			if (!exameAux.getCriadoEm().equals(aelMaterialExame.getCriadoEm())
					|| !exameAux
							.getServidor()
							.getId()
							.getMatricula()
							.equals(aelMaterialExame.getServidor().getId()
									.getMatricula())
					|| !exameAux
							.getServidor()
							.getId()
							.getVinCodigo()
							.equals(aelMaterialExame.getServidor().getId()
									.getVinCodigo())) {
				throw new ApplicationBusinessException(
						ManterExamesMaterialAnaliseRNExceptionCode.AEL_00369);
			}
		} else {
			aelMaterialExame.getServidor().setPessoaFisica(
					getRegistroColaboradorFacade().obterRapPessoasFisicas(
							aelMaterialExame.getServidor().getId()));
		}
	}

	/**
	 * oradb Forms - CHK_AEL_EXAMES
	 * 
	 * @param aelMaterialExame
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void validarDependencias(AelExamesMaterialAnalise aelMaterialExame)
			throws BaseException {

		BaseListException listaException = new BaseListException();

		// Verifica a existência de registros em outras entidades
		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								McoResultadoExameSignifs.class,
								McoResultadoExameSignifs.Fields.EMA_EXA_SIGLA,
								McoResultadoExameSignifs.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_MCO_RESULTADO_EXAME_SIGNIFS));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelUnfExecutaExames.class,
								AelUnfExecutaExames.Fields.EMA_EXA_SIGLA,
								AelUnfExecutaExames.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_UNF_EXECUTA_EXAMES));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelGrupoRecomendacaoExame.class,
								AelGrupoRecomendacaoExame.Fields.EMA_EXA_SIGLA,
								AelGrupoRecomendacaoExame.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_GRUPO_RECOMENDACAO_EXAMES));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelOrdExameMatAnalise.class,
								AelOrdExameMatAnalise.Fields.EMA_EXA_SIGLA,
								AelOrdExameMatAnalise.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_ORD_EXAME_MAT_ANALISES));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelTipoAmostraExame.class,
								AelTipoAmostraExame.Fields.EMA_EXA_SIGLA,
								AelTipoAmostraExame.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_TIPOS_AMOSTRA_EXAMES));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelGrupoExamePrioriza.class,
								AelGrupoExamePrioriza.Fields.EMA_EXA_SIGLA,
								AelGrupoExamePrioriza.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_GRUPO_EXAME_PRIORIZAS));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelExamesLimitadoAtend.class,
								AelExamesLimitadoAtend.Fields.EMA_EXA_SIGLA,
								AelExamesLimitadoAtend.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_EXAMES_LIMITADO_ATEND));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelExamesNotificacao.class,
								AelExamesNotificacao.Fields.EMA_EXA_SIGLA,
								AelExamesNotificacao.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_EXAMES_NOTIFICACAO));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelUnidExameSignificativo.class,
								AelUnidExameSignificativo.Fields.EMA_EXA_SIGLA,
								AelUnidExameSignificativo.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_UNID_EXAME_SIGNIFICATIVOS));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelExameForaAgh.class,
								AelExameForaAgh.Fields.EMA_EXA_SIGLA,
								AelExameForaAgh.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_EXAME_FORA_AGHS));

		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelVersaoLaudo.class,
								AelVersaoLaudo.Fields.EMA_EXA_SIGLA,
								AelVersaoLaudo.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_VERSAO_LAUDOS));

		final boolean chkAelResultadosPadroes = this.getAelResultadosPadraoDAO().verificarOcorrenciaPadraoCampoPorExameMaterial(aelMaterialExame.getId().getExaSigla(), aelMaterialExame.getId().getManSeq());
		if(chkAelResultadosPadroes){
			listaException.add(new ApplicationBusinessException(ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_RESULTADOS_PADROES));
		}
		
		listaException
				.add(this
						.existeItem(
								aelMaterialExame,
								AelProjetoExame.class,
								AelProjetoExame.Fields.EMA_EXA_SIGLA,
								AelProjetoExame.Fields.EMA_MAN_SEQ,
								ManterExamesMaterialAnaliseRNExceptionCode.CHK_AEL_PROJETO_EXAMES));

		if (listaException.hasException()) {
			throw listaException;
		}
	}

	/**
	 * oradb CHK_AEL_EXAMES
	 * 
	 * @param aelMaterialExame
	 * @param class1
	 * @param field
	 * @param exceptionCode
	 * @return
	 */
	private ApplicationBusinessException existeItem(
			AelExamesMaterialAnalise aelMaterialExame, Class class1,
			Enum field, Enum seq, BusinessExceptionCode exceptionCode) {
		final boolean isExisteOcorrencia = getAelExamesMaterialAnaliseDAO()
				.existeItem(aelMaterialExame, class1, field, seq);
		if (isExisteOcorrencia) {
			return new ApplicationBusinessException(exceptionCode);
		}
		return null;
	}

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	protected AelIntervaloColetaDAO getAelIntervaloColetaDAO() {
		return aelIntervaloColetaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	

	protected AelExamesDependentesDAO getAelExamesDependentesDAO() {
		return aelExamesDependentesDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected AelExamesMatAnaliseJnDAO getAelExamesMatAnaliseJnDAO() {
		return aelExamesMatAnaliseJnDAO;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}

	protected ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelExameConselhoProfsDAO getAelExameConselhoProfsDAO() {
		return aelExameConselhoProfsDAO;
	}

	protected AelCopiaResultadosDAO getAelCopiaResultadosDAO() {
		return aelCopiaResultadosDAO;
	}

	protected AelOrdExameMatAnaliseDAO getAelOrdExameMatAnaliseDAO() {
		return aelOrdExameMatAnaliseDAO;
	}

	protected ManterTipoAmostraExameRN getManterTipoAmostraExameRN() {
		return manterTipoAmostraExameRN;
	}
	
	protected AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}