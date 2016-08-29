package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoObjetoCustoDAO;

/**
 * @author rogeriovieira
 */
public class SigCalculoObjetoCustoDAOTest extends AbstractDAOTest<SigCalculoObjetoCustoDAO> {

	@Override
	protected SigCalculoObjetoCustoDAO doDaoUnderTests() {
		return new SigCalculoObjetoCustoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigCalculoObjetoCustoDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigCalculoObjetoCustoDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void pesquisarCalculoObjetoCustoTest() {
		this.daoUnderTests.pesquisarCalculoObjetoCustoSubProduto(7);
	}

	@Test
	public void buscaObjetosCustoComProducaoParaRateioTest() {
		this.daoUnderTests.buscarObjetosCustoComProducaoParaRateio(7, 31330);
	}

	@Test
	public void buscarCustosContratoTest() {
		this.daoUnderTests.buscarCustosContrato(40);
	}

	@Test
	public void buscarCalculosObjetoCustoPorCompetenciaTest() {
		this.daoUnderTests.buscarCalculosObjetoCustoPorCompetencia(this.entityManager.find(SigProcessamentoCusto.class, 7));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}