package br.gov.mec.aghu.faturamento;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.faturamento.dao.FatConvFxEtariaItemDAO;

public class FatConvFxEtariaItemDAOTest extends AbstractDAOTest<FatConvFxEtariaItemDAO> {

	@Override
	protected FatConvFxEtariaItemDAO doDaoUnderTests() {
		return new FatConvFxEtariaItemDAO() {
			private static final long serialVersionUID = 4078154411568740060L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatConvFxEtariaItemDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return FatConvFxEtariaItemDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {

	}

	//@Test
	public void testObterListaPorIphCnvDataFaixaEtariaAtiva() {
		if (isEntityManagerOk()) {
			final Short codConvenio = 1;
			final Short iphPhoSeq = 12;
			final Integer iphSeq = 1915;
			//final Calendar calendar = Calendar.getInstance();
			//calendar.set(Calendar.MONTH, 0);
			final Date data = new Date();
			
			List<Byte> result = this.daoUnderTests.obterListaCodSusPorIphCnvDataFaixaEtariaAtiva(codConvenio, iphPhoSeq, iphSeq, data);
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Retornou vazio.");
			} else {
				logger.info("Retornou " + result.size() + " resultados.");
				for (Byte result1 : result) {
					logger.info("CODIGO_SUS = " + result1);
				}
			}
		}
	}

	@Test
	public void testObterListaPorIphCnvIdadeDataFaixaEtariaAtiva() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Short codConvenio = 1;
			final Short iphPhoSeq = 12;
			final Integer iphSeq = 1915;
			//final Calendar calendar = Calendar.getInstance();
			//calendar.set(Calendar.MONTH, 0);
			final Date data = new Date();
			final Short idade = 32;
			
			List<Byte> result = this.daoUnderTests
					.obterListaCodSusPorIphCnvIdadeDataFaixaEtariaAtiva(codConvenio, iphPhoSeq, iphSeq, data, idade);
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Retornou vazio.");
			} else {
				logger.info("Retornou " + result.size() + " resultados.");
				for (Byte result1 : result) {
					logger.info("CODIGO_SUS = " + result1);
				}
			}
		}
	}

	@Override
	protected void finalizeMocks() {
	}
}