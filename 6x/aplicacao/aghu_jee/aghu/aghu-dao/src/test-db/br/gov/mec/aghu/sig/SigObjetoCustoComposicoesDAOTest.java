package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoComposicoesDAO;

/**
 * @author jgugel
 */
public class SigObjetoCustoComposicoesDAOTest extends AbstractDAOTest<SigObjetoCustoComposicoesDAO> {

	@Override
	protected SigObjetoCustoComposicoesDAO doDaoUnderTests() {
		return new SigObjetoCustoComposicoesDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return SigObjetoCustoComposicoesDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigObjetoCustoComposicoesDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void pesquisarObjetoCustoComposicaoAtivoTest(){
		this.getDaoUnderTests().pesquisarObjetoCustoComposicaoAtivo(this.entityManager.find(SigObjetoCustoVersoes.class, 3));
	}
	
	@Test
	public void buscarObjetoDeCustoNutricaoParenteral(){
		this.getDaoUnderTests().buscarObjetoDeCustoNutricaoParenteral(Short.parseShort("1"));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}	
}