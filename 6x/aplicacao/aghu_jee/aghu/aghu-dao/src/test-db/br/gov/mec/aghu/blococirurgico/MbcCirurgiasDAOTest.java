package br.gov.mec.aghu.blococirurgico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiasExposicaoRadiacaoIonizanteVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcRelatCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasPendenteRetornoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedAgendPorUnidCirurgicaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.MbcCirurgias;

public class MbcCirurgiasDAOTest extends AbstractDAOTest<MbcCirurgiasDAO> {
	
	@Override
	protected MbcCirurgiasDAO doDaoUnderTests() {
		return new MbcCirurgiasDAO() {
			private static final long serialVersionUID = 1058151041682953177L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcCirurgiasDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return true;
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}


	@Test
	public void testBuscarDataCirurgias() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Integer codPaciente = 2083185;
			final DominioSituacaoCirurgia situacaoCirurgia = DominioSituacaoCirurgia.RZDA;
			final Boolean indDigtNotaSala = true;
			final Short cnvCodigo = 1;
			final DominioIndRespProc dominioIndRespProc = DominioIndRespProc.NOTA ;
			final DominioSituacao situacao = DominioSituacao.A;
			final Integer phiSeq = 13742; // 'P_PHI_IMPLANTE_COCLEAR'
			final Calendar calendar = Calendar.getInstance();
			final Calendar dtRealizado = Calendar.getInstance();
			calendar.set(Calendar.MONTH, -180); // - c_dias
			calendar.set(Calendar.DAY_OF_MONTH, 5);//calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			final Date dtRealizadoFimMes = calendar.getTime();

			try {
				List<Date> result = this.daoUnderTests.buscarDataCirurgias(codPaciente, dtRealizado.getTime(), dtRealizadoFimMes, situacaoCirurgia, indDigtNotaSala,
						cnvCodigo, dominioIndRespProc, situacao, phiSeq);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (Date result1 : result) {
						logger.info("Data = " + result1);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	
	@Test
	public void testCriteriaUnion1CirurgiasExposicaoRadiacaoIonizante()
			throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Date dataInicial = DateUtil.obterData(2013, 0, 1); 
			final Date dataFinal = DateUtil.obterData(2013, 0, 5);

			final List<Short> unidadesFuncionais = new ArrayList<Short>();
			unidadesFuncionais.add((short) 126);
			unidadesFuncionais.add((short) 131);
			unidadesFuncionais.add((short) 130);
			
			final List<Short> equipamentos = new ArrayList<Short>();
			equipamentos.add((short) 109);
			equipamentos.add((short) 7);
			equipamentos.add((short) 111);
			equipamentos.add((short) 105);
			
			try {
				List<CirurgiasExposicaoRadiacaoIonizanteVO> result = doDaoUnderTests().criteriaUnion1CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos);
				logger.info(result.size());
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (CirurgiasExposicaoRadiacaoIonizanteVO resultado : result) {
						logger.info("CirurgiasExposicaoRadiacaoIonizanteVO = " + resultado);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void testCriteriaUnion2CirurgiasExposicaoRadiacaoIonizante()
			throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Date dataInicial = DateUtil.obterData(2013, 0, 1); 
			final Date dataFinal = DateUtil.obterData(2013, 0, 5);
			
			final List<Short> unidadesFuncionais = new ArrayList<Short>();
			unidadesFuncionais.add((short) 126);
			unidadesFuncionais.add((short) 131);
			unidadesFuncionais.add((short) 130);
			
			final List<Short> equipamentos = new ArrayList<Short>();
			equipamentos.add((short) 109);
			equipamentos.add((short) 7);
			equipamentos.add((short) 111);
			equipamentos.add((short) 105);
			
			try {
				List<CirurgiasExposicaoRadiacaoIonizanteVO> result = doDaoUnderTests().criteriaUnion2CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos);
				logger.info(result.size());
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (CirurgiasExposicaoRadiacaoIonizanteVO resultado : result) {
						logger.info("CirurgiasExposicaoRadiacaoIonizanteVO = " + resultado);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void testCriteriaUnion3CirurgiasExposicaoRadiacaoIonizante()
			throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Date dataInicial = DateUtil.obterData(2013, 0, 1); 
			final Date dataFinal = DateUtil.obterData(2013, 0, 5);
	
			final List<Short> unidadesFuncionais = new ArrayList<Short>();
			unidadesFuncionais.add((short) 126);
			unidadesFuncionais.add((short) 131);
			unidadesFuncionais.add((short) 130);

			final List<Short> equipamentos = new ArrayList<Short>();
			equipamentos.add((short) 109);
			equipamentos.add((short) 7);
			equipamentos.add((short) 111);
			equipamentos.add((short) 105);
			
			try {
				List<CirurgiasExposicaoRadiacaoIonizanteVO> result = doDaoUnderTests().criteriaUnion3CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos);
				logger.info(result.size());
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (CirurgiasExposicaoRadiacaoIonizanteVO resultado : result) {
						logger.info("CirurgiasExposicaoRadiacaoIonizanteVO = " + resultado);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	@Test
	public void testoObterCirurgiasExposicaoRadiacaoIonizante()throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			final Date dataInicial = DateUtil.obterData(2013, 0, 1); 
			final Date dataFinal = DateUtil.obterData(2013, 0, 5);
	
			final List<Short> unidadesFuncionais = new ArrayList<Short>();
			unidadesFuncionais.add((short) 126);
			unidadesFuncionais.add((short) 131);
			unidadesFuncionais.add((short) 130);
	
			final List<Short> equipamentos = new ArrayList<Short>();
			equipamentos.add((short) 109);
			equipamentos.add((short) 7);
			equipamentos.add((short) 111);
			equipamentos.add((short) 105);
			
				try {
					List<CirurgiasExposicaoRadiacaoIonizanteVO> listVO = new ArrayList<CirurgiasExposicaoRadiacaoIonizanteVO>();
					listVO.addAll(doDaoUnderTests().criteriaUnion1CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos));
					listVO.addAll(doDaoUnderTests().criteriaUnion2CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos));
					listVO.addAll(doDaoUnderTests().criteriaUnion3CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos));
					
					Set<CirurgiasExposicaoRadiacaoIonizanteVO> result = new HashSet<CirurgiasExposicaoRadiacaoIonizanteVO>();
					result.addAll(listVO);
					
					logger.info(result.size());
					if (result == null || result.isEmpty()) {
						logger.info("Retornou null.");
					} else {
						logger.info("Retornou " + result.size() + " registros.");
						for (CirurgiasExposicaoRadiacaoIonizanteVO resultado : result) {
							logger.info("CirurgiasExposicaoRadiacaoIonizanteVO = " + resultado);
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					Assert.fail("Not expecting exception: " + e);
				}
		}
	}

	 @Test
	public void testpesquisarCirurgiasEletivasParaAgenda() {
		if (isEntityManagerOk()) {
			final Integer agdSeq = 463907;
			
			try {
				List<MbcCirurgias> result = this.daoUnderTests.pesquisarCirurgiasEletivasParaAgenda(agdSeq);
				logger.info("###############################################");
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
					for (MbcCirurgias result1 : result) {
						logger.info("Seq agenda = " + result1.getAgenda().getSeq());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	 
	@Test
	public void testPesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim() {
		if (isEntityManagerOk()) {
			Calendar calDtInicio = Calendar.getInstance();
			calDtInicio.set(Calendar.YEAR, 2013);
			calDtInicio.set(Calendar.MONTH, 6); // Julho
			calDtInicio.set(Calendar.DAY_OF_MONTH, 1);
			DateUtil.zeraHorario(calDtInicio);
			
			Calendar calDtFim = (Calendar) calDtInicio.clone();
			calDtFim.set(Calendar.DAY_OF_MONTH, 9);

			final Short unfSeq = Short.valueOf("126");
			final Integer pciSeq = null;
			final Date dtInicio = calDtInicio.getTime();
			final Date dtFim = calDtFim.getTime();
						
			try {
				final List<RelatorioProcedAgendPorUnidCirurgicaVO> result = this.daoUnderTests
						.pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
								unfSeq, pciSeq, dtInicio, dtFim);
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null ou lista vazia.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(ArrayUtils.toString(e.getStackTrace()));
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}	 
	
	@Test
	public void testPesquisarCirurgiaNaoCanceladaPorUnfSeqDtInicioDtFim() {
		if (isEntityManagerOk()) {
			Calendar calDtInicio = Calendar.getInstance();
			calDtInicio.set(Calendar.YEAR, 2013);
			calDtInicio.set(Calendar.MONTH, 0); // Janeiro
			calDtInicio.set(Calendar.DAY_OF_MONTH, 1);
			DateUtil.zeraHorario(calDtInicio);
			
			Calendar calDtFim = (Calendar) calDtInicio.clone();
			calDtFim.set(Calendar.MONTH, 6); // Julho

			final Short unfSeq = Short.valueOf("126");
			final Date dtInicio = calDtInicio.getTime();
			final Date dtFim = calDtFim.getTime();
						
			try {
				final List<RelatorioCirurgiasPendenteRetornoVO> result = this.daoUnderTests
						.pesquisarCirurgiaNaoCanceladaPorUnfSeqDtInicioDtFim(
								unfSeq, dtInicio, dtFim);
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null ou lista vazia.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(ArrayUtils.toString(e.getStackTrace()));
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	 
	@Test
	public void testPesquisarCirurgiaNaoCanceladaProcedPrincipalPorUnfSeqDtInicioDtFimEPciSeq() {
		if (isEntityManagerOk()) {
			Calendar calDtInicio = Calendar.getInstance();
			calDtInicio.set(Calendar.YEAR, 2013);
			calDtInicio.set(Calendar.MONTH, 0); // Janeiro
			calDtInicio.set(Calendar.DAY_OF_MONTH, 1);
			DateUtil.zeraHorario(calDtInicio);
			
			Calendar calDtFim = (Calendar) calDtInicio.clone();
			calDtFim.set(Calendar.MONTH, 6); // Julho

			final Short unfSeq = Short.valueOf("126");
			final Date dtInicio = calDtInicio.getTime();
			final Date dtFim = calDtFim.getTime();
			final Integer pciSeq = null;
						
			try {
				final List<RelatorioCirurgiasPendenteRetornoVO> result = this.daoUnderTests
						.pesquisarCirurgiaNaoCanceladaProcedPrincipalPorUnfSeqDtInicioDtFimEPciSeq(
								unfSeq, dtInicio, dtFim, pciSeq);
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null ou lista vazia.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(ArrayUtils.toString(e.getStackTrace()));
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void testPesquisarCirurgiaNaoCanceladaDigitaNotaSalaPorUnfSeqDtInicioDtFim() {
		if (isEntityManagerOk()) {
			Calendar calDtInicio = Calendar.getInstance();
			calDtInicio.set(Calendar.YEAR, 2013);
			calDtInicio.set(Calendar.MONTH, 0); // Janeiro
			calDtInicio.set(Calendar.DAY_OF_MONTH, 1);
			DateUtil.zeraHorario(calDtInicio);
			
			Calendar calDtFim = (Calendar) calDtInicio.clone();
			calDtFim.set(Calendar.MONTH, 6); // Julho

			final Short unfSeq = Short.valueOf("126");
			final Date dtInicio = calDtInicio.getTime();
			final Date dtFim = calDtFim.getTime();
			final Integer gmtCodigoOrteseProtese = 13;
						
			try {
				final List<RelatorioCirurgiasPendenteRetornoVO> result = this.daoUnderTests
						.pesquisarCirurgiaNaoCanceladaDigitaNotaSalaPorUnfSeqDtInicioDtFim(
								unfSeq, dtInicio, dtFim, gmtCodigoOrteseProtese);
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null ou lista vazia.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
			} catch (Exception e) {
				logger.error(ArrayUtils.toString(e.getStackTrace()));
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	@Test
	public void obterCirurRealizPorEspecEProf() throws ApplicationBusinessException,
			ApplicationBusinessException {
		if (isEntityManagerOk()) {

			final Date dataInicial = DateUtil.obterData(2013, 8, 1);
			final Date dataFinal = DateUtil.obterData(2013, 9, 1);
			final Short unidadeFuncional = (short) 126;
			final Short especialidade = null;

			try {
				List<MbcRelatCirurRealizPorEspecEProfVO> result = doDaoUnderTests()
						.obterCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);
				
				logger.info(result.size());
				
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
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