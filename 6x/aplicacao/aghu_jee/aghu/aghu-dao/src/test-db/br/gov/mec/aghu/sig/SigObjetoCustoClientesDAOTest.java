package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;

/**
 * @author jgugel
 */
public class SigObjetoCustoClientesDAOTest extends AbstractDAOTest<SigObjetoCustoClientesDAO> {

	@Override
	protected SigObjetoCustoClientesDAO doDaoUnderTests() {
		return new SigObjetoCustoClientesDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigObjetoCustoClientesDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigObjetoCustoClientesDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscarClientesRateioObjetoCustoTest() {
		this.daoUnderTests.buscarClientesRateioObjetoCusto(14);
	}

	@Test
	public void buscarClientesAtivosPorDirecionadorTest() {
		this.daoUnderTests.buscarClientesAtivosPorDirecionador(this.entityManager.find(SigDirecionadores.class, 17));
	}

	@Test
	public void buscarClientesAtivosComTodosCentrosCustoPorDirecionadoresTest() {
		this.daoUnderTests.buscarClientesAtivosComIndicadorTodosCentrosCustoPorDirecionadores(new SigDirecionadores[] { this.entityManager.find(SigDirecionadores.class, 17) });
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}