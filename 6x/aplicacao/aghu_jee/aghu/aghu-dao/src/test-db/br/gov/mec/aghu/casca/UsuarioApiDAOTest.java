package br.gov.mec.aghu.casca;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.casca.dao.UsuarioApiDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;


public class UsuarioApiDAOTest extends AbstractDAOTest<UsuarioApiDAO> {
	
	private static final Log LOG = LogFactory.getLog(UsuarioApiDAOTest.class);
	
	@Override
	protected UsuarioApiDAO doDaoUnderTests() {
		return new UsuarioApiDAO() {
			private static final long serialVersionUID = -1169477820141134222L;
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return UsuarioApiDAOTest.this.runCriteriaCount(criteria);
			}

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return UsuarioApiDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return UsuarioApiDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return UsuarioApiDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return UsuarioApiDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void testObterUsuarioApi() {
		LOG.info("testObterUsuarioApi");
		doDaoUnderTests().obterUsuarioApi("79ecc9953k9aa8983k15");
	}
	
	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}