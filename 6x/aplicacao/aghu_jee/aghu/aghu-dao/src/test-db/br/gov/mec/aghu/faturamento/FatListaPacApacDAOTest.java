package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatListaPacApacDAO;
import br.gov.mec.aghu.model.FatListaPacApac;

public class FatListaPacApacDAOTest extends AbstractDAOTest<FatListaPacApacDAO> {
	
	@Override
	protected FatListaPacApacDAO doDaoUnderTests() {
		return new FatListaPacApacDAO() {
			private static final long serialVersionUID = -2220285789400697606L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatListaPacApacDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return FatListaPacApacDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {}
	
	@Test
	public void obterCursorAtosMedicosVOs(){
		if (isEntityManagerOk()) {
		    
		    final List<FatListaPacApac> vos = daoUnderTests.buscarFatListaPacApac();
		    logger.info(vos.size());
		    Assert.assertNotNull(vos);
		}
	}
	 
	@Override
	protected void finalizeMocks() {}
}