package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

/**
 * 
 * @author cvagheti
 */
public class ScoAutorizacaoFornJnDAOTest extends AbstractDAOTest<ScoAutorizacaoFornJnDAO> {
	
	@Override
	protected ScoAutorizacaoFornJnDAO doDaoUnderTests() {
		return new ScoAutorizacaoFornJnDAO() {
			private static final long serialVersionUID = 723792094616755271L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoAutorizacaoFornJnDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return ScoAutorizacaoFornJnDAOTest.this.runCriteriaUniqueResult(criteria);
			}
		};
	}

	/**
	 * Testa busca de versões de AF por número do PAC e complemento.
	 */
	@Test
	public void testBuscaAutFornJNPorNumPacNumCompl() {
		doDaoUnderTests().buscarAutFornJNPorNumPacNumCompl(35, (short) 1, 0, 10);
	}
	
	/**
	 * Testa obtenção de versão de AF pelo ID.
	 */
	@Test
	public void testObtencaoScoAutorizacaoFornJn() {
		doDaoUnderTests().obterScoAutorizacaoFornJn(1,(short)1,(short)1);
	}
	
	/**
	 * Testa obtenção de responsáveis de versão de AF pelo ID dela.
	 */
	@Test
	public void testObtencaoResponsaveisAutorizacaoFornJn() {
		doDaoUnderTests().obterResponsaveisAutorizacaoFornJn(1,(short)1,(short)1);
	}
	
	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}