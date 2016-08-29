package br.gov.mec.aghu.blococirurgico;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ResumoAgendaCirurgiaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;

public class MbcAgendasDAOTest extends AbstractDAOTest<MbcAgendasDAO> {
	
	@Override
	protected MbcAgendasDAO doDaoUnderTests() {
		return new MbcAgendasDAO() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6536607079488461531L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcAgendasDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected org.hibernate.Query createHibernateQuery(String query) {
				return MbcAgendasDAOTest.this.createHibernateQuery(query);
			}
		};
	}
	
	@Override
	protected void initMocks() {
	}

	@Test
	public void testBuscarOrdemOverbook() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			Date dataAgenda = DateUtil.obterData(2013, Calendar.JULY, 8);
			Short salaSeqp = Short.valueOf("13");
			Short salaUnfSeq = Short.valueOf("126");
			Short unfSeq = Short.valueOf("126");
			
			try {
				List<Byte> result = this.daoUnderTests.buscarOrdemOverbook(dataAgenda, salaSeqp, salaUnfSeq, unfSeq);
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
	
	@Test
	public void testBuscarEscalaAgenda() throws ApplicationBusinessException {
		
		if (isEntityManagerOk()) {
			Date dataAgenda = DateUtil.obterData(2013, Calendar.FEBRUARY, 06);			
			Short salaSeqp = Short.valueOf("5");
			Short unfSeq = Short.valueOf("126");
			Date dataInicio = DateUtil.obterData(2013, Calendar.FEBRUARY, 06, 13, 00);
			Date dataFim = DateUtil.obterData(2013, Calendar.FEBRUARY, 06, 16, 00);
			
			MbcAgendas agenda = new MbcAgendas();
			agenda.setDtAgenda(dataAgenda);
			MbcSalaCirurgicaId salaId = new MbcSalaCirurgicaId(unfSeq, salaSeqp);
			MbcSalaCirurgica sala = new MbcSalaCirurgica();
			sala.setId(salaId);
			
			AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
			unidade.setSeq(unfSeq);
			
			agenda.setSalaCirurgica(sala); 
			agenda.setUnidadeFuncional(unidade);
			
			try {
				List<MbcAgendas> result = this.daoUnderTests.buscarEscalaAgendas(agenda, dataInicio, dataFim, false);
				logger.info("###############################################");
				if (result == null) {
					logger.info("Retornou null.");
				} else {
					logger.info("Resultado = " + result);
					for (MbcAgendas agd : result) {
						logger.info("agdseq = " + agd.getSeq());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}
	
	
	@Test
	public void testPesquisarAgendaComControleEscalaCirurgicaDefinitiva() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				MbcAgendas result = this.daoUnderTests.pesquisarAgendaComControleEscalaCirurgicaDefinitiva(402285);
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
	
	@Test
	public void testPesquisarResumoAgendamentos() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			Date dtAgenda = DateUtil.obterData(2010, 10, 8);
			
			MbcProfAtuaUnidCirgs atuaUnidCirgs = new MbcProfAtuaUnidCirgs();
			MbcProfAtuaUnidCirgsId atuaUnidCirgsId = new MbcProfAtuaUnidCirgsId(Integer.valueOf(24834), Short.valueOf("4"), Short.valueOf("126"), DominioFuncaoProfissional.MPF);
			atuaUnidCirgs.setId(atuaUnidCirgsId);
			
			Short unfSeq = Short.valueOf("126");
			
			try {
				List<ResumoAgendaCirurgiaVO> result = this.daoUnderTests.pesquisarResumoAgendamentos(dtAgenda, atuaUnidCirgs, unfSeq, Short.valueOf("17"));
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
	
	@Test
	public void testBuscarAgendasPlanejadasParaEscala() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			Date dtAgenda = DateUtil.obterData(2010, 10, 8);
			
			Short unfSeq = Short.valueOf("126");
			
			try {
				List<MbcAgendas> result = this.daoUnderTests.buscarAgendasPlanejadasParaEscala(dtAgenda,
						Integer.valueOf(24834), Short.valueOf("4"), Short.valueOf("126"), DominioFuncaoProfissional.MPF,
						Short.valueOf("10"), unfSeq);
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
	
	@Test
	public void testBuscarRegimeSusPorId() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				List<DominioRegimeProcedimentoCirurgicoSus> result = this.daoUnderTests.buscarRegimeSusPorId(480939);
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
	
	@Test
	public void testBuscarRegimeSusPacientePorId() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			try {
				List<MbcAgendas> result = this.daoUnderTests.buscarRegimeSusPacientePorId(480939);
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

	
	@Test
	public void buscarDatasAgendaEscala() throws ApplicationBusinessException {
		if (isEntityManagerOk()) {
			
			PortalPesquisaCirurgiasParametrosVO cirurgiasParametrosVO = new PortalPesquisaCirurgiasParametrosVO();
			cirurgiasParametrosVO.setPacCodigo(2199706);
			
			try {
				List<MbcAgendas> result = this.daoUnderTests.buscarDatasAgendaEscala(cirurgiasParametrosVO);
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