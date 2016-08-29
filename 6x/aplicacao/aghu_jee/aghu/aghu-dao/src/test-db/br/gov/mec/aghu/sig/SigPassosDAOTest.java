package br.gov.mec.aghu.sig;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigPassosDAO;

/**
 * @author rmalvezzi
 */
public class SigPassosDAOTest extends AbstractDAOTest<SigPassosDAO> {

	@Override
	protected SigPassosDAO doDaoUnderTests() {
		return new SigPassosDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigPassosDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Test
	public void listarTodosOrdenadosPelaDescricaoTest() {
		this.getDaoUnderTests().listarTodosOrdenadosPelaDescricao();
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}