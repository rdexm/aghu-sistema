package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalServicoDAO;

/**
 * @author jgugel
 */
public class SigMvtoContaMensalServicoDAOTest extends AbstractDAOTest<SigMvtoContaMensalServicoDAO> {

	@Override
	protected SigMvtoContaMensalServicoDAO doDaoUnderTests() {
		return new SigMvtoContaMensalServicoDAO() {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected Query createHibernateQuery(String query) {
				return SigMvtoContaMensalServicoDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Test
	public void buscarMovimentosServicosCentroCustoParte1Test(){
		this.daoUnderTests.buscarMovimentosServicosCentroCustoParte1(4, 31313, 0);
	}
	
	@Test
	public void buscarMovimentosServicosCentroCustoParte2Test(){
		this.daoUnderTests.buscarMovimentosServicosCentroCustoParte2(4, 31313, 1);
	}
	
	@Test
	public void buscarMovimentosServicosCentroCustoParte3Test(){
		this.daoUnderTests.buscarMovimentosServicosCentroCustoParte3(4, 31313, 2);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}