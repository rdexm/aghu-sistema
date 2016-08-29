package br.gov.mec.aghu.exames.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExecExamesMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAmostrasId;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;

/**
 * 
 * @author lsamberg
 *
 */
@Stateless
public class AelAmostraItemExamesRN extends BaseBusiness {


@EJB
private AelAmostrasON aelAmostrasON;

@EJB
private AelAmostrasRN aelAmostrasRN;

@EJB
private AelAmostraItemExamesON aelAmostraItemExamesON;

private static final Log LOG = LogFactory.getLog(AelAmostraItemExamesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelExecExamesMatAnaliseDAO aelExecExamesMatAnaliseDAO;

@Inject 
private AelAmostrasDAO aelAmostrasDAO;

@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2105910650806853791L;

	public enum AelAmostraItemExamesRNExceptionCode implements BusinessExceptionCode {

		AEL_00892;

	}

	/**
	 * Remove objeto SEM FLUSH;
	 * @param {AelAmostraItemExames} aelAmostraItemExames
	 */
	public void removerSemFlush(AelAmostraItemExames aelAmostraItemExames) {

		getAelAmostraItemExamesDAO().remover(aelAmostraItemExames);
		getAelAmostraItemExamesDAO().flush();

	}

	/**
	 * Regras de update em AEL_AMOSTRA_ITEM_EXAMES
	 * @param aelAmostraItemExames
	 * @param novaSituacaoAmostra
	 * @param flush
	 * @param atualizarItemSolic - Indica se deve atualizar ael_item_solicitacao_exames
	 * @throws BaseException
	 */
	public void atualizarAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames, DominioSituacaoAmostra novaSituacaoAmostra, Boolean flush, Boolean atualizarItemSolic, String nomeMicrocomputador) throws BaseException{

		AelAmostraItemExames aelAmostraItemExamesOld = obterAelAmostraItemExamesOld(aelAmostraItemExames);
		//AelAmostraItemExames aelAmostraItemExamesOld = getAelAmostraItemExamesDAO().obterOriginal(aelAmostraItemExames.getId());
		//AelAmostraItemExames aelAmostraItemExamesOld = getAelAmostraItemExamesDAO().obterPorChavePrimaria(aelAmostraItemExames.getId());
		DominioSituacaoAmostra situacaoAmostraOld = null;
		if (aelAmostraItemExamesOld != null){
			situacaoAmostraOld = aelAmostraItemExamesOld.getSituacao();
		}
		if(novaSituacaoAmostra != null){
			aelAmostraItemExames.setSituacao(novaSituacaoAmostra);
		}
		beforeUpdateAelAmostraItemExames(aelAmostraItemExames);
		getAelAmostraItemExamesDAO().merge(aelAmostraItemExames);
		if (flush){
			getAelAmostraItemExamesDAO().flush();
		}
		afterUpdateAelAmostraItemExames(aelAmostraItemExames, aelAmostraItemExamesOld, atualizarItemSolic, nomeMicrocomputador, new Date(), situacaoAmostraOld);
	}

	private AelAmostraItemExames obterAelAmostraItemExamesOld(AelAmostraItemExames aelAmostraItemExames){		
		AelAmostraItemExames aelAmostraItemExamesOld = null;
		
		if (aelAmostraItemExames != null){		
			aelAmostraItemExamesOld = getAelAmostraItemExamesDAO().obterOriginal(aelAmostraItemExames);
			
			AelItemSolicitacaoExamesId idItemSolic = aelAmostraItemExamesOld.getAelItemSolicitacaoExames().getId();			
			AelItemSolicitacaoExames itemSolicitacaoExamesOld = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(idItemSolic, true, AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO);
			
			aelAmostraItemExamesOld.setAelItemSolicitacaoExames(itemSolicitacaoExamesOld);
			
			AelAmostrasId idAmostra = aelAmostraItemExamesOld.getAelAmostras().getId();
			AelAmostras amostra = aelAmostrasDAO.obterPorChavePrimaria(idAmostra);
			
			aelAmostraItemExamesOld.setAelAmostras(amostra);
		}
		
		return aelAmostraItemExamesOld;
	}
	
	/**
	 * Regras de update em AEL_AMOSTRA_ITEM_EXAMES
	 * @param aelAmostraItemExames
	 * @param flush
	 * @param atualizarItemSolic - Indica se deve atualizar ael_item_solicitacao_exames
	 * @throws BaseException
	 */
	public void atualizarAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames, Boolean flush, Boolean atualizarItemSolic, String nomeMicrocomputador) throws BaseException{
		this.atualizarAelAmostraItemExames(aelAmostraItemExames, null, flush, atualizarItemSolic, nomeMicrocomputador);
	}
	
	public AelAmostraItemExames inserir(AelAmostraItemExames itemAmostra) throws BaseException {

		this.beforeInsertAelAmostraItemExames(itemAmostra);
		this.getAelAmostraItemExamesDAO().persistir(itemAmostra);		
		this.getAelAmostraItemExamesDAO().flush();
		return itemAmostra;

	}

	/**
	 * ORADB TRIGGER AELT_AIE_BRU
	 * @param aelAmostraItemExames
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void beforeUpdateAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames) throws ApplicationBusinessException {
		aelAmostraItemExames.setAlteradoEm(new Date());
	}

	/**
	 * ORADB AELP_ENFORCE_AIE_RULES
	 * @param aelAmostraItemExames
	 * @param situacaoOld 
	 * @throws BaseException 
	 */
	public void afterUpdateAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames, AelAmostraItemExames aelAmostraItemExamesOld, Boolean atualizarItemSolic, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		this.afterUpdateAelAmostraItemExames(aelAmostraItemExames, aelAmostraItemExamesOld, atualizarItemSolic, nomeMicrocomputador, dataFimVinculoServidor, null);
		
	}
	/**
	 * ORADB AELP_ENFORCE_AIE_RULES
	 * @param aelAmostraItemExames
	 * @param situacaoOld 
	 * @throws BaseException 
	 */
	public void afterUpdateAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames, AelAmostraItemExames aelAmostraItemExamesOld, Boolean atualizarItemSolic, String nomeMicrocomputador, final Date dataFimVinculoServidor, DominioSituacaoAmostra situacaoOld) throws BaseException {

		DominioSituacaoAmostra situacao = aelAmostraItemExames.getSituacao();
		
		if (situacaoOld == null){
			situacaoOld = aelAmostraItemExamesOld.getSituacao();
		}

		if (!situacao.equals(situacaoOld)) {
			altItemExm(aelAmostraItemExames, aelAmostraItemExamesOld, atualizarItemSolic, nomeMicrocomputador, dataFimVinculoServidor, situacaoOld);

			atuEquAmos(aelAmostraItemExames, nomeMicrocomputador);

			if (situacao.equals(DominioSituacaoAmostra.R) 
					|| ((situacao.equals(DominioSituacaoAmostra.G) && situacaoOld.equals(DominioSituacaoAmostra.R)))
					|| ((situacao.equals(DominioSituacaoAmostra.C) && situacaoOld.equals(DominioSituacaoAmostra.R)))
					|| ((situacao.equals(DominioSituacaoAmostra.M) && situacaoOld.equals(DominioSituacaoAmostra.R)))) {

				atuSitFilh(aelAmostraItemExames, nomeMicrocomputador);

			}

		}

	}

	/**
	 * ORADB aelk_aie_rn.rn_aiep_alt_item_exm INICIO
	 * @param aelAmostraItemExames
	 * @param aelAmostraItemExamesOld
	 * @param situacaoOld 
	 * @throws BaseException 
	 */
	private void altItemExm(AelAmostraItemExames aelAmostraItemExames, AelAmostraItemExames aelAmostraItemExamesOld, Boolean atualizarItemSolic, String nomeMicrocomputador, final Date dataFimVinculoServidor, DominioSituacaoAmostra situacaoOld) throws BaseException {

		if (atualizarItemSolic) {
			if(situacaoOld == null && aelAmostraItemExamesOld != null){
				situacaoOld = aelAmostraItemExamesOld.getSituacao();
			}
			if (!situacaoOld.equals(DominioSituacaoAmostra.R) && !situacaoOld.equals(DominioSituacaoAmostra.U) && aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.A)) {

				throw new ApplicationBusinessException(AelAmostraItemExamesRNExceptionCode.AEL_00892);

			}

			AghParametros parametroColeta = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
			AghParametros parametroColetado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO);
			AghParametros parametroRecebido = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_RECEBIDO);
			AghParametros parametroAreaExecutora = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

			AelSitItemSolicitacoes vIseSituacao = null;
			AelMotivoCancelaExames vMocSeq = null;
			String vComplemento = null;

			if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.M)) {

				String codigo = parametroColeta.getVlrTexto();
				vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(codigo);

			} else if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.R)) {

				String codigo = parametroAreaExecutora.getVlrTexto();
				vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(codigo);

			} else if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.U)) {

				String codigo = parametroRecebido.getVlrTexto();
				vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(codigo);

			} else if(aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.C)) {

				String codigo = parametroColetado.getVlrTexto();
				vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(codigo);

			} else if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.G) || aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.A)) {

				AelExtratoItemSolicitacao aelExtratoItemSolicitacao =  getAelExtratoItemSolicitacaoDAO().obterPenultimoExtratoPorItemSolicitacao(aelAmostraItemExamesOld.getId().getIseSoeSeq(),aelAmostraItemExamesOld.getId().getIseSeqp());

				if (aelExtratoItemSolicitacao != null) {

					vIseSituacao = aelExtratoItemSolicitacao.getAelSitItemSolicitacoes();
					// TODO Mapear com o POJO da tabela AEL_MOTIVO_CANCELA_EXAMES quando o mesmo for mapeado.
					if (aelExtratoItemSolicitacao.getAelMotivoCancelaExames() != null) {

						vMocSeq = aelExtratoItemSolicitacao.getAelMotivoCancelaExames();

					}

					vComplemento = aelExtratoItemSolicitacao.getComplementoMotCanc();

				}

			}

			if (vIseSituacao != null) {

				AelItemSolicitacaoExames aelItemSolicitacaoExames =  aelAmostraItemExamesOld.getAelItemSolicitacaoExames();
				
				AelItemSolicitacaoExames original = new AelItemSolicitacaoExames();
				try {
					BeanUtils.copyProperties(original, aelItemSolicitacaoExames);
				} catch (IllegalAccessException | InvocationTargetException e) {
					LOG.error("Erro ao copiar itemSolicitacaoExame.", e);
				}
				
				aelItemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);
				aelItemSolicitacaoExames.setAelMotivoCancelaExames(vMocSeq);
				aelItemSolicitacaoExames.setComplementoMotCanc(vComplemento);
				getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames,  original, true, nomeMicrocomputador);

			}

		}

		altItemExm(aelAmostrasDAO.obterPorChavePrimaria(aelAmostraItemExamesOld.getAelAmostras().getId()));

	}

	/**
	 * ORADB aelk_aie_rn.rn_aiep_alt_item_exm FIM
	 * @param aelAmostras
	 *  
	 * @throws ApplicationBusinessException 
	 */
	private void altItemExm(AelAmostras aelAmostras) throws BaseException {

		DominioSituacaoAmostra aieSituacao = getAelAmostrasRN().avaliarSituacaoAmostras(aelAmostras);

		if (!aieSituacao.equals(aelAmostras.getSituacao())) {

			if (aieSituacao.equals(DominioSituacaoAmostra.R)){

				aelAmostras.setDthrEntrada(new Date());			

			}

			aelAmostras.setSituacao(aieSituacao);
			getAelAmostrasON().persistirAelAmostra(aelAmostras, true);

		}

	}

	/**
	 * ORADB aelk_aie_rn.rn_aiep_atu_equ_amos
	 * @param aelAmostraItemExames
	 * @throws BaseException 
	 */
	private void atuEquAmos(AelAmostraItemExames aelAmostraItemExames, String nomeMicrocomputador) throws BaseException {

		if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.R)) {

			AelItemSolicitacaoExames itemSolicitacaoExames = aelAmostraItemExames.getAelItemSolicitacaoExames();
			DominioProgramacaoExecExames vProgramacao = null;

			if (itemSolicitacaoExames.getTipoColeta().equals(DominioTipoColeta.U)) {

				vProgramacao = DominioProgramacaoExecExames.U;

			} else {

				vProgramacao = DominioProgramacaoExecExames.R;

			}

			if (vProgramacao.equals(DominioProgramacaoExecExames.R)) {

				AelSolicitacaoExames solicitacao = aelAmostraItemExames.getAelItemSolicitacaoExames().getSolicitacaoExame();
				Boolean automacao = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacao.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.AUTOMACAO_ROTINA);

				if (!automacao) {

					vProgramacao = DominioProgramacaoExecExames.U;

				}

			}

			AelExecExamesMatAnalise execExamesMatAnalise = getAelExecExamesMatAnaliseDAO().buscarAelExecExamesMatAnaliseComEquipamentoPorAelItemSolicitacaoExame(itemSolicitacaoExames,vProgramacao);

			if (execExamesMatAnalise != null) {

				aelAmostraItemExames.setAelEquipamentos(execExamesMatAnalise.getAelEquipamentos());

			}

			getAelAmostraItemExamesON().persistirAelAmostraItemExames(aelAmostraItemExames, true, nomeMicrocomputador);

		}	

	}	

	/**
	 * ORADB aelk_aie_rn.rn_aiep_atu_sit_filh
	 * @param aelAmostraItemExamess
	 * @throws BaseException 
	 */
	private void atuSitFilh(AelAmostraItemExames aelAmostraItemExames, String nomeMicrocomputador) throws BaseException {
/*
		AghParametros parametroNaAreaExecutora = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros parametroAExecutar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);

		AelSitItemSolicitacoes situacao = null;

		if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.R)) {

			String codigo = parametroNaAreaExecutora.getVlrTexto();
			situacao = getAelSitItemSolicitacoesDAO().obterPeloId(codigo);

		} else if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.G) || aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.C) || aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.M)) {

			String codigo = parametroAExecutar.getVlrTexto();
			situacao = getAelSitItemSolicitacoesDAO().obterPeloId(codigo);

		}
*/
		AelItemSolicitacaoExames itemSolicitacaoExames = aelAmostraItemExames.getAelItemSolicitacaoExames();
		List<AelItemSolicitacaoExames> listaItensFilhos = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorItemSolicitacaoExamePai(itemSolicitacaoExames);

		for (AelItemSolicitacaoExames filho : listaItensFilhos) {
			if (!filho.getMaterialAnalise().getIndColetavel()) {
				if (!itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals("EX") && !itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals("LI")) {
					if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.R)) {
						final AghParametros parametroNaAreaExecutora = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
						filho.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(parametroNaAreaExecutora.getVlrTexto()));
					} else if (aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.G)
							|| aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.C)
							|| aelAmostraItemExames.getSituacao().equals(DominioSituacaoAmostra.M)) {
						final AghParametros parametroAExecutar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
						filho.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPeloId(parametroAExecutar.getVlrTexto()));
					}
					getSolicitacaoExameFacade().atualizar(filho, nomeMicrocomputador, null);
					this.flush();
				}
			}
		}
	}

	/**
	 * ORADB TRIGGER AELT_AIE_BRI
	 * 
	 * @param aelAmostraItemExames
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void beforeInsertAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames) throws ApplicationBusinessException {
		aelAmostraItemExames.setAlteradoEm(new Date());
	}


	/** GET/SET **/
	public AelAmostraItemExamesDAO getAelAmostraItemExamesDAO(){
		return aelAmostraItemExamesDAO;
	}

	public AelAmostraItemExamesON getAelAmostraItemExamesON(){
		return aelAmostraItemExamesON;
	}

	protected AelExecExamesMatAnaliseDAO getAelExecExamesMatAnaliseDAO(){
		return aelExecExamesMatAnaliseDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO(){
		return aelExtratoItemSolicitacaoDAO;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	public AelAmostrasRN getAelAmostrasRN(){
		return aelAmostrasRN;
	}

	public AelAmostrasON getAelAmostrasON(){
		return aelAmostrasON;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void atualizarIndImpressaoQuestionario(
			AelRespostaQuestao respostaQuestao) {
		AelItemSolicitacaoExames item = aelItemSolicitacaoExameDAO.obterPorChavePrimaria(respostaQuestao.getAelItemSolicitacaoExames().getId());
		item.setIndInfComplImp(true);
		getAelItemSolicitacaoExameDAO().atualizar(item);
		getAelItemSolicitacaoExameDAO().flush();
	}

}
