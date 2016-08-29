package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioApAnterior;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemConfigExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemConfigExameJnDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelItemConfigExameId;
import br.gov.mec.aghu.model.AelItemConfigExameJn;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelItemConfigExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelItemConfigExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemConfigExameJnDAO aelItemConfigExameJnDAO;
	
	@Inject
	private AelItemConfigExameDAO aelItemConfigExameDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;

	private static final long serialVersionUID = -798406697870064652L;

	private enum AelItemConfigExameRNCode implements
	BusinessExceptionCode {
		AEL_00353, AEL_02733, ERRO_CLONE_ITEM_CONFIG_EXAME; 
	}

	public void persistir(AelItemConfigExame item, AelItemConfigExame itemOld, Boolean inclusao) throws BaseException {
		if(inclusao) {
			this.inserir(item, itemOld);
		}
		else {
			this.atualizar(item, itemOld);
		}
	}

	public void inserir(AelItemConfigExame item, AelItemConfigExame itemOld) throws BaseException {
		preInserir(item);
		//Chamda modificada para não haver necessidade de utilizar exceção com rollback
		posInserir(item);
		
		getAelItemConfigExameDAO().persistir(item);
	}

	public void atualizar(AelItemConfigExame item, AelItemConfigExame itemOld) throws BaseException {
		getAelItemConfigExameDAO().merge(item);
		posAtualizar(item, itemOld);
	}
	
	public void excluir(AelItemConfigExame item, AelItemConfigExame itemOld) throws BaseException {
		item = getAelItemConfigExameDAO().obterPorChavePrimaria(item.getId());
		itemOld = getAelItemConfigExameDAO().obterPorChavePrimaria(item.getId());
		getAelItemConfigExameDAO().remover(item);
		posDelete(item, itemOld);
	}

	
	/**
	 *  @ORADB : AELT_LUI_ASI
	 */
	public void posInserir(AelItemConfigExame item) throws BaseException {
		atualizaIndLaudoUnico(item, DominioSimNao.S);
		enforceLu3Rules(item);
	}

	/**
	 *  @ORADB : AELT_LUI_ASU
	 *  @ORADB : AELT_LUI_ARU
	 */
	public void posAtualizar(AelItemConfigExame item, AelItemConfigExame itemOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Chamda modificada para não haver necessidade de utilizar exceção com rollback
		enforceLu3Rules(item);
		
		if(CoreUtil.modificados(item.getId(), itemOld.getId())
			|| CoreUtil.modificados(item.getServidor(), itemOld.getServidor())){
			
			AelItemConfigExameJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelItemConfigExameJn.class, servidorLogado.getUsuario());
			jn.setLu2Seq(itemOld.getId().getLu2Seq());
			jn.setLu2Seq(itemOld.getId().getLu2Seq());
			jn.setUfeEmaExaSigla(itemOld.getId().getUfeEmaExaSigla());
			jn.setUfeEmaManSeq(itemOld.getId().getUfeEmaManSeq());
			jn.setUfeUnfSeq(itemOld.getId().getUfeUnfSeq());
			jn.setCriadoEm(itemOld.getCriadoEm());
			jn.setServidor(itemOld.getServidor());
			getAelItemConfigExameJnDAO().persistir(jn);
		}
	}

	
	/**
	 * Caso estiver incluindo uma configuração atualiza para 'S' senao para 'N'
	 * 
	 * @param item
	 * @throws MECBaseException 
	 */
	protected void atualizaIndLaudoUnico(AelItemConfigExame item, DominioSimNao dominioSimNao) throws BaseException {
		AghUnidadesFuncionais aghUnidadesFuncionais = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(item.getId().getUfeUnfSeq());
		
		AelUnfExecutaExames unfExecutaExames = getAelUnfExecutaExamesDAO().obterPorChavePrimaria(
				new AelUnfExecutaExamesId(item.getId().getUfeEmaExaSigla(), item.getId().getUfeEmaManSeq(), aghUnidadesFuncionais ));
		
		unfExecutaExames.setIndLaudoUnico(dominioSimNao);
		
		if (DominioSimNao.N.equals(dominioSimNao)) {
			unfExecutaExames.setIndNumApAnterior(DominioApAnterior.N);
		}
		
		getCadastrosApoioExamesFacade().persistirUnidadeExecutoraExames(unfExecutaExames, null);
	}
	
	/**
	 *  @ORADB : AELT_LU3_ARD
	 */
	public void posDelete(AelItemConfigExame item, AelItemConfigExame itemOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		atualizaIndLaudoUnico(item, DominioSimNao.N);
		
		AelItemConfigExameJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelItemConfigExameJn.class, servidorLogado.getUsuario());
		jn.setLu2Seq(itemOld.getId().getLu2Seq());
		jn.setLu2Seq(itemOld.getId().getLu2Seq());
		jn.setUfeEmaExaSigla(itemOld.getId().getUfeEmaExaSigla());
		jn.setUfeEmaManSeq(itemOld.getId().getUfeEmaManSeq());
		jn.setUfeUnfSeq(itemOld.getId().getUfeUnfSeq());
		jn.setCriadoEm(itemOld.getCriadoEm());
		jn.setServidor(itemOld.getServidor());
		getAelItemConfigExameJnDAO().persistir(jn);		
	}
	
	/**
	 *  @ORADB : AELT_LU3_BRI
	 */
	public void preInserir(AelItemConfigExame item) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelItemConfigExameRNCode.AEL_00353);
		}
		
		item.setCriadoEm(new Date());
		item.setServidor(servidorLogado);
	}

	/**
	 *  @ORADB : AELP_ENFORCE_LU3_RULES
	 */
	public void enforceLu3Rules(AelItemConfigExame item) throws BaseException {
		this.rnLu3pVerOutGrp(item, item.getId().getLu2Seq(), item.getId().getUfeEmaExaSigla(), item.getId().getUfeEmaManSeq(), item.getId().getUfeUnfSeq());
	}

	public AelItemConfigExame clonarItemConfigExame(AelItemConfigExame item) throws BaseException {
		AelItemConfigExame itemClone = null;
		try{
			itemClone = (AelItemConfigExame) BeanUtils.cloneBean(item);
		} catch(Exception e){
			throw new ApplicationBusinessException(AelItemConfigExameRNCode.ERRO_CLONE_ITEM_CONFIG_EXAME);
		}
		
		if(item.getId() != null) {
			itemClone.setId(new AelItemConfigExameId(item.getId().getLu2Seq(), item.getId().getUfeEmaExaSigla(), item.getId().getUfeEmaManSeq(), item.getId().getUfeUnfSeq()));
		}
		
		if(item.getConfigExLaudoUnico() != null) {
			AelConfigExLaudoUnico config = new AelConfigExLaudoUnico();
			config.setSeq(item.getConfigExLaudoUnico().getSeq());
			itemClone.setConfigExLaudoUnico(config);
		}

		if(item.getUnidadeExecutaExame() != null) {
			AelUnfExecutaExames unidade = new AelUnfExecutaExames();
			AelUnfExecutaExamesId id = new AelUnfExecutaExamesId(item.getUnidadeExecutaExame().getId().getEmaExaSigla(), item.getUnidadeExecutaExame().getId().getEmaManSeq(), item.getUnidadeExecutaExame().getId().getUnfSeq());
			unidade.setId(id);
			itemClone.setUnidadeExecutaExame(unidade);
		}

		return itemClone;
	}

	
	/**
	 *  @ORADB : AELK_LU3_RN.RN_LU3P_VER_OUT_GRP
	 */
	public void rnLu3pVerOutGrp(AelItemConfigExame item, Integer lu2Seq, String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) throws BaseException {
		if(getAelItemConfigExameDAO().obterQuantidadeItemConfigExame(lu2Seq, ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq) > 0) {
			// Esse exame já está cadastrado em outro grupo. Verifique.
			throw new ApplicationBusinessException(AelItemConfigExameRNCode.AEL_02733);
		}
	}
	
	
	protected AelItemConfigExameDAO getAelItemConfigExameDAO() {
		return aelItemConfigExameDAO;
	}

	protected AelItemConfigExameJnDAO getAelItemConfigExameJnDAO() {
		return aelItemConfigExameJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}
	
	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return this.cadastrosApoioExamesFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
