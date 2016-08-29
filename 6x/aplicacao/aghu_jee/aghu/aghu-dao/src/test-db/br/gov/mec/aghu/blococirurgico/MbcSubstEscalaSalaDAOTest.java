package br.gov.mec.aghu.blococirurgico;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;

public class MbcSubstEscalaSalaDAOTest extends AbstractDAOTest<MbcSubstEscalaSalaDAO> {
	
	@Override
	protected MbcSubstEscalaSalaDAO doDaoUnderTests() {
		return new MbcSubstEscalaSalaDAO() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6536607079488461531L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcSubstEscalaSalaDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {

	}

	@Test
	public void testPesquisarCedenciaSala() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			Date dataBase = DateUtil.obterData(2010, Calendar.SEPTEMBER, 27);
			Short casSeq = Short.valueOf("192");
			Short espSeq = Short.valueOf("33");
			Short seqp = Short.valueOf("1");
					
			try {
				List<Short> result = this.daoUnderTests.pesquisarCedenciaSala(dataBase, casSeq, espSeq, seqp);
				logger.info("###############################################");
				if (result == null) {
					logger.info("Retornou null.");
				} else {
					logger.info("Resultado = " + result);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	
	@Override
	protected void finalizeMocks() {
		
	}
}
