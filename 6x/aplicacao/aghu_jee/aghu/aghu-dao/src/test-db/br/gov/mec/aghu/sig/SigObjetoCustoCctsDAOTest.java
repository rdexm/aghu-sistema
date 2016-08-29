package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;

/**
 * @author rmalvezzi
 */
public class SigObjetoCustoCctsDAOTest extends AbstractDAOTest<SigObjetoCustoCctsDAO> {

	@Override
	protected SigObjetoCustoCctsDAO doDaoUnderTests() {
		return new SigObjetoCustoCctsDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigObjetoCustoCctsDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscarClientesRateioObjetoCustoSemValorTest() {
		this.daoUnderTests.buscarClientesRateioObjetoCustoSemValor();
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}