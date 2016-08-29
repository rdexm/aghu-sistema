package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatAutorizadoCmaDAO;
import br.gov.mec.aghu.faturamento.vo.CursorCAutorizadoCMSVO;

public class FatAutorizadoCmaDAOTest extends AbstractDAOTest<FatAutorizadoCmaDAO> {
	
	@Override
	protected FatAutorizadoCmaDAO doDaoUnderTests() {
		return new FatAutorizadoCmaDAO() {
			private static final long serialVersionUID = 9081055248442878400L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatAutorizadoCmaDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return FatAutorizadoCmaDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}	
	
	/**
	 * Testa cursor: FATK_CTHN_RN_UN.RN_CTHC_ATU_INS_AAM.C_AUTORIZADO_SMS
	 */
	@Test
	public void buscarPrimeiraPorCthCodSusComSomatorio() {
		if (isEntityManagerOk()) {
			final Integer cthSeq = 430673;
			final Long codSusCma = 206030010L;
			
			final CursorCAutorizadoCMSVO vo = this.daoUnderTests.buscarPrimeiraPorCthCodSusComSomatorio(cthSeq, codSusCma);
			
			logger.info(vo);
			Assert.assertNotNull(vo);
		}
	}

	@Override
	protected void finalizeMocks() {}
}