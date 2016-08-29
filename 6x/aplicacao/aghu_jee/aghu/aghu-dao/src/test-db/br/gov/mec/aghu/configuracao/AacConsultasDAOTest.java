package br.gov.mec.aghu.configuracao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.faturamento.vo.FatConsultaPrhVO;
import br.gov.mec.aghu.model.AacConsultas;

public class AacConsultasDAOTest extends AbstractDAOTest<AacConsultasDAO> {
	
	@Override
	protected AacConsultasDAO doDaoUnderTests() {
		return new AacConsultasDAO() {
			private static final long serialVersionUID = -5809170816830663721L;

			@Override
			protected Query createHibernateQuery(String query) {
				return AacConsultasDAOTest.this.createHibernateQuery(query);
			}
			
			@Override
			public boolean isOracle() {
				return AacConsultasDAOTest.this.isOracle();
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
	public void testBuscarApacAssociacao() throws BaseException {
		if (isEntityManagerOk()) {
			final Integer codPaciente = 1949623;
			final Date dtInicio = new Date(); // 31/05/00
			dtInicio.setDate(dtInicio.getDate() - (60 * 60 * 24 *11) );
			final Date dtFim = new Date();
			final Short cnvCodigo = 29;
			final Integer codigoTratamento = 0;
			try {
				List<AacConsultas> result = getDaoUnderTests().buscarApacAssociacao(codPaciente, dtInicio, dtFim, cnvCodigo, codigoTratamento);

				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail(e.getMessage());
			}
		}
	}

	@Test
	public void buscarConsultaPorProcedAmbRealizadoEspecialidade() throws BaseException {
		if (isEntityManagerOk()) {
			final Integer codPaciente = 1919058;
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2011);
			dtCalendar.set(Calendar.MONTH, 3);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dtInicioCompetencia = dtCalendar.getTime();
			dtCalendar.set(Calendar.MONTH, 0);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 1);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 0);
			final Date dtInicio = dtCalendar.getTime();
			final Date dtFim = new Date();
			final Short cnvCodigo = 1;
			final Integer codigoTratamento = 27;
			final Byte cpeMes = 5;
			final Short cpeAno = 2011;
			
			if (isEntityManagerOk()) {
				try {
					List<AacConsultas> result = getDaoUnderTests().buscarConsultaPorProcedAmbRealizadoEspecialidade(codPaciente, dtInicioCompetencia, dtInicio,
							dtFim, cnvCodigo, codigoTratamento, cpeMes, cpeAno);
					if (result == null || result.isEmpty()) {
						logger.info("Não retornou registros.");
					} else {
						logger.info("Retornou " + result.size() + " registros.");
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					Assert.fail("Not expecting exception: " + e.getMessage());
				}
			}
		}
	}

	@Test
	public void testBuscarFatConsultaPrhVOAcompanhamento() throws BaseException {
		final Integer codPaciente = 0;
		final Long numeroAtm = 0L;
		final Date dtInicio = new Date();
		final Date dtFim = new Date();
		
		if (isEntityManagerOk()) {
			try {
				final List<FatConsultaPrhVO> result = getDaoUnderTests().buscarFatConsultaPrhVOAcompanhamento(codPaciente, numeroAtm, dtInicio, dtFim);
				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void testBuscarFatConsultaPrhVO() throws BaseException {

		final Integer codPaciente = 0;
		final Long numeroAtm = 0L;
		final Date dtInicio = new Date();
		final Date dtFim = new Date();
		Integer codigoTratamento = 27;
		
		if (isEntityManagerOk()) {
			try {
				final List<FatConsultaPrhVO> result = getDaoUnderTests().buscarFatConsultaPrhVO(codPaciente, numeroAtm, codigoTratamento, dtInicio, dtFim);
				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}

	@Test
	public void obterNumeroDasConsultas(){

		if (isEntityManagerOk()) {
			final Integer pacCodigo = 13839;
			final Date dtTransplante = DateUtil.obterData(2000, 6, 1);
			final Date dtInicio = DateUtil.obterData(2000, 6, 4);
			final Date dtFim = DateUtil.obterData(2000, 6, 31);
			final String ttrCodigo = "FIGADO";
			final DominioIndAbsenteismo absenteismo = DominioIndAbsenteismo.R;
			
			
			final List<Integer> numeros = getDaoUnderTests().obterNumeroDasConsultas( pacCodigo,
																					 dtTransplante, 
																					 dtInicio, dtFim, 
																					 ttrCodigo, absenteismo);
			
			if (numeros != null && !numeros.isEmpty()) {
				for (Integer numero : numeros) {
					logger.info("numero "+numero);
				}
				Assert.assertTrue(true);
			} else {
				Assert.assertFalse(true);
			}
		}
	}
}