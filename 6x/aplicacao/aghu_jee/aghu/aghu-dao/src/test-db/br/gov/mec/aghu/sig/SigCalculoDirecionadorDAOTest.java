package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoDirecionadorDAO;

/**
 * @author rmalvezzi
 */
public class SigCalculoDirecionadorDAOTest extends AbstractDAOTest<SigCalculoDirecionadorDAO> {

	@Override
	protected SigCalculoDirecionadorDAO doDaoUnderTests() {
		return new SigCalculoDirecionadorDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigCalculoDirecionadorDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigCalculoDirecionadorDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscarCustosDiretosDosObjetosCustoApoioRatearTest() {
		this.daoUnderTests.buscarCustosDiretosDosObjetosCustoApoioRatear(14);
	}

	@Test
	public void buscarCustosIndiretosRatearObjetosCustoApoioTest() {
		this.daoUnderTests.buscarCustosIndiretosRatearObjetosCustoApoio(14);
	}

	@Test
	public void buscarValoresIndiretosObjetoCustoApoioTest() {
		this.daoUnderTests.buscarValoresIndiretosObjetoCustoApoio(this.entityManager.find(SigProcessamentoCusto.class, 14));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}