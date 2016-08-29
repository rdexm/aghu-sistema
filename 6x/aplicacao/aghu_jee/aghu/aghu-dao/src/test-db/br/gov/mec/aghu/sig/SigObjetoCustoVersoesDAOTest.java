package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;

/**
 * @author rmalvezzi
 */
public class SigObjetoCustoVersoesDAOTest extends AbstractDAOTest<SigObjetoCustoVersoesDAO> {

	@Override
	protected SigObjetoCustoVersoesDAO doDaoUnderTests() {
		return new SigObjetoCustoVersoesDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createHibernateQuery(String query) {
				return SigObjetoCustoVersoesDAOTest.this.createHibernateQuery(query);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigObjetoCustoVersoesDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscaInsumosAlocadosAtividadePesoPorRateioTest() {
		this.getDaoUnderTests().buscarInsumosAlocadosAtividadePesoPorRateio(7);
	}

	@Test
	public void buscaObjetosCustoProducaoPHITest() {
		this.getDaoUnderTests().buscaObjetosCustoProducaoPHI(this.entityManager.find(SigProcessamentoCusto.class, 7));
	}

	@Test
	public void buscaObjetosCustoProducaoPacienteTest() {
		this.getDaoUnderTests().buscaObjetosCustoProducaoPaciente(this.entityManager.find(SigProcessamentoCusto.class, 7));
	}

	@Test
	public void buscarListaObjetoCustoAtivoPeloPHITest() {
		this.getDaoUnderTests().buscarListaObjetoCustoAtivoPeloPHI(this.entityManager.find(FatProcedHospInternos.class, 28396));
	}

	@Test
	public void pesquisarObjetoCustoPesoClienteCountTest() {
		this.getDaoUnderTests().pesquisarObjetoCustoPesoClienteCount(null, null, null, null);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}