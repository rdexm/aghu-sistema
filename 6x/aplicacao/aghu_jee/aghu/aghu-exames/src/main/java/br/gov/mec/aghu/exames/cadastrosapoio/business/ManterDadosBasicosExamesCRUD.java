package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Calendar;
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
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesJnDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelSinonimoExameDAO;
import br.gov.mec.aghu.exames.pesquisa.business.AelExamesExceptionCode;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesJn;
import br.gov.mec.aghu.model.AelExamesJnId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelSinonimoExameId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpaCadOrdItemExame;
import br.gov.mec.aghu.model.MpaPopExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
@Stateless
public class ManterDadosBasicosExamesCRUD extends BaseBusiness {

	@EJB
	private AelSinonimosExamesCRUD aelSinonimosExamesCRUD;
	
	private static final Log LOG = LogFactory.getLog(ManterDadosBasicosExamesCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	
	@Inject
	private AelExamesJnDAO aelExamesJnDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	@EJB
	private IParametroFacade iParametroFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@Inject
	private AelSinonimoExameDAO aelSinonimoExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4779657271186003580L;

	
	
	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}
	
	protected AelExamesJnDAO getAelExamesJnDAO() {
		return aelExamesJnDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected AelSinonimoExameDAO getAelSinonimoExameDAO(){
		return aelSinonimoExameDAO;
	}
	
	private AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}
	
	protected AelSinonimosExamesCRUD getAelSinonimosExamesCRUD(){
		return aelSinonimosExamesCRUD;
	}
	
	public void persistirAelExames(AelExames aelExames, String sigla) throws BaseException {
		
		testaParametroObrigatorio(aelExames);
		
		if (aelExames.getSigla() == null) {
			aelExames.setSigla(sigla);
			preInserirAelExames(aelExames);
			getAelExamesDAO().persistir(aelExames);
			getAelExamesDAO().flush();
			posInserirAelExames(aelExames);
		}else{
			AelExames aelExamesOriginal = getAelExamesDAO().obterPeloId(aelExames.getSigla());
			Integer oldSerMatriculaAlterado = aelExamesOriginal.getSerMatricula();
			Short oldSerVinCodigoAlterado = aelExamesOriginal.getSerVinCodigo();
			DominioSituacao oldIndSituacao = aelExamesOriginal.getIndSituacao();
			String oldSigla = aelExamesOriginal.getSigla();
			String oldDescricao = aelExamesOriginal.getDescricao();
			String oldDescricaoUsual = aelExamesOriginal.getDescricaoUsual();
			Boolean oldIndImpressao = aelExamesOriginal.getIndImpressao();
			preAtualizarAelExames(aelExames,aelExamesOriginal);
			getAelExamesDAO().merge(aelExames);
			getAelExamesDAO().flush();
			posAtualizarAelExames(aelExames, oldSerMatriculaAlterado, oldSerVinCodigoAlterado, oldIndSituacao, oldSigla, oldDescricao, oldDescricaoUsual, oldIndImpressao);
		}
	}
	
	/**
	 * ORADB aelt_exa_aru
	 * 
	 * @param aelExames
	 * @param aelExamesOriginal
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void posAtualizarAelExames(AelExames aelExames, Integer oldSerMatriculaAlterado, Short oldSerVinCodigoAlterado, DominioSituacao oldIndSituacao, String oldSigla, String oldDescricao, String oldDescricaoUsual, Boolean oldIndImpressao) throws ApplicationBusinessException {		
		inserirAelExamesJn(aelExames, oldSerMatriculaAlterado, oldSerVinCodigoAlterado, oldIndSituacao, oldSigla, oldDescricao, oldDescricaoUsual, oldIndImpressao, DominioOperacoesJournal.UPD);
	}

	/**
	 * ORADB aelt_exa_asi
	 * 
	 * @param aelExames
	 * @throws ApplicationBusinessException 
	 *  
	 */
	private void posInserirAelExames(AelExames aelExames) throws ApplicationBusinessException {
		inserirAelSinonimoExame(aelExames);
	}

	/**
	 * ORADB AELT_EXA_BRU
	 * 
	 * @param aelExames
	 * @param aelExamesOriginal 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 *  
	 */
	private void preAtualizarAelExames(AelExames aelExames, AelExames aelExamesOriginal) throws ApplicationBusinessException {
		verificarSituacaoExameESituacaoMaterialAnalise(aelExames);
		validarAlteracoesAelSinonimoExame(aelExames,aelExamesOriginal);
		atualizarAelSinonimoExame(aelExames);
	}
	
	/**
	 * ORADB AELT_EXA_ARU
	 * 
	 * @param aelExames
	 * @param aelExamesOriginal
	 * @throws ApplicationBusinessException 
	 */
	private void inserirAelExamesJn(AelExames aelExames,
			Integer oldSerMatriculaAlterado, Short oldSerVinCodigoAlterado, DominioSituacao oldIndSituacao, String oldSigla, String oldDescricao, String oldDescricaoUsual, Boolean oldIndImpressao, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		
		if ((aelExames.getSigla() != null && !aelExames.getSigla().equals(oldSigla))
				|| (aelExames.getDescricao() != null && !aelExames.getDescricao().equals(
						oldDescricao))
				|| (aelExames.getDescricaoUsual() != null && !aelExames.getDescricaoUsual().equals(
						oldDescricaoUsual))
				|| (aelExames.getIndSituacao()!=null && !aelExames.getIndSituacao().equals(
						oldSigla))
				|| (aelExames.getSerMatriculaAlterado() != null && !aelExames.getSerMatriculaAlterado().equals(
						oldSerMatriculaAlterado))
				|| (aelExames.getSerVinCodigoAlterado() != null &&!aelExames.getSerVinCodigoAlterado().equals(
						oldSerVinCodigoAlterado))
				|| (aelExames.getIndImpressao()!=null && !aelExames.getIndImpressao().equals(
						oldIndImpressao))) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelExamesJn aelExamesJn = new AelExamesJn();
			AelExamesJnId id = new AelExamesJnId();
			id.setJnUser(servidorLogado.getUsuario());
			id.setJnDateTime(new Date());
			id.setJnOperation(operacao.toString());
			id.setSigla(oldSigla);
			id.setDescricao(oldDescricao);
			id.setDescricaoUsual(oldDescricaoUsual);
			id.setIndSituacao(oldIndSituacao);
			id.setSerMatriculaAlterado(oldSerMatriculaAlterado);
			id.setSerVinCodigoAlterado(oldSerVinCodigoAlterado);
			id.setIndImpressao(oldIndImpressao);
			aelExamesJn.setId(id);
			
			getAelExamesJnDAO().persistir(aelExamesJn);
			getAelExamesJnDAO().flush();
			
		}
		
	}

	/**
	 * ORADB aelk_exa_rn.rn_exap_ver_update
	 * 
	 * @param aelExames
	 * @param aelExamesOriginal 
	 */
	private void validarAlteracoesAelSinonimoExame(AelExames aelExames, AelExames aelExamesOriginal) throws ApplicationBusinessException {
		if(aelExames != null && aelExamesOriginal != null){
			if( !aelExames.getCriadoEm().equals(aelExamesOriginal.getCriadoEm()) || !aelExames.getSerMatricula().equals(aelExamesOriginal.getSerMatricula()) || !aelExames.getSerVinCodigo().equals(aelExamesOriginal.getSerVinCodigo()) || !aelExames.getDescricao().equals(aelExamesOriginal.getDescricao())){
				throw new ApplicationBusinessException(AelExamesExceptionCode.AEL_00369);
			}
		}

	}

	/**
	 * ORADB aelk_exa_rn.rn_exap_atu_update
	 * 
	 * @param aelExames
	 * @throws ApplicationBusinessException 
	 *  
	 */
	private void atualizarAelSinonimoExame(AelExames aelExames) throws ApplicationBusinessException{
		AelSinonimoExame aelSinonimoExame = getAelSinonimoExameDAO().buscarSinonimoPrincipalPorAelExames(aelExames);
		if(aelSinonimoExame != null){
			aelSinonimoExame.setNome(aelExames.getDescricaoUsual());
			getAelSinonimosExamesCRUD().atualizarAelSinonimosExames(aelSinonimoExame, false);
		}
	}
	
	/**
	 * ORADB AEL_EMA_EXA_FK1 - AELT_EXA_BRI - AELT_EXA_ASI – ENFORCE AELP_ENFORCE_EXA_RULES
	 * 
	 * @param aelExames
	 * @throws ApplicationBusinessException
	 */
	private void preInserirAelExames(AelExames aelExames) throws BaseException {	
		
		// Regra AEL_EMA_EXA_FK1 (Não permite siglas/PK duplicadas no cadastro de um novo exame)
		if (getAelExamesDAO().obterPeloId(aelExames.getSigla()) != null){
			AelExamesExceptionCode.AEL_EMA_EXA_FK1.throwException();
		}
		
		setarServidorNoExame(aelExames);
		verificarSituacaoExameESituacaoMaterialAnalise(aelExames);
		aelExames.setCriadoEm(new Date());
		
		fonetizarDescricaoExame(aelExames);
	}

	/**
	 * ORADB aelk_fme_rn.rn_fmep_atu_ins
	 * @param aelExames
	 */
	private void fonetizarDescricaoExame(AelExames aelExames) {
		// TODO implementar este método?
	}

	/**
	 * ORADB aelk_exa_rn.rn_exap_atu_sinonimo
	 * 
	 * @param aelExames
	 * @throws ApplicationBusinessException 
	 *  
	 */
	private void inserirAelSinonimoExame(AelExames aelExames) throws ApplicationBusinessException {
		
		AelSinonimoExame aelSinonimoExame = new AelSinonimoExame();
		AelSinonimoExameId aelSinonimoExameId = new AelSinonimoExameId();
		aelSinonimoExameId.setExaSigla(aelExames.getSigla());
		aelSinonimoExameId.setSeqp(Short.valueOf("1"));
		aelSinonimoExame.setId(aelSinonimoExameId);
		
		aelSinonimoExame.setNome(aelExames.getDescricaoUsual());
		aelSinonimoExame.setIndSituacao(DominioSituacao.A);
		
		getAelSinonimosExamesCRUD().inserirAelSinonimosExames(aelSinonimoExame);
	}

	/**
	 * ORADB aelk_exa_rn.rn_exap_ver_situacao
	 * 
	 * @param aelExames
	 *  
	 */
	private void verificarSituacaoExameESituacaoMaterialAnalise(AelExames aelExames) throws ApplicationBusinessException {
		if(DominioSituacao.I.equals(aelExames.getIndSituacao())){
			List<AelExamesMaterialAnalise> examesMateriais =  getAelExamesMaterialAnaliseDAO().buscarAelExamesMaterialAnaliseAtivoPorAelExames(aelExames);
			if(examesMateriais != null && !examesMateriais.isEmpty()){
				throw new ApplicationBusinessException(AelExamesExceptionCode.AEL_00351);
			}
		}
	}

	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * 
	 * @param aelExames
	 * @throws ApplicationBusinessException  
	 */
	private void setarServidorNoExame(AelExames aelExames) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Short vSerVinCodigo =servidorLogado.getId().getVinCodigo();
		Integer vSerMatricula =servidorLogado.getId().getMatricula();
		
		aelExames.setSerMatricula(vSerMatricula);
		aelExames.setSerVinCodigo(vSerVinCodigo);		
	}
	
	/**
	 * ORADB aelk_exa_rn.rn_exap_ver_delecao
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	public void verificaDataCriacao(final Date data) throws BaseException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(AelExamesExceptionCode.AEL_00343);
			}
		} else {
			throw new ApplicationBusinessException(AelExamesExceptionCode.AEL_00344);
		}
	}
	
	/**
	 * ORADB CHK_AEL_EXAMES
	 * 
	 * @param aelExames
	 */
	private void validaDependencias(AelExames aelExames) throws BaseException {	

		BaseListException listaException = new BaseListException();

		// Verifica a existência de registros em outras entidades
		listaException.add(this.existeItem(aelExames, MpaPopExame.class, MpaPopExame.Fields.EXAME_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_MPA_POP_EXAMES));
		listaException.add(this.existeItem(aelExames, MpaCadOrdItemExame.class, MpaCadOrdItemExame.Fields.UFE_EMA_EXA_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_MPA_CAD_ORD_ITEM_EXAMES));
		listaException.add(this.existeItem(aelExames, AelOrdExameMatAnalise.class, AelOrdExameMatAnalise.Fields.EMA_EXA_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_AEL_ORD_EXAME_MAT_ANALISES));
		listaException.add(this.existeItem(aelExames, AelExamesMaterialAnalise.class, AelExamesMaterialAnalise.Fields.EXA_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_AEL_EXAMES_MATERIAL_ANALISE));
		listaException.add(this.existeItem(aelExames, AelItemSolicitacaoExames.class, AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_AEL_ITEM_SOLICITACAO_EXAMES));
		listaException.add(this.existeItem(aelExames, AelUnfExecutaExames.class, AelUnfExecutaExames.Fields.EMA_EXA_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_AEL_UNF_EXECUTA_EXAMES));
		listaException.add(this.existeItem(aelExames, AelGradeAgendaExame.class, AelGradeAgendaExame.Fields.UFE_EMA_EXA_SIGLA, AelExamesExceptionCode.CHK_AEL_EXAMES_AEL_GRADE_AGENDA_EXAMES));
		
		
		if (listaException.hasException()) {
			throw listaException;
		}
	}
	
	/**
	 * ORADB CHK_AEL_EXAMES
	 * @param aelExames
	 * @param class1
	 * @param field
	 * @param exceptionCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ApplicationBusinessException existeItem(AelExames aelExames, Class class1, Enum field, BusinessExceptionCode exceptionCode) {
		final boolean isExisteOcorrencia = getAelExamesDAO().existeItem(aelExames, class1, field);
		if(isExisteOcorrencia){
			return new ApplicationBusinessException(exceptionCode);
		}
		return null;
	}
	
	/**
	 * Pré-remover
	 * @param aelExames
	 * @throws BaseException
	 */
	public void preRemoverAelExames(AelExames aelExames) throws BaseException {		
		testaParametroObrigatorio(aelExames);
		verificaDataCriacao(aelExames.getCriadoEm());
		validaDependencias(aelExames);
	}
	
	/**
	 * Remove todos os sinônimos de exames de um exame
	 * @param aelExames
	 */
	private void removerAelSinonimoExames(AelExames aelExames){
		List<AelSinonimoExame> listaSinonimoExames = getAelSinonimoExameDAO().pesquisarSinonimosExames(aelExames);
		for (AelSinonimoExame aelSinonimoExame : listaSinonimoExames) {
			getAelSinonimoExameDAO().remover(aelSinonimoExame);
			getAelSinonimoExameDAO().flush();
		}
	}
	
	/**
	 * ORADB aelk_exa_rn.rn_exap_ver_delecao
	 * @param aelExames
	 * @throws BaseException
	 */
	public void removerAelExames(AelExames aelExames) throws BaseException{
		aelExames = getAelExamesDAO().obterPorChavePrimaria(aelExames.getSigla());
		testaParametroObrigatorio(aelExames);
		preRemoverAelExames(aelExames);
		removerAelSinonimoExames(aelExames);
		getAelExamesDAO().remover(aelExames);
		getAelExamesDAO().flush();
		
	}
	
	private void testaParametroObrigatorio(Object o){
		if (o == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}