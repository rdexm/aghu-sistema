package br.gov.mec.aghu.sig;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.sig.dao.SigCalculoDetalheConsumoDAO;

/**
 * 
 * @author rmalvezzi
 *
 */
public class SigCalculoDetalheConsumoDAOTest  extends AbstractDAOTest<SigCalculoDetalheConsumoDAO>{

	@Override
	protected SigCalculoDetalheConsumoDAO doDaoUnderTests() {
		return new SigCalculoDetalheConsumoDAO(){

			private static final long serialVersionUID = 1L;
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return SigCalculoDetalheConsumoDAOTest.this.runCriteriaUniqueResult(criteria);
			}
		};
	}
	
	@Test
	public void testeBuscarItemConsumo(){
		this.daoUnderTests.buscarItemConsumo(this.entityManager.find(SigCalculoAtdConsumo.class, 1), this.entityManager.find(FatProcedHospInternos.class, 4088));
	}
	
	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}