package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatTipoTratamentosDAO;
import br.gov.mec.aghu.faturamento.vo.CursorTipoListaPmrKitPreVO;

public class FatTipoTratamentosDAOTest extends AbstractDAOTest<FatTipoTratamentosDAO> {
	
	@Override
	protected FatTipoTratamentosDAO doDaoUnderTests() {
		return new FatTipoTratamentosDAO() {
			private static final long serialVersionUID = -2220285789400697606L;

			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return FatTipoTratamentosDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return FatTipoTratamentosDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {}
	
	@Test
	public void obterCursorCPmr1(){
		if(isEntityManagerOk()){
			Short cpgCphCspCnvCodigo = Short.valueOf("1");
			Byte cpgCphCspSeq1 = Byte.valueOf("1");
			Byte cpgCphCspSeq2= Byte.valueOf("2");
			Integer tptSeq = 26;
			boolean indListaCandidato = true;
			
			final List<CursorTipoListaPmrKitPreVO> vos = daoUnderTests.obterCursorCTipoLista( cpgCphCspCnvCodigo, cpgCphCspSeq1,
																						      cpgCphCspSeq2, tptSeq, indListaCandidato); 
			
			logger.info(vos.size());
		    Assert.assertNotNull(vos);
		}
	}

	@Override
	protected void finalizeMocks() {}
}