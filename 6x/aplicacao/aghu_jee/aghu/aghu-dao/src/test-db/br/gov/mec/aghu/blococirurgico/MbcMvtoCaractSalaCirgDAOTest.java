package br.gov.mec.aghu.blococirurgico;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoCaractSalaCirgDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;

public class MbcMvtoCaractSalaCirgDAOTest extends AbstractDAOTest<MbcMvtoCaractSalaCirgDAO> {
	
	@Override
	protected MbcMvtoCaractSalaCirgDAO doDaoUnderTests() {
		return new MbcMvtoCaractSalaCirgDAO() {
			private static final long serialVersionUID = 7394349730002060512L;
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return MbcMvtoCaractSalaCirgDAOTest.this.runCriteriaUniqueResult(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
	}


	@Test
	public void testPesquisarUltimoMovimentoDaCaractSalaCirg() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				MbcMvtoCaractSalaCirg result = this.daoUnderTests.pesquisarUltimoMovimentoDaCaractSalaCirg(Short.valueOf("2545"));
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
