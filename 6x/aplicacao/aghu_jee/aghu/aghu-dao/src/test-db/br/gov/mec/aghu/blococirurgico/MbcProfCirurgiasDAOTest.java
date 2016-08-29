package br.gov.mec.aghu.blococirurgico;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapServidores;

public class MbcProfCirurgiasDAOTest  extends AbstractDAOTest<MbcProfCirurgiasDAO> {

	private static final Log log = LogFactory.getLog(MbcProfCirurgiasDAOTest.class);
	
	@Override
	protected MbcProfCirurgiasDAO doDaoUnderTests() {
		return new MbcProfCirurgiasDAO() {
			private static final long serialVersionUID = -3425546458946135183L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return MbcProfCirurgiasDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return MbcProfCirurgiasDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcProfCirurgiasDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Test
	public void buscaRapServidorPorCrgSeqEIndResponsavel() throws ApplicationBusinessException {

		MbcProfCirurgias result = null;

		if (isEntityManagerOk()) {
			// assert
			try {
				result = this.daoUnderTests.buscaRapServidorPorCrgSeqEIndResponsavel(42020, DominioSimNao.S); 
				logger.info("###############################################");
				if (result == null) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou 1 registros.");
					logger.info("PucIndFuncaoProf = " + result.getId().getPucIndFuncaoProf());
					logger.info("PucSerMatricula = " + result.getId().getPucSerMatricula());
					logger.info("PucSerVinCodigo = " + result.getId().getPucSerVinCodigo());
					logger.info("PucUnfSeq = " + result.getId().getPucUnfSeq());
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Test
	public void buscaRapServidor(){
		final Integer crgSeq = 234; 
		final DominioSimNao resp = DominioSimNao.S;
		
		if (isEntityManagerOk()) {
			RapServidores result = this.daoUnderTests.buscaRapServidor(crgSeq, resp);
			
			if (result == null) {
				log.info("Não retornou registros.");
			} else {
				log.info("Retornou [" + result + "].");
			}
			Assert.assertTrue(true);
		}
	}

	@Test
	public void buscaRapServidor1(){
		final Integer crgSeq = 234; 
		DominioFuncaoProfissional[] funcoes = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.ANP,
																				DominioFuncaoProfissional.ANR, 
																				DominioFuncaoProfissional.ANC 
																			  };
		
		if (isEntityManagerOk()) {
			RapServidores result = this.daoUnderTests.buscaRapServidor(crgSeq, funcoes);
			
			if (result == null) {
				log.info("Não retornou registros.");
			} else {
				log.info("Retornou [" + result + "].");
			}
			Assert.assertTrue(true);
		}
	}
	
	@Ignore
	public void buscarAgendasEscalasUnion1(){
		if (isEntityManagerOk()) {
			PortalPesquisaCirurgiasParametrosVO cirurgiasParametrosVO = new PortalPesquisaCirurgiasParametrosVO();
//			cirurgiasParametrosVO.setEspSeq(Short.valueOf("33"));
//			cirurgiasParametrosVO.setPucSerMatricula(1294);
//			cirurgiasParametrosVO.setPucSerVinCodigo(Short.valueOf("4"));
//			cirurgiasParametrosVO.setPucUnfSeq(Short.valueOf("126"));
//			cirurgiasParametrosVO.setPucIndFuncaoProf(DominioFuncaoProfissional.MPF);
//			cirurgiasParametrosVO.setSala(Short.valueOf("13"));
			cirurgiasParametrosVO.setDataInicio(DateUtil.obterData(2013, 8, 18));
			cirurgiasParametrosVO.setDataInicio(DateUtil.obterData(2013, 8, 19));
			List<PortalPesquisaCirurgiasAgendaEscalaVO> result = this.daoUnderTests.pesquisarAgendasEscalaCirurgiasUnion1(cirurgiasParametrosVO, DateUtil.obterData(2013, 8, 18), false);
			if (result == null) {
				log.info("Não retornou registros.");
			} else {
				log.info("Retornou [" + result + "].");
				
				for(PortalPesquisaCirurgiasAgendaEscalaVO vo : result){
					log.info("agdSeq: " + vo.getAgdSeq() + " Paciente : " + vo.getNomePaciente());
				}
			}
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void buscarAgendasEscalasUnion2(){
		if (isEntityManagerOk()) {
			PortalPesquisaCirurgiasParametrosVO cirurgiasParametrosVO = new PortalPesquisaCirurgiasParametrosVO();
//			cirurgiasParametrosVO.setSala(Short.valueOf("13"));
			cirurgiasParametrosVO.setDataInicio(DateUtil.obterData(2013, 8, 17));
			cirurgiasParametrosVO.setDataFim(DateUtil.obterData(2013, 8, 17));
			List<PortalPesquisaCirurgiasAgendaEscalaVO> result = this.daoUnderTests.pesquisarAgendasEscalaCirurgiasUnion2(cirurgiasParametrosVO, DateUtil.obterData(2013, 8, 17), 
					new Short[]{29, 50}, false);
			if (result == null) {
				log.info("Não retornou registros.");
			} else {
				log.info("Retornou [" + result + "].");
			}
			Assert.assertTrue(true);
		}
	}
	
	@Override
	protected void finalizeMocks() {
		
	}

}
