package br.gov.mec.aghu.faturamento;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.faturamento.dao.FatSinonimoItemProcedHospDAO;

public class FatSinonimoItemProcedHospDAOTest extends AbstractDAOTest<FatSinonimoItemProcedHospDAO> {

	@Override
	protected FatSinonimoItemProcedHospDAO doDaoUnderTests() {
	     return new FatSinonimoItemProcedHospDAO() {

			private static final long serialVersionUID = -202807494866862531L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatSinonimoItemProcedHospDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return true;
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
    public void obterListaCodTabelaFatSsmTest() {
		if (isEntityManagerOk()) {
	        logger.info("Teste para: FatSinonimoItemProcedHospDAO método obterListaCodTabelaFatSsm.");
	        Long codTabela = 201010119L;
	        Integer idade = 25;
	        Short paramTabelaFaturPadrao = 12;
			
			List<Long> lista = this.daoUnderTests
				.obterListaCodTabelaFatSsm(codTabela, idade, DominioSexoDeterminante.M, paramTabelaFaturPadrao);
	        if (!lista.isEmpty()) {
	            logger.info( " OK " );
	        } else {
	        	logger.info("falhou" );
	        }
		}
    }	
}