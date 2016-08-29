package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSimNao;

/**
 * Teste unitário da classe {@link ScoMaterialDAO}.
 * 
 * @author mlcruz
 */
public class ScoMaterialDAOTest extends AbstractDAOTest<ScoMaterialDAO> {
	
	@Override
	protected ScoMaterialDAO doDaoUnderTests() {
		return new ScoMaterialDAO() {
			private static final long serialVersionUID = -1169477820141135522L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoMaterialDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoMaterialDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Long executeCriteriaCount(DetachedCriteria criteria) {
				return ScoMaterialDAOTest.this.runCriteriaCount(criteria);
			}
		};
	}

	@Test
	public void testObtencaoMateriaisRMAutomatica() {
		doDaoUnderTests().obterMateriaisRMAutomatica(null, null);
	}
	
	@Test
	public void testExistenciaMaterialEstocavelPorAlmoxarifadoCentral() {
		doDaoUnderTests().existeMaterialEstocavelPorAlmoxarifadoCentral(0, (short) 0, true);
	}
	
	@Test
	public void testPesquisaListaMateriaisParaCatalogo() {
		doDaoUnderTests()
				.pesquisarListaMateriaisParaCatalogo(0, 10, null, true, null,
						null, null, DominioSimNao.S, null, null, null, null, null, null,null);
	}
	
	@Test
	public void testPesquisaScoMateriaisRelMensal() {
		doDaoUnderTests().pesquisaScoMateriaisRelMensal(1, DominioSimNao.N);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}