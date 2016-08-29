package br.gov.mec.aghu.sig;

import javax.persistence.Query;

import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoDirRateiosDAO;

/**
 * @author rmalvezzi
 */
public class SigObjetoCustoDirRateiosDAOTest extends AbstractDAOTest<SigObjetoCustoDirRateiosDAO> {

	@Override
	protected SigObjetoCustoDirRateiosDAO doDaoUnderTests() {
		return new SigObjetoCustoDirRateiosDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createQuery(String query) {
				return SigObjetoCustoDirRateiosDAOTest.this.createQuery(query);
			}

			@Override
			protected org.hibernate.Query createHibernateQuery(String query) {
				return SigObjetoCustoDirRateiosDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscarDirecionadoresRateioObjetoCustoTest() {
		this.daoUnderTests.buscarDirecionadoresRateioObjetoCusto(40);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}