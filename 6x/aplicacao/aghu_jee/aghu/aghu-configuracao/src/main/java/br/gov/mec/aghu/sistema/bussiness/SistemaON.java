package br.gov.mec.aghu.sistema.bussiness;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SistemaON extends BaseBMTBusiness {

	private static final Log LOG = LogFactory.getLog(SistemaON.class);

	private static final long serialVersionUID = -135597083821717225L;

	@Inject
	private DataAccessService dataAccessService; 
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
//	public Directory getLuceneDirectory(Class clazz) {
//		Session session = getSession();
//		session.getSessionFactory().openSession();
//		FullTextSession fullTextSession = Search.getFullTextSession(session);
//
//		return fullTextSession.getSearchFactory().getDirectoryProviders(clazz)[0]
//				.getDirectory();
//	}
	
	public void indexar(Class clazz) throws InterruptedException {
		this.beginTransaction(60 * 60 * 12); // 12 horas
		dataAccessService.indexar(clazz);
		this.commitTransaction();
	}

	
}
