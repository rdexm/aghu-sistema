package br.gov.mec.aghu.sig;

import javax.persistence.Query;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigProducaoObjetoCustoDAO;

/**
 * @author rmalvezzi
 */
public class SigProducaoObjetoCustoDAOTest extends AbstractDAOTest<SigProducaoObjetoCustoDAO> {

	@Override
	protected SigProducaoObjetoCustoDAO doDaoUnderTests() {
		return new SigProducaoObjetoCustoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createQuery(String query) {
				return SigProducaoObjetoCustoDAOTest.this.createQuery(query);
			}
		};
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}