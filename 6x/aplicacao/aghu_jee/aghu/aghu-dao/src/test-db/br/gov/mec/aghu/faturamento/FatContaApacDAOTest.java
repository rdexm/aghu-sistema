package br.gov.mec.aghu.faturamento;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.model.FatContaApac;

public class FatContaApacDAOTest extends AbstractDAOTest<FatContaApacDAO> {

	@Override
	protected FatContaApacDAO doDaoUnderTests() {
		return new FatContaApacDAO() {
			private static final long serialVersionUID = 3541608372093892098L;
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatContaApacDAOTest.this.runCriteria(criteria);
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
	public void testObterCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao() {
		if (isEntityManagerOk()) {
			final DominioModuloCompetencia modulo = DominioModuloCompetencia.AMB;
			final Byte cpeMes = 5;
			final Short cpeAno = 2011;
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2011);
			dtCalendar.set(Calendar.MONTH, 3);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dtHrInicio = dtCalendar.getTime();
			List<FatContaApac> result = getDaoUnderTests().buscarFatContaApacAtivaPorModuloDtInicioMesAno(modulo, dtHrInicio, cpeMes, cpeAno); 
			if (result == null) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou 1 registro.");
			}
		}
	}
}