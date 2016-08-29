package br.gov.mec.aghu.compras;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.vo.PosicaoFinalEstoqueVO;


/**
 * Teste unitário da classe {@link SceEstoqueGeralDAO}.
 * 
 * @author mlcruz
 */
public class SceEstoqueGeralDAOTest extends
		AbstractDAOTest<SceEstoqueGeralDAO> {
	
	private static final Log LOG = LogFactory.getLog(SceEstoqueGeralDAOTest.class);
	
	@Override
	protected SceEstoqueGeralDAO doDaoUnderTests() {
		return new SceEstoqueGeralDAO() {
			private static final long serialVersionUID = -1169477820141135522L;
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return SceEstoqueGeralDAOTest.this.runCriteriaCount(criteria);
			}

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return SceEstoqueGeralDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return SceEstoqueGeralDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SceEstoqueGeralDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void testObterQuantidadeRegistrosPorDataCompetencia() {
		LOG.info("testObterQuantidadeRegistrosPorDataCompetencia");
		doDaoUnderTests().obterQuantidadeRegistrosPorDataCompetencia(new Date());
	}
	
	@Test
	public void testPesquisaEstoqueGeral() {
		LOG.info("testPesquisaEstoqueGeral");
		doDaoUnderTests().pesquisarEstoqueGeral(0, 10, null, true, null, null,
				null, null, null, null, true, false, null, null, null);
	}
	
	@Test
	public void testBuscaDadosPosicaoFinalEstoque() throws BaseException {
		LOG.info("testBuscaDadosPosicaoFinalEstoque");
		List<PosicaoFinalEstoqueVO> result = doDaoUnderTests()
				.buscaDadosPosicaoFinalEstoque(new Date(), null, "S", null, null,
						null, null);
		
		for (PosicaoFinalEstoqueVO item : result) {
			item.getIndEstocavel();
		}
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}