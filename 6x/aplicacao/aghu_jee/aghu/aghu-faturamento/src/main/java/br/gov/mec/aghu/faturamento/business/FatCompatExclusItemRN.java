package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemJnDAO;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompatExclusItemJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Implementação das seguintes triggers:
 * FATT_ICT_ARD
 * FATT_ICT_ARU
 * FATT_ICT_BRI
 * FATT_ICT_BRU
 */

@Stateless
public class FatCompatExclusItemRN extends BaseBusiness implements
		Serializable {
	
	private static final Log LOG = LogFactory.getLog(FatCompatExclusItemRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatCompatExclusItemJnDAO fatCompatExclusItemJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2828662847369744537L;

	/** ORADB Trigger FATT_ICT_ARD
	 * 
	 * @param fatCompatExclusItem
	 */
	public void executarAposDeletarFatCompatExclusItem(FatCompatExclusItem fatCompatExclusItem){
		this.inserirJournalItemProcedHospitalar(fatCompatExclusItem, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * ORADB Trigger FATT_ICT_ARU
	 * 
	 * @param oldFatCompatExclusItem
	 * @param newFatCompatExclusItem
	 */
	public void executarAposAtualizarFatCompatExclusItem(FatCompatExclusItem oldFatCompatExclusItem,
			FatCompatExclusItem newFatCompatExclusItem) {
		if ((CoreUtil.modificados(oldFatCompatExclusItem.getItemProcedHospCompatibiliza(), newFatCompatExclusItem.getItemProcedHospCompatibiliza())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getItemProcedHosp(), newFatCompatExclusItem.getItemProcedHosp())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getIndComparacao(), newFatCompatExclusItem.getIndComparacao())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getIndCompatExclus(), newFatCompatExclusItem.getIndCompatExclus())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getCriadoEm(), newFatCompatExclusItem.getCriadoEm())) || 
		   (CoreUtil.modificados(oldFatCompatExclusItem.getCriadoPor(), newFatCompatExclusItem.getCriadoPor())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getAlteradoEm(), newFatCompatExclusItem.getAlteradoEm())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getAlteradoPor(), newFatCompatExclusItem.getAlteradoPor())) ||
		   (CoreUtil.modificados(oldFatCompatExclusItem.getObservacao(), newFatCompatExclusItem.getObservacao()))) {
			this.inserirJournalItemProcedHospitalar(oldFatCompatExclusItem, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * ORADB Trigger FATT_ICT_BRI
	 * 
	 * @param fatCompatExclusItem
	 */
	public void executarAntesInserirFatCompatExclusItem(FatCompatExclusItem fatCompatExclusItem) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatCompatExclusItem.setCriadoEm(new Date());
		fatCompatExclusItem.setCriadoPor(servidorLogado.getUsuario());
		fatCompatExclusItem.setAlteradoEm(new Date());
		fatCompatExclusItem.setAlteradoPor(servidorLogado.getUsuario());
	}
	
	/**
	 * ORADB Trigger FATT_ICT_BRU
	 * 
	 * @param fatCompatExclusItem
	 */
	public void executarAntesAtualizarFatCompatExclusItem(FatCompatExclusItem fatCompatExclusItem) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatCompatExclusItem.setAlteradoEm(new Date());
		fatCompatExclusItem.setAlteradoPor(servidorLogado.getUsuario());
	}	

	protected void inserirJournalItemProcedHospitalar(
		FatCompatExclusItem fatCompatExclusItem, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		//AghuFacade aghuFacade = this.getAghuFacade();
		
		//FatCompatExclusItemJn jn = new FatCompatExclusItemJn();
		FatCompatExclusItemJn jn = BaseJournalFactory.getBaseJournal(operacao, FatCompatExclusItemJn.class, servidorLogado.getUsuario());
		
		//jn.setJnUser(servidorLogado.getUsuario());
		//jn.setJnDateTime(new Date());
		//jn.setJnOperation(operacao);
		if (fatCompatExclusItem.getItemProcedHosp() != null) {
			jn.setIphPhoSeq(fatCompatExclusItem.getItemProcedHosp().getId().getPhoSeq());
			jn.setIphSeq(fatCompatExclusItem.getItemProcedHosp().getId().getSeq());
		}
		
		if (fatCompatExclusItem.getItemProcedHospCompatibiliza() != null) {
			jn.setIphPhoSeqCompatibiliza(fatCompatExclusItem.getItemProcedHospCompatibiliza().getId().getPhoSeq());
			jn.setIphSeqCompatibiliza(fatCompatExclusItem.getItemProcedHospCompatibiliza().getId().getSeq());
		}
		
		jn.setIndComparacao(fatCompatExclusItem.getIndComparacao());
		jn.setIndCompatExclus(fatCompatExclusItem.getIndCompatExclus());
		jn.setCriadoEm(fatCompatExclusItem.getCriadoEm());
		jn.setCriadoPor(fatCompatExclusItem.getCriadoPor());
		jn.setAlteradoEm(fatCompatExclusItem.getAlteradoEm());
		jn.setAlteradoPor(fatCompatExclusItem.getAlteradoPor());
		jn.setObservacao(fatCompatExclusItem.getObservacao());
		
		this.getFatCompatExclusItemJnDAO().persistir(jn);
		this.getFatCompatExclusItemJnDAO().flush();
		
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected FatCompatExclusItemJnDAO getFatCompatExclusItemJnDAO() {
		return fatCompatExclusItemJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
