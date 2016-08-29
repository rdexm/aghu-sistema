package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAnalisesDAO;

/**
 * @author rmalvezzi
 */
public class SigProcessamentoAnalisesDAOTest extends AbstractDAOTest<SigProcessamentoAnalisesDAO> {

	@Override
	protected SigProcessamentoAnalisesDAO doDaoUnderTests() {
		return new SigProcessamentoAnalisesDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigProcessamentoAnalisesDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void buscaTotalAnalisesSemParecerTest() {
		this.getDaoUnderTests().buscarTotalAnalisesSemParecer(this.entityManager.find(SigProcessamentoCusto.class, 1));
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}

}