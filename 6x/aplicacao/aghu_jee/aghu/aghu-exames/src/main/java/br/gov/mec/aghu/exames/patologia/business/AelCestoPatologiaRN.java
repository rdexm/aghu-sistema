package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelCestoPatologiaDAO;
import br.gov.mec.aghu.exames.dao.AelCestoPatologiaJnDAO;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.model.AelCestoPatologiaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelCestoPatologiaRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelCestoPatologiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelCestoPatologiaDAO aelCestoPatologiaDAO;
	
	@Inject
	private AelCestoPatologiaJnDAO aelCestoPatologiaJnDAO;

	private static final long serialVersionUID = -6588530376472068321L;
	
	public enum AelCestoPatologiaRNExceptionCode implements BusinessExceptionCode {
		AEL_00353
	}
	
	private void bri(final AelCestoPatologia aelCestoPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelCestoPatologia.setCriadoEm(new Date());
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelCestoPatologiaRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelCestoPatologia aelCestoPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelCestoPatologia.setRapServidores(servidorLogado);
		bri(aelCestoPatologia);
		getAelCestoPatologiaDao().persistir(aelCestoPatologia);
	}
	
	private void createJournal(final AelCestoPatologia reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelCestoPatologiaJn journal = BaseJournalFactory.getBaseJournal(operacao,AelCestoPatologiaJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getRapServidores());
		
		getAelCestoPatologiaJnDAO().persistir(journal);
	}
	
	private AelCestoPatologiaJnDAO getAelCestoPatologiaJnDAO(){
		return aelCestoPatologiaJnDAO;
	}

	
	/**
	 * ORADB: AELT_LUK_ARU
	 */
	private void aru(final AelCestoPatologia alterado) throws ApplicationBusinessException{
		final AelCestoPatologia original = getAelCestoPatologiaDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getRapServidores(), original.getRapServidores() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelCestoPatologia aelCestoPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelCestoPatologia.setRapServidores(servidorLogado);
		getAelCestoPatologiaDao().merge(aelCestoPatologia);
		aru(aelCestoPatologia);
	}
	
	protected AelCestoPatologiaDAO getAelCestoPatologiaDao(){
		return aelCestoPatologiaDAO;
	} 

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}