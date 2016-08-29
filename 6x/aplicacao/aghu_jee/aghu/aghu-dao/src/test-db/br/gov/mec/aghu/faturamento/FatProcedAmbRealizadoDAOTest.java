package br.gov.mec.aghu.faturamento;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoProcedAmbVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadoVO;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;

public class FatProcedAmbRealizadoDAOTest extends AbstractDAOTest<FatProcedAmbRealizadoDAO> {
	
	@Override
	protected FatProcedAmbRealizadoDAO doDaoUnderTests() {
		return new FatProcedAmbRealizadoDAO() {
			private static final long serialVersionUID = -8375800556965130174L;

			@Override
			public boolean isOracle() {
				return FatProcedAmbRealizadoDAOTest.this.isOracle();
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatProcedAmbRealizadoDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return FatProcedAmbRealizadoDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return FatProcedAmbRealizadoDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	@Override
	protected void initMocks() {
		int x = 0;
		logger.info("inicializando mocks");
		if (x > 0) {
			logger.info("x > 0");
		}
	}

	@Test
	public void listarProcedAmbRealizadoPorSituacaoModuloDthrInicioMesAnoGrupoConvenio() {

		List<FatProcedAmbRealizado> result = null;

		if (isEntityManagerOk()) {
			// assert
			// sem dados
			result = this.daoUnderTests.listarProcedAmbRealizadoPorSituacaoModuloDthrInicioMesAnoGrupoConvenio(
					DominioSituacaoProcedimentoAmbulatorio.ABERTO, DominioModuloCompetencia.AMB, new Date(), 1, 2500, DominioGrupoConvenio.C);

			// com dados
			/*
			 * result = this.dao.
			 * listarProcedAmbRealizadoPorSituacaoModuloDthrInicioMesAnoGrupoConvenio
			 * ( DominioSituacaoProcedimentoAmbulatorio.ABERTO,
			 * DominioModuloCompetencia.AMB, DateUtil.obterData(2010, 06,
			 * 31, 20, 00), 8, 2010, DominioGrupoConvenio.S);
			 */
			Assert.assertTrue(result.isEmpty());
		}
	}

	@Test
	public void listarRegistrosMesProcessamento() {

		List<FatProcedAmbRealizado> result = null;

		if (isEntityManagerOk()) {
			// assert
			// sem dados
			result = this.daoUnderTests.listarRegistrosMesProcessamento(new DominioSituacaoProcedimentoAmbulatorio[] {
					DominioSituacaoProcedimentoAmbulatorio.ABERTO, DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO },
					new DominioLocalCobrancaProcedimentoAmbulatorialRealizado[] { DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B,
							DominioLocalCobrancaProcedimentoAmbulatorialRealizado.A }, new Integer[] { 5967, 5968, 5969, 5770, 5971, 5973, 5976,
							5979, 5981, 5983, 5984, 5985, 5986, 5987, 5993, 5771, 5995, 5994, 5978 }, (byte) 2, // 2
					(short) 1, // 1
					DominioOrigemProcedimentoAmbulatorialRealizado.BSA, new Date(), new Date(), Boolean.FALSE);

			Assert.assertTrue(result.isEmpty());
		}
	}

	@Test
	public void listarRegistrosMesComQtdeZero() {

		List<FatProcedAmbRealizado> result = null;

		if (isEntityManagerOk()) {
			// assert
			// sem dados
			result = this.daoUnderTests.listarRegistrosMesComQtdeZero(DominioSituacaoProcedimentoAmbulatorio.ABERTO,
					DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B, new Integer[] { 5967, 5968, 5969, 5770, 5971, 5973, 5976, 5979,
							5981, 5983, 5984, 5985, 5986, 5987, 5993, 5771, 5995, 5994, 5978 }, (byte) 2, // 2
					(short) 1, // 1
					DominioOrigemProcedimentoAmbulatorialRealizado.BSA);

			Assert.assertTrue(result.isEmpty());
		}
	}

	@Test
	public void testBuscarPorConsultaAtendimento() {
		if (isEntityManagerOk()) {
			Integer numero = 17654335;
			Integer seqAtendimento = 8818757;
			List<FatProcedAmbRealizado> result = this.daoUnderTests.buscarPorConsultaAtendimento(numero, seqAtendimento);
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
		}
	}

	@Test
	public void testBuscarPendentesPorDtInicioAnoMes() {
		if (isEntityManagerOk()) {
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2011);
			dtCalendar.set(Calendar.MONTH, 3);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dataInicio = dtCalendar.getTime();
			final Byte mes = 5;
			final Short ano = 2011;
			final List<FatProcedAmbRealizado> result = this.daoUnderTests.buscarPendentesPorDtInicioAnoMes(dataInicio, mes, ano);
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
		}
	}

	@Test
	public void testBuscarAbertosPorDtInicioAnoMesUnid() {
		if (isEntityManagerOk()) {
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2012);
			dtCalendar.set(Calendar.MONTH, 0);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 20);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 11);
			dtCalendar.set(Calendar.MINUTE, 55);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dataInicio = dtCalendar.getTime();
			
			//dtCalendar.set(Calendar.MONTH, 0);
			//dtCalendar.set(Calendar.DAY_OF_MONTH, 1);
			//dtCalendar.set(Calendar.HOUR_OF_DAY, 0);
			final Date dataLiberadoEmergencia = new Date();
			final Byte mes = 12;
			final Short ano = 2011;
			final List<FatProcedAmbRealizado> result = this.daoUnderTests.buscarAbertosPorDtInicioAnoMesUnid(dataInicio, mes, ano,
					dataLiberadoEmergencia, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
		}
	}

	@Test
	public void testBuscarPorAtendimento() {
		if (isEntityManagerOk()) {
			final List<FatProcedAmbRealizado> result = this.daoUnderTests.buscarPorAtendimento(2012995);
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
		}
	}

	@Test
	public void testBuscarPorPacienteEmAtendimento() {
		if (isEntityManagerOk()) {
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2011);
			dtCalendar.set(Calendar.MONTH, 3);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dataInicio = dtCalendar.getTime();
			dtCalendar.set(Calendar.MONTH, 0);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 1);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 0);
			final Date dataLiberadoEmergencia = dtCalendar.getTime();
			final Byte mes = 5;
			final Short ano = 2011;
			final List<FatProcedAmbRealizado> result = this.daoUnderTests.buscarPorPacienteEmAtendimento(dataInicio, mes, ano,
					dataLiberadoEmergencia);
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
		}
	}

	@Test
	public void obterFatProcedAmbRealizadoPorPrhConNumeroTest() {
		if (isEntityManagerOk()) {
			FatProcedAmbRealizado ambRealizado = this.daoUnderTests.obterFatProcedAmbRealizadoPorPrhConNumero(null);
			Assert.assertTrue(ambRealizado == null);
		}
	}

	@Test
	public void testBuscarPorDataGrupoCaracteristica() {
		if (isEntityManagerOk()) {
			final String DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDO = "dd/MM/yyyy HH:mm:ss";
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.DAY_OF_MONTH, 31);
			dtCalendar.set(Calendar.MONTH, 9);
			dtCalendar.set(Calendar.YEAR, 2012);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dataInicio = dtCalendar.getTime();
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.MONTH, 10);
			dtCalendar.set(Calendar.YEAR, 2012);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 23);
			dtCalendar.set(Calendar.MINUTE, 59);
			final Date dataFim = dtCalendar.getTime();
			final List<FatProcedAmbRealizadoVO> result = this.daoUnderTests.buscarPorDataGrupoCaracteristica(dataInicio, dataFim, "Quantidade minima grupo",
					(short)6, DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDO);
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
				for (FatProcedAmbRealizadoVO fatProcedAmbRealizadoVO : result) {
					logger.info("Seq " + fatProcedAmbRealizadoVO.getSeq());
//						logger.info("CspCnvCodigo " + fatProcedAmbRealizadoVO.getCspCnvCodigo());
//						logger.info("CspSeq " + fatProcedAmbRealizadoVO.getCspSeq());
//						logger.info("EspSeq " + fatProcedAmbRealizadoVO.getEspSeq());
//						logger.info("PhiSeq " + fatProcedAmbRealizadoVO.getPhiSeq());
//						logger.info("UnfSeq " + fatProcedAmbRealizadoVO.getUnfSeq());
//						logger.info("ValorNumerico " + fatProcedAmbRealizadoVO.getValorNumerico());
//						logger.info("DthrRealizado " + fatProcedAmbRealizadoVO.getDthrRealizado());
				}
			}
		}
	}

	@Test
	public void testBuscarPorSituacaoData() {
		if (isEntityManagerOk()) {
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.DAY_OF_MONTH, 31);
			dtCalendar.set(Calendar.MONTH, 9);
			dtCalendar.set(Calendar.YEAR, 2012);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dataInicio = dtCalendar.getTime();
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.MONTH, 10);
			dtCalendar.set(Calendar.YEAR, 2012);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 23);
			dtCalendar.set(Calendar.MINUTE, 59);
			final Date dataFim = dtCalendar.getTime();
			final List<FatProcedAmbRealizado> result = this.daoUnderTests.buscarPorSituacaoData(dataInicio, dataFim, DominioSituacaoProcedimentoAmbulatorio.CONSULTAS_GRUPO);
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
				for (FatProcedAmbRealizado item : result) {
					logger.info(item.getSeq() + " ");
				}
			}
		}
	}
	
	@Test
	public void testBuscarPorSeqSituacao() {
		if (isEntityManagerOk()) {
			final Long seq = 1L;
			final DominioSituacaoProcedimentoAmbulatorio situacaoProcedimentoAmbulatorio = DominioSituacaoProcedimentoAmbulatorio.ABERTO;
			final FatProcedAmbRealizado result = this.daoUnderTests.buscarPorSeqSituacao(seq, situacaoProcedimentoAmbulatorio);
			if (result == null) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou 1 registro.");
			}
		}
	}

	@Test
	public void testBuscarEncerramentoPmr() {
		if (isEntityManagerOk()) {
			final Long pmrSeq = 0L;
			final String localCobranca = "B";
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2011);
			dtCalendar.set(Calendar.MONTH, 3);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 22);
			dtCalendar.set(Calendar.MINUTE, 0);
			dtCalendar.set(Calendar.SECOND, 0);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dataInicio = dtCalendar.getTime();
			final Byte cpeMes = 5;
			final Short cpeAno = 2011;
			final FatProcedAmbRealizado result = this.daoUnderTests.buscarEncerramentoPmr(pmrSeq,
					DominioLocalCobrancaProcedimentoAmbulatorialRealizado.valueOf(localCobranca), dataInicio, cpeMes, cpeAno);
			if (result == null) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou 1 registro.");
			}
		}
	}

	@Test
	public void testBuscarFatEspelhoProcedAmbVO() {
		if (isEntityManagerOk()) {
			final Calendar dtCalendar = Calendar.getInstance();
			dtCalendar.set(Calendar.YEAR, 2013);
			dtCalendar.set(Calendar.MONTH, 6);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 31);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 23);
			dtCalendar.set(Calendar.MINUTE, 59);
			dtCalendar.set(Calendar.SECOND, 59);
			dtCalendar.set(Calendar.MILLISECOND, 0);
			final Date dtHrInicio = dtCalendar.getTime();
			final Integer mes = 07;
			final Integer ano = 2013;

			dtCalendar.set(Calendar.MONTH, 10);
			dtCalendar.set(Calendar.DAY_OF_MONTH, 30);
			dtCalendar.set(Calendar.HOUR_OF_DAY, 23);
			dtCalendar.set(Calendar.MINUTE, 59);
			dtCalendar.set(Calendar.SECOND, 00);
			final Date dthrRealizado = dtCalendar.getTime();
			final DominioModuloCompetencia modulo = DominioModuloCompetencia.AMB;
			final DominioLocalCobrancaProcedimentoAmbulatorialRealizado localCobranca = DominioLocalCobrancaProcedimentoAmbulatorialRealizado.M;
			final DominioSituacaoProcedimentoAmbulatorio situacao = DominioSituacaoProcedimentoAmbulatorio.ABERTO;
			final DominioGrupoConvenio grupoConvenio = DominioGrupoConvenio.S;
			Date inicial = new Date();
			logger.info("inicial = " + DateFormatUtil.obterDataAtualHoraMinutoSegundo());
			Long nroRegistrosTotal = 
				this.daoUnderTests.buscarFatEspelhoProcedAmbCount(dthrRealizado, modulo, localCobranca, situacao, dtHrInicio,
					mes, ano, grupoConvenio);

			logger.info("Encontrou " + nroRegistrosTotal + " registros.");
			
		
			final List<FatEspelhoProcedAmbVO> result = this.daoUnderTests.buscarFatEspelhoProcedAmbVO(dthrRealizado, modulo, localCobranca,
					situacao, dtHrInicio, mes, ano, grupoConvenio);

			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}

			Assert.assertTrue(true);
	
			logger.info("inicial = " + inicial);
			logger.info("final = " + DateFormatUtil.obterDataAtualHoraMinutoSegundo());
		}
	}

	@Override
	protected void finalizeMocks() {
		int x = 0;
		logger.info("finalizando mocks");
		if (x > 0) {
			logger.info("x > 0");
		}
	}
}