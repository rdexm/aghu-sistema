package br.gov.mec.aghu.faturamento;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.dao.FatResumoApacsDAO;

public class FatResumoApacsDAOTest extends AbstractDAOTest<FatResumoApacsDAO> {

	@Override
	protected FatResumoApacsDAO doDaoUnderTests() {
		return new FatResumoApacsDAO() {
			private static final long serialVersionUID = 3913095100011148044L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatResumoApacsDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Test
	public void testBuscarDataFinalResumosApacsAtivos() {
		if (isEntityManagerOk()) {
			final Integer codPaciente = 1646008;
			final Calendar dtRealizado = Calendar.getInstance();
			dtRealizado.set(Calendar.MONTH, -180); // - c_dias
			dtRealizado.set(Calendar.DAY_OF_MONTH, 5);//calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			dtRealizado.set(Calendar.HOUR_OF_DAY, 0);
			dtRealizado.set(Calendar.MINUTE, 0);
			dtRealizado.set(Calendar.SECOND, 0);
			dtRealizado.set(Calendar.MILLISECOND, 0);

			List<Date> result = this.daoUnderTests.buscarDataFinalResumosApacsAtivos(codPaciente, dtRealizado.getTime(), DominioSimNao.S);
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
				for (Date result1 : result) {
					logger.info("Data = " + result1);
				}
			}
		}
	}

	@Override
	protected void finalizeMocks() {
	}
}