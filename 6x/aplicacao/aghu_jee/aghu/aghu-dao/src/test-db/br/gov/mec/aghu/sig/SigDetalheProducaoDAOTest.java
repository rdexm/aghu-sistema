package br.gov.mec.aghu.sig;

import java.util.Date;
import java.util.List;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigDetalheProducaoDAO;

/**  
 * @author rmalvezzi
 */
public class SigDetalheProducaoDAOTest extends AbstractDAOTest<SigDetalheProducaoDAO> {

	SigProcessamentoCusto sigProcessamentoCusto;
	
	@Override
	protected SigDetalheProducaoDAO doDaoUnderTests() {
		return new SigDetalheProducaoDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return SigDetalheProducaoDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigDetalheProducaoDAOTest.this.runCriteria(criteria);
			}

			@Override
			protected ScrollableResults createScrollableResults(
					DetachedCriteria criteria, Integer fetchSize,
					ScrollMode scrollMode) {
				return SigDetalheProducaoDAOTest.this.createScrollableResults(criteria, fetchSize, scrollMode);
			}
		};
	}
	
	@Test 
	public void pesquisarProducaoTest(){
		this.doDaoUnderTests().pesquisarProducao(0, 15, null, true, this.entityManager.find(FccCentroCustos.class, 31313), null, null, null);
	}

	@Test
	public void buscaProducaoExamesPorMesCompetenciaTest() {
		this.doDaoUnderTests().buscarProducaoExamesPorMesCompetencia(sigProcessamentoCusto);
	}
	
	@Test
	public void pesquisarDetalheProducao() {
		this.doDaoUnderTests().pesquisarDetalheProducao(this.entityManager.find(SigProcessamentoCusto.class, 9), this.entityManager.find(FccCentroCustos.class, 31313), this.entityManager.find(SigObjetoCustoVersoes.class, 90));
	}

	@Override
	protected void initMocks() {
		sigProcessamentoCusto = new SigProcessamentoCusto(4);
		sigProcessamentoCusto.setDataInicio(new Date());
		sigProcessamentoCusto.setCompetencia(new Date());
		sigProcessamentoCusto.setDataFim(new Date());
	}

	@Override
	protected void finalizeMocks() {
	}
}