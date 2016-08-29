package br.gov.mec.aghu.faturamento;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihTempDAO;
import br.gov.mec.aghu.faturamento.vo.CursorMaximasAtosMedicoAihTempVO;

public class FatAtoMedicoAihTempDAOTest extends AbstractDAOTest<FatAtoMedicoAihTempDAO> {
	
	@Override
	protected FatAtoMedicoAihTempDAO doDaoUnderTests() {
		return new FatAtoMedicoAihTempDAO() {
			private static final long serialVersionUID = -8419234519764620172L;

			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return FatAtoMedicoAihTempDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return FatAtoMedicoAihTempDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}
	
	
	/**
	 * Testa cursor: FATP_SEPARA_ITENS_POR_COMP.RN_QUANTIDADES_MAXIMAS.C_MAXIMAS
	 */
	@Test
	public void listarPorIphCodSusCthGrpSit() {
		if (isEntityManagerOk()) {
			final Short phoSeq = 12; 
			final Integer cthSeq = 432488;
			
			// POSTGRES
			final Integer iphSeq = 2287;
			final Date competencia = DateUtil.obterData(2012, 02, 01);

			// ORACLE
//			final Integer iphSeq = 2113;
//			final Date competencia = DateUtil.obterData(2011, 07, 01);
			
			final Short seqArqSUS = Short.valueOf("999");
			final Byte taoSeq = Byte.valueOf("1");
			
			final List<CursorMaximasAtosMedicoAihTempVO> vos = this.daoUnderTests.obterQtMaximasAtosMedicoAihTemp( phoSeq, iphSeq, 
																												   cthSeq, taoSeq, 
																												   seqArqSUS, competencia );
			
			if(vos != null && !vos.isEmpty()){
				for (CursorMaximasAtosMedicoAihTempVO vo : vos) {
					logger.info(vo);
				}
			}
		}
	}

	@Override
	protected void finalizeMocks() {}
}
