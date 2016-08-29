package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelMaterialApDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialApJnDAO;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelMaterialApJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelMaterialApRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelMaterialApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AelMaterialApJnDAO aelMaterialApJnDAO;
	
	@Inject
	private AelMaterialApDAO aelMaterialApDAO;

	private static final long serialVersionUID = 4879378548867391256L;
	
	public void persistirAelMaterialAp(final AelMaterialAp aelMaterialAp, AelMaterialAp aelMaterialApOld) throws BaseException {
		if (aelMaterialAp.getSeq() == null) {
			inserirAelMaterialAp(aelMaterialAp);
		}
		else {
			if(aelMaterialApOld == null){
				aelMaterialApOld = getAelMaterialApDAO().obterOriginal(aelMaterialAp.getSeq());
			}
			atualizarAelMaterialAp(aelMaterialAp, aelMaterialApOld);
		}
	}
	
	public void persistirAelMaterialAp(final AelMaterialAp aelMaterialAp) throws BaseException {
		persistirAelMaterialAp(aelMaterialAp, null);
	}
	
	public void persistirAelMaterialAp(final AelMaterialAp aelMaterialAp, final AelMaterialAp aelMaterialApOld, final Boolean isInsert) throws BaseException {
		
		if(aelMaterialApOld != null && !isInsert){
				atualizarAelMaterialAp(aelMaterialAp, aelMaterialApOld);
		}else{
			this.persistirAelMaterialAp(aelMaterialAp);
		}
	}
	
	protected void inserirAelMaterialAp(final AelMaterialAp aelMaterialAp) throws BaseException {
		final AelMaterialApDAO dao = getAelMaterialApDAO();
		this.executarAntesInserirAelMaterialAp(aelMaterialAp);
		dao.persistir(aelMaterialAp);
		dao.flush();
	}
	
	protected void atualizarAelMaterialAp(final AelMaterialAp aelMaterialApNew, final AelMaterialAp aelMaterialApOld) throws BaseException {
		final AelMaterialApDAO dao = getAelMaterialApDAO();
		this.executarAntesAtualizarAelMaterialAp(aelMaterialApNew,aelMaterialApOld);
		dao.merge(aelMaterialApNew);
		this.executarDepoisAtualizarAelMaterialAp(aelMaterialApNew, aelMaterialApOld);
		dao.flush();
	}

	public void excluirAelMaterialAp(AelMaterialAp aelMaterialAp) throws BaseException {
		final AelMaterialApDAO dao = getAelMaterialApDAO();
		this.executarAntesExcluirAelMaterialAp(aelMaterialAp);
		aelMaterialAp = dao.merge(aelMaterialAp);
		dao.remover(aelMaterialAp);
		this.executarAposExcluirAelMaterialAp(aelMaterialAp);
		dao.flush();
	}

	/**
	 * ORADB Trigger AELT_LUR_BRI
	 * 
	 * @param aelMaterialAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelMaterialAp(AelMaterialAp aelMaterialAp) throws ApplicationBusinessException {
		LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		aelMaterialAp.setCriadoEm(new Date());
		
		laudoUnicoRN.aelpAtuServidor(aelMaterialAp);
		
		laudoUnicoRN.rnLurpVerEtapLau(aelMaterialAp.getAelExameAp().getSeq());
		// Retirado pois a criação do materialAp é feita no recebimento de amostra realizado pelo administrativo, e não pelo patologista.
		//laudoUnicoRN.rnLurpVerServidor(aelMaterialAp.getRapServidores());
	}	
	
	/**
	 * ORADB Trigger AELT_LUR_BRU
	 * 
	 * @param aelMaterialApNew
	 * @param aelMaterialApOld
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesAtualizarAelMaterialAp(
			AelMaterialAp aelMaterialApNew, AelMaterialAp aelMaterialApOld) throws ApplicationBusinessException {
		
		LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		laudoUnicoRN.rnLurpVerEtapLau(aelMaterialApNew.getAelExameAp().getSeq());
		// Retirado pois a criação do materialAp é feita no recebimento de amostra realizado pelo administrativo, e não pelo patologista.
		//laudoUnicoRN.rnLurpVerServidor(aelMaterialApNew.getRapServidores());
	}

	/**
	 * ORADB Trigger AELT_LUR_ARU
	 * 
	 * @param aelMaterialApNew
	 * @param aelMaterialApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarDepoisAtualizarAelMaterialAp(
			AelMaterialAp aelMaterialApNew, AelMaterialAp aelMaterialApOld) throws ApplicationBusinessException {
		
		aelMaterialApNew = this.getAelMaterialApDAO().obterPorChavePrimaria(aelMaterialApNew.getSeq());
		aelMaterialApOld = this.getAelMaterialApDAO().obterPorChavePrimaria(aelMaterialApOld.getSeq());
		
		if (CoreUtil.modificados(aelMaterialApNew.getSeq(), aelMaterialApOld.getSeq())
		||  CoreUtil.modificados(aelMaterialApNew.getMaterial(), aelMaterialApOld.getMaterial())
		||  CoreUtil.modificados(aelMaterialApNew.getCriadoEm(), aelMaterialApOld.getCriadoEm())
		||  CoreUtil.modificados(aelMaterialApNew.getAelExameAp(), aelMaterialApOld.getAelExameAp())
		||  CoreUtil.modificados(aelMaterialApNew.getRapServidores(), aelMaterialApOld.getRapServidores())
		||  CoreUtil.modificados(aelMaterialApNew.getOrdem(), aelMaterialApOld.getOrdem())
		||  CoreUtil.modificados(aelMaterialApNew.getItemSolicitacaoExame(), aelMaterialApOld.getItemSolicitacaoExame())) {
			inserirJournal(aelMaterialApOld, DominioOperacoesJournal.UPD);
		}
		
	}

	private void inserirJournal(AelMaterialAp materialAp,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelMaterialApJn jn = new AelMaterialApJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setCriadoEm(new Date());
		jn.setOperacao(operacao);
		jn.setSeq(materialAp.getSeq());
		jn.setAelExameAp(materialAp.getAelExameAp());
		jn.setServidor(materialAp.getRapServidores());
		jn.setOrdem(materialAp.getOrdem());
		jn.setItemSolicitacaoExame(materialAp.getItemSolicitacaoExame());
		
		getAelMaterialApJnDAO().persistir(jn);
	}

	/**
	 * ORADB Trigger AELT_LUR_BRD
	 * 
	 * @param aelMaterialAp
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesExcluirAelMaterialAp(AelMaterialAp aelMaterialAp) throws ApplicationBusinessException {
		getLaudoUnicoRN().rnLurpVerEtapLau(aelMaterialAp.getAelExameAp().getSeq());
	}	
	
	/**
	 * ORADB Trigger AELT_LUR_ARU
	 * 
	 * @param aelMaterialAp
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAposExcluirAelMaterialAp(AelMaterialAp aelMaterialAp) throws ApplicationBusinessException {
		inserirJournal(aelMaterialAp, DominioOperacoesJournal.DEL);
	}

	protected AelMaterialApDAO getAelMaterialApDAO() {
		return aelMaterialApDAO;
	}
	
	protected LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	protected AelMaterialApJnDAO getAelMaterialApJnDAO() {
		return aelMaterialApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
