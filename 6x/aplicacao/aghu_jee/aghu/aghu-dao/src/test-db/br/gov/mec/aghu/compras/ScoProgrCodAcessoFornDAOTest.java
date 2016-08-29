package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.ScoProgrCodAcessoFornDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;

/**
 * @author rafael
 *
 */
public class ScoProgrCodAcessoFornDAOTest extends AbstractDAOTest<ScoProgrCodAcessoFornDAO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8093143248951177785L;

	@Override
	protected ScoProgrCodAcessoFornDAO doDaoUnderTests() {
		return new ScoProgrCodAcessoFornDAO() {

			private static final long serialVersionUID = -2044344636208134132L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return ScoProgrCodAcessoFornDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}
	
	@Test
	public void testConsultaAcessos() {
		ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setNumero(88);
		
		List<ScoProgrCodAcessoForn> resultado = this.doDaoUnderTests().listarFornecedores(fornecedor, 10, 10, null, false);
		
		Assert.assertNotNull(resultado);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}

}
