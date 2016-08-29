package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.sig.dao.SigCalculoClienteDAO;

/**
 * 
 * @author rmalvezzi
 *
 */
public class SigCalculoClienteDAOTest extends AbstractDAOTest<SigCalculoClienteDAO> {

	@Override
	protected SigCalculoClienteDAO doDaoUnderTests() {
		return new SigCalculoClienteDAO() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria, boolean cacheable) {
				return SigCalculoClienteDAOTest.this.runCriteriaUniqueResult(criteria, cacheable);
			}
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return SigCalculoClienteDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria, boolean cacheble) {
				return SigCalculoClienteDAOTest.this.runCriteria(criteria, cacheble);
			}
			
			@Override
			protected javax.persistence.Query createQuery(String query) {
				return SigCalculoClienteDAOTest.this.createQuery(query);
			};
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigCalculoClienteDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscaSomaPesosClientesIntermediariosFinalisticosObjetoCustoApoioTest() {
		this.daoUnderTests.buscarSomaPesosClientesIntermediariosFinalisticosObjetoCustoApoio(this.entityManager.find(SigCalculoObjetoCusto.class, 19000),
				this.entityManager.find(SigDirecionadores.class, 1));

	}

	@Test
	public void buscaClientesIntermediariosFinalisticosObjetoCustoApoioTest() {
		this.daoUnderTests.buscarClientesIntermediariosFinalisticosObjetoCustoApoio(this.entityManager.find(SigCalculoObjetoCusto.class, 19000),
				this.entityManager.find(SigDirecionadores.class, 1));
	}

	@Test
	public void buscaSomaPesosClienesObjetoCustoTest() {
		this.daoUnderTests.buscarSomaPesosClienesObjetoCusto(this.entityManager.find(SigCalculoObjetoCusto.class, 19000),
				this.entityManager.find(SigDirecionadores.class, 1));

	}

	@Test
	public void buscaClientesObjetoCustoApoioRatearTest() {
		this.daoUnderTests.buscarClientesObjetoCustoApoioRatear(this.entityManager.find(SigCalculoObjetoCusto.class, 19000),
				this.entityManager.find(SigDirecionadores.class, 1));
	}

	@Test
	public void removerSigCalculoClienteByIdProcessamentoCustoTest() {
		this.daoUnderTests.removerPorProcessamento(6);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}