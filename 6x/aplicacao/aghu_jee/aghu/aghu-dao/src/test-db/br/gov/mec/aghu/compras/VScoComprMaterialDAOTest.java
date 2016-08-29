package br.gov.mec.aghu.compras;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.compras.dao.VScoComprMaterialDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;

public class VScoComprMaterialDAOTest extends AbstractDAOTest<VScoComprMaterialDAO> {

	@Override
	protected VScoComprMaterialDAO doDaoUnderTests() {
		return new VScoComprMaterialDAO() {
			private static final long serialVersionUID = -5809170816830663721L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return VScoComprMaterialDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}
	
	@Test
	public void testPesquisaUltimaEntrega() {
		this.daoUnderTests.pesquisaUltimaEntrega(269302);
	}

}
