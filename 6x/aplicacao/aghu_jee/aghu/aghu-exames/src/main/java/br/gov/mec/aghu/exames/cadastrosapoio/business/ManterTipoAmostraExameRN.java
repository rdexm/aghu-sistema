package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameJnDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelTipoAmostraExameJn;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterTipoAmostraExameRN extends BaseBusiness{

	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory.getLog(ManterTipoAmostraExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTipoAmostraExameJnDAO aelTipoAmostraExameJnDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5116194557740645261L;

	public enum ManterTipoAmostraExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00392,AEL_00402,AEL_00394,AEL_00361,AEL_00397,AEL_00403,AEL_00404,AEL_00406,
		AEL_00837,AEL_00839,AEL_00840,AEL_00369,AEL_00417;
	}
	
	/*
	 * Triggers...
	 */
	
	/**
	 * ORADB AELT_TAE_BRI
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInserirAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {
		this.preInserirAelTipoAmostraExame(tipoAmostraExame, null);
	}
	
	/**
	 * ORADB AELT_TAE_BRI
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInserirAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame, List<AelTipoAmostraExame> listaTiposAmostraExame) throws ApplicationBusinessException {
		this.atualizarServidor(tipoAmostraExame);
		this.atualizarServidorAlterado(tipoAmostraExame);
		this.verificarRecipiente(tipoAmostraExame);
		this.verificarAnticoagulante(tipoAmostraExame);
		this.verificarMaterialAnalise(tipoAmostraExame);
		this.verificarUnidadeFuncional(tipoAmostraExame);
		this.verificarNumeroAmostra(tipoAmostraExame);
		if(listaTiposAmostraExame != null){
			this.verificarOrigemAtendimento(tipoAmostraExame, listaTiposAmostraExame);	
		} else{
			this.verificarOrigemAtendimento(tipoAmostraExame);
		}
		
	}
	
	/**
	 * Inserir AelTipoAmostraExame
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {
		this.validaParametroObrigatorio(tipoAmostraExame);
		this.preInserirAelTipoAmostraExame(tipoAmostraExame);
		this.getAelTipoAmostraExameDAO().persistir(tipoAmostraExame);
		this.getAelTipoAmostraExameDAO().flush();
	}
	
	/**
	 * ORADB AELT_TAE_BRU e TRIGGER AELT_TAE_ASU (AELP_ENFORCE_TAE_RULES)
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void preAtualizarAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {
	
		this.getAelTipoAmostraExameDAO().desatachar(tipoAmostraExame);
		AelTipoAmostraExame auxAelTipoAmostraExame = getAelTipoAmostraExameDAO().obterPorChavePrimaria(tipoAmostraExame.getId());
		this.getAelTipoAmostraExameDAO().desatachar(auxAelTipoAmostraExame);
		
		
		// ORADB aelk_tae_rn.rn_taep_ver_update
		boolean isServidorModificado = false;
		isServidorModificado = CoreUtil.modificados(tipoAmostraExame.getServidor().getId().getMatricula(), auxAelTipoAmostraExame.getServidor().getId().getMatricula());
		isServidorModificado = CoreUtil.modificados(tipoAmostraExame.getServidor().getId().getVinCodigo(), auxAelTipoAmostraExame.getServidor().getId().getVinCodigo());
		if(isServidorModificado){
			throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00369, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
		}

		// ORADB aelk_tae_rn.rn_taep_ver_rec_anti
		boolean isRecipienteModificado = CoreUtil.modificados(tipoAmostraExame.getRecipienteColeta().getSeq(), auxAelTipoAmostraExame.getRecipienteColeta().getSeq());
		if(isRecipienteModificado){
			this.verificarRecipiente(tipoAmostraExame);
		}

		// ORADB aelk_tae_rn.rn_taep_ver_anticoag
		Integer seqAnticoagulante = null;
		Integer seqAnticoagulanteAntigo = null;
		if(tipoAmostraExame.getAnticoagulante() != null){
			seqAnticoagulante = tipoAmostraExame.getAnticoagulante().getSeq();
		}
		if(auxAelTipoAmostraExame.getAnticoagulante() != null){
			seqAnticoagulanteAntigo = auxAelTipoAmostraExame.getAnticoagulante().getSeq();
		}
		if(CoreUtil.modificados(seqAnticoagulante, seqAnticoagulanteAntigo)){
			this.verificarRecipiente(tipoAmostraExame);
			this.verificarAnticoagulante(tipoAmostraExame);
		}
		
		// ORADB aelk_tae_rn.rn_taep_ver_mat_anls
		Integer seqMaterialAnalise = null;
		Integer seqMaterialAnaliseAntigo = null;
		if(tipoAmostraExame.getMaterialAnalise() != null){
			seqMaterialAnalise = tipoAmostraExame.getMaterialAnalise().getSeq();
		}
		if(auxAelTipoAmostraExame.getMaterialAnalise() != null){
			seqMaterialAnaliseAntigo = auxAelTipoAmostraExame.getMaterialAnalise().getSeq();
		}
		if(CoreUtil.modificados(seqMaterialAnalise, seqMaterialAnaliseAntigo)){
			this.verificarMaterialAnalise(tipoAmostraExame);
		}

		// ORADB aelk_tae_rn.rn_taep_ver_unid_fun
		Short seqUnidadeFuncional = null;
		Short seqUnidadeFuncionalAntiga = null;
		if(tipoAmostraExame.getUnidadeFuncional()!= null){
			seqUnidadeFuncional = tipoAmostraExame.getUnidadeFuncional().getSeq();
		}
		if(auxAelTipoAmostraExame.getUnidadeFuncional() != null){
			seqUnidadeFuncionalAntiga = auxAelTipoAmostraExame.getUnidadeFuncional().getSeq();
		}
		if(CoreUtil.modificados(seqUnidadeFuncional, seqUnidadeFuncionalAntiga)){
			this.verificarUnidadeFuncional(tipoAmostraExame);
		}
		
		// ORADB aelk_tae_rn.rn_taep_ver_nro_amos
		if(tipoAmostraExame.getNroAmostras() != null){
			boolean isNroAmostrasModificada = CoreUtil.modificados(tipoAmostraExame.getNroAmostras(), auxAelTipoAmostraExame.getNroAmostras());
			if(isNroAmostrasModificada){
				this.verificarNumeroAmostra(tipoAmostraExame);
			}		
		}

		// ORADB aelk_ael_rn.rn_aelp_atu_servidor
		this.atualizarServidorAlterado(tipoAmostraExame);

		// ORADB aelk_tae_rn.rn_taep_ver_origem de TRIGGER AELT_TAE_ASU (AELP_ENFORCE_TAE_RULES) 
		boolean isOrigemAtendimentoModificada = CoreUtil.modificados(tipoAmostraExame.getOrigemAtendimento(), auxAelTipoAmostraExame.getOrigemAtendimento());
		if(isOrigemAtendimentoModificada){
			this.verificarOrigemAtendimento(tipoAmostraExame);
		}

	}
	

	/**
	 * Atualizar AelTipoAmostraExame
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {
		
		this.validaParametroObrigatorio(tipoAmostraExame);
		AelTipoAmostraExame auxAelTipoAmostraExame = getAelTipoAmostraExameDAO().obterPorChavePrimaria(tipoAmostraExame.getId());
		
		if (CoreUtil.modificados(tipoAmostraExame.getAnticoagulante(), auxAelTipoAmostraExame.getAnticoagulante()) 
				|| CoreUtil.modificados(tipoAmostraExame.getExamesMaterialAnalise(), auxAelTipoAmostraExame.getExamesMaterialAnalise())
				|| CoreUtil.modificados(tipoAmostraExame.getIndCongela(), auxAelTipoAmostraExame.getIndCongela())
				|| CoreUtil.modificados(tipoAmostraExame.getMaterialAnalise(), auxAelTipoAmostraExame.getMaterialAnalise())
				|| CoreUtil.modificados(tipoAmostraExame.getNroAmostras(), auxAelTipoAmostraExame.getNroAmostras())
				|| CoreUtil.modificados(tipoAmostraExame.getOrigemAtendimento(), auxAelTipoAmostraExame.getOrigemAtendimento())
				|| CoreUtil.modificados(tipoAmostraExame.getRecipienteColeta(), auxAelTipoAmostraExame.getRecipienteColeta())
				|| CoreUtil.modificados(tipoAmostraExame.getResponsavelColeta(), auxAelTipoAmostraExame.getResponsavelColeta())
				|| CoreUtil.modificados(tipoAmostraExame.getServidor(), auxAelTipoAmostraExame.getServidor())
				|| CoreUtil.modificados(tipoAmostraExame.getServidorAlterado(), auxAelTipoAmostraExame.getServidorAlterado())
				|| CoreUtil.modificados(tipoAmostraExame.getUnidadeFuncional(), auxAelTipoAmostraExame.getUnidadeFuncional())
				|| CoreUtil.modificados(tipoAmostraExame.getUnidadeMedidaAmostra(), auxAelTipoAmostraExame.getUnidadeMedidaAmostra())
				|| CoreUtil.modificados(tipoAmostraExame.getVolumeAmostra(), auxAelTipoAmostraExame.getVolumeAmostra())) {
			
			this.preAtualizarAelTipoAmostraExame(tipoAmostraExame);
			
			this.getAelTipoAmostraExameDAO().atualizarParaListaMaterialAnaliseExame(tipoAmostraExame);
			this.getAelTipoAmostraExameDAO().flush();
			
			this.inserirAelTipoAmostraExameJn(tipoAmostraExame);
			
		}
		
	}
	
	/**
	 * ORADB TRIGGER AELT_TAE_ARU
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void inserirAelTipoAmostraExameJn(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelTipoAmostraExameJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelTipoAmostraExameJn.class, servidorLogado.getUsuario());
		jn.setAnticoagulante(tipoAmostraExame.getAnticoagulante());
		jn.setExamesMaterialAnalise(tipoAmostraExame.getExamesMaterialAnalise());
		jn.setIndCongela(tipoAmostraExame.getIndCongela());
		jn.setMaterialAnalise(tipoAmostraExame.getMaterialAnalise());
		jn.setNroAmostras(tipoAmostraExame.getNroAmostras());
		jn.setOrigemAtendimento(tipoAmostraExame.getOrigemAtendimento());
		jn.setRecipienteColeta(tipoAmostraExame.getRecipienteColeta());
		jn.setResponsavelColeta(tipoAmostraExame.getResponsavelColeta());
		jn.setServidorAlterado(tipoAmostraExame.getServidorAlterado());
		jn.setUnidadeFuncional(tipoAmostraExame.getUnidadeFuncional());
		jn.setUnidadeMedidaAmostra(tipoAmostraExame.getUnidadeMedidaAmostra());
		jn.setVolumeAmostra(tipoAmostraExame.getVolumeAmostra());

		this.getAelTipoAmostraExameJnDAO().persistir(jn);
		this.getAelTipoAmostraExameJnDAO().flush();

	}
	
	/**
	 * ORADB AELT_TAE_ASD aelk_tae_rn.rn_taep_ver_delecao
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void preRemoverAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {

		// Contabiliza tipos de amostra. Se este for o último para um material de análise de exame cujo material de análise seja coletável
		final Long totalPorExame = this.getAelTipoAmostraExameDAO().countListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(tipoAmostraExame.getExamesMaterialAnalise().getId().getExaSigla(), tipoAmostraExame.getExamesMaterialAnalise().getId().getManSeq());
		final Long totalColetavelUnfAtivas = this.getAelUnfExecutaExamesDAO().countUnfExecutaExameAtivaMaterialAnaliseColetavel(tipoAmostraExame.getExamesMaterialAnalise().getAelExames().getSigla(),tipoAmostraExame.getExamesMaterialAnalise().getAelMateriaisAnalises().getSeq());
		
		// aelk_tae_rn.rn_taep_ver_delecao - Verifica se é o único/último item persistido
		if(totalPorExame == 1 && totalColetavelUnfAtivas == 1){
			throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00417, tipoAmostraExame.getOrigemAtendimento().getDescricao()  + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
		}
	
	}
	
	/**
	 * Remover AelTipoAmostraExame
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void removerAelTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) throws ApplicationBusinessException {
		this.validaParametroObrigatorio(tipoAmostraExame);
		this.preRemoverAelTipoAmostraExame(tipoAmostraExame);

		this.getAelTipoAmostraExameDAO().remover(this.getAelTipoAmostraExameDAO().obterAelTipoAmostraExameIdPorID(tipoAmostraExame.getId()));
		this.getAelTipoAmostraExameDAO().flush();
		
	}
	
	/*
	 * Validações...
	 */

	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarServidor(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		tipoAmostraExame.setServidor(servidorLogado);
	}
	
	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarServidorAlterado(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		tipoAmostraExame.setServidorAlterado(servidorLogado);
	}
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_rec_anti
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarRecipiente(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		AelRecipienteColeta recipienteColeta = tipoAmostraExame.getRecipienteColeta();
		AelAnticoagulante anticoagulante = tipoAmostraExame.getAnticoagulante();
		if(recipienteColeta != null){
			if(!recipienteColeta.getIndSituacao().isAtivo()){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00392, recipienteColeta.getDescricao(), tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
			if(recipienteColeta.getIndAnticoag().equals(DominioSimNao.S) && anticoagulante == null){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00402, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
		}
	}
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_anticoag
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarAnticoagulante(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		AelAnticoagulante anticoagulante = tipoAmostraExame.getAnticoagulante();
		if(anticoagulante != null){
			if(!anticoagulante.getIndSituacao().isAtivo()){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00394, anticoagulante.getDescricao(), tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
		}
	}
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_mat_anls
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarMaterialAnalise(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		AelMateriaisAnalises materialAnalise = tipoAmostraExame.getMaterialAnalise();
		if(materialAnalise != null){
			if(!materialAnalise.getIndSituacao().isAtivo()){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00361, materialAnalise.getDescricao(), tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
			if(!materialAnalise.getIndColetavel()){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00397, materialAnalise.getDescricao(), tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}	
		}
	}
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_unid_fun
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarUnidadeFuncional(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		AghUnidadesFuncionais unidadeFuncional = tipoAmostraExame.getUnidadeFuncional();
		if(unidadeFuncional != null){
			if(!unidadeFuncional.getIndSitUnidFunc().isAtivo()){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00403, unidadeFuncional.getDescricao(), tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}	
			
			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA);
			if(!possuiCaracteristica){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00404, unidadeFuncional.getDescricao(), tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
			if(!tipoAmostraExame.getResponsavelColeta().equals(DominioResponsavelColetaExames.C)){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00406, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
		}

	}
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_nro_amos
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarNumeroAmostra(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		
		AelExamesMaterialAnalise exameMaterialAnalise = tipoAmostraExame.getExamesMaterialAnalise();
		AelMateriaisAnalises materialAnalise = tipoAmostraExame.getMaterialAnalise();
		
		if(!exameMaterialAnalise.getIndSolicInformaColetas() && !exameMaterialAnalise.getIndGeraItemPorColetas() 
				&& materialAnalise.getIndColetavel() && (tipoAmostraExame.getNroAmostras() == null || tipoAmostraExame.getNroAmostras() == 0)){ 
			throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00837, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
		}
	}
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_origem
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarOrigemAtendimento(AelTipoAmostraExame tipoAmostraExame)  throws ApplicationBusinessException{
		DominioOrigemAtendimento origemAtendimento = tipoAmostraExame.getOrigemAtendimento();
		if(origemAtendimento != null){	
			
			List<AelTipoAmostraExame> listaDiferenteTodasOrigens = getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExamePorOrigemAtendimentoDiferenteTodasOrigens(tipoAmostraExame.getExamesMaterialAnalise().getAelExames().getSigla(), tipoAmostraExame.getMaterialAnalise().getSeq());
			if(origemAtendimento.equals(DominioOrigemAtendimento.T) && (listaDiferenteTodasOrigens != null && !listaDiferenteTodasOrigens.isEmpty())){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00839, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
			
			List<AelTipoAmostraExame> listaTodasOrigens = getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExamePorOrigemAtendimentoTodasOrigens(tipoAmostraExame.getExamesMaterialAnalise().getAelExames().getSigla(), tipoAmostraExame.getMaterialAnalise().getSeq());
			if(!origemAtendimento.equals(DominioOrigemAtendimento.T) && (listaTodasOrigens != null && !listaTodasOrigens.isEmpty())){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00840, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}	
		}
	}	
	
	/**
	 * ORADB aelk_tae_rn.rn_taep_ver_origem
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	public void verificarOrigemAtendimento(AelTipoAmostraExame tipoAmostraExame, List<AelTipoAmostraExame> listaTiposAmostraExame)  throws ApplicationBusinessException{
		
		DominioOrigemAtendimento origemAtendimento = tipoAmostraExame.getOrigemAtendimento();
		Integer seqMaterialAnalise = tipoAmostraExame.getMaterialAnalise().getSeq();
		
		for (AelTipoAmostraExame outro : listaTiposAmostraExame) {
			DominioOrigemAtendimento outroOrigemAtendimento = outro.getOrigemAtendimento();
			Integer outroSeqMaterialAnalise = outro.getMaterialAnalise().getSeq();
			
			if(origemAtendimento.equals(DominioOrigemAtendimento.T) && (seqMaterialAnalise.equals(outroSeqMaterialAnalise) && (!outroOrigemAtendimento.equals(DominioOrigemAtendimento.T)))){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00839, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
			
			if(!origemAtendimento.equals(DominioOrigemAtendimento.T) && (seqMaterialAnalise.equals(outroSeqMaterialAnalise) && (outroOrigemAtendimento.equals(DominioOrigemAtendimento.T)))){
				throw new ApplicationBusinessException(ManterTipoAmostraExameRNExceptionCode.AEL_00840, tipoAmostraExame.getOrigemAtendimento().getDescricao() + _HIFEN_ + tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
			
		}

	}
	
	/**
	 * Valida parâmetros obrigatórios
	 * @param object
	 */
	private void validaParametroObrigatorio(Object object){
		if (object == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
	}
	

	/*
	 * Getters...
	 */	
	

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected AelTipoAmostraExameJnDAO getAelTipoAmostraExameJnDAO() {
		return aelTipoAmostraExameJnDAO;
	}
	
	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
