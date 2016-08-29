package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoLogGeracaoScMatEstocavelDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

/**
 * Teste unitário da classe {@link ScoLogGeracaoScMatEstocavelDAO}.
 * 
 * @author mlcruz
 */
public class ScoLogGeracaoScMatEstocavelDAOTest extends AbstractDAOTest<ScoLogGeracaoScMatEstocavelDAO> {
	
	@Override
	protected ScoLogGeracaoScMatEstocavelDAO doDaoUnderTests() {
		return new ScoLogGeracaoScMatEstocavelDAO() {
			private static final long serialVersionUID = -6129635894535038661L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoLogGeracaoScMatEstocavelDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return ScoLogGeracaoScMatEstocavelDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected boolean isPostgreSQL() {
				return false;
			}
		};
	}

	@Test
	public void testPesquisaProcessoGeracaoCodigoData() {
		doDaoUnderTests().pesquisarProcessoGeracaoCodigoData(null);
	}
	
	@Test
	public void testObtencaoUltimoProcessoGeracao() {
		doDaoUnderTests().obterUltimoProcessoGeracao();
	}
	
	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}