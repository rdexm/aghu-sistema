package br.gov.mec.aghu.blococirurgico;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;

public class MbcProfAtuaUnidCirgsDAOTest extends AbstractDAOTest<MbcProfAtuaUnidCirgsDAO> {
	
	@Override
	protected MbcProfAtuaUnidCirgsDAO doDaoUnderTests() {
		return new MbcProfAtuaUnidCirgsDAO() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8063792239222518884L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcProfAtuaUnidCirgsDAOTest.this.runCriteria(criteria);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void testPesquisarAgendaComControleEscalaCirurgicaDefinitiva() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				List<MbcProfAtuaUnidCirgs> result = this.daoUnderTests.buscarEquipeMedicaParaMudancaNaAgenda("", Short.valueOf("35"), Short.valueOf("17"));
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
