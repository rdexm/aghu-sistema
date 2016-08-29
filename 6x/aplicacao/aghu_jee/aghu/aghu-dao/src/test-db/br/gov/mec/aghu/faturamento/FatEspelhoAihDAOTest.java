package br.gov.mec.aghu.faturamento;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosMedVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosProtVO;
import br.gov.mec.aghu.faturamento.vo.CursorCBuscaRegCivilVO;
import br.gov.mec.aghu.faturamento.vo.CursorCEAIVO;

public class FatEspelhoAihDAOTest extends AbstractDAOTest<FatEspelhoAihDAO> {

	@Override
	protected FatEspelhoAihDAO doDaoUnderTests() {
		return new FatEspelhoAihDAO() {

			private static final long serialVersionUID = 5625915310657294840L;

			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return FatEspelhoAihDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return FatEspelhoAihDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {}

	@Override
	protected void finalizeMocks() {}
	
	@Test
	public void obterResultadoCursorBuscaConta()  {

		if (isEntityManagerOk()) {
	
			final Date dtHrInicio = new Date();
			final Integer mes = 11;
			final Integer ano = 2012;
			final Boolean indAutorizadoSSM = Boolean.TRUE;
			final DominioSituacaoConta indSituacao = DominioSituacaoConta.A;
			final Date dtEncInicial= new Date();
			final Date dtEncFinal = new Date();
			final DominioModuloCompetencia modulo = DominioModuloCompetencia.INT;
			
			final List<CursorBuscaContaVO> result = this.daoUnderTests.obterResultadoCursorBuscaConta( dtHrInicio, mes, ano, indAutorizadoSSM, 
																					     			   indSituacao, dtEncInicial, dtEncFinal,
																					     			   modulo);
		
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
			
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void obterResultadoCursorCEAI()  {

		if (isEntityManagerOk()) {
			final Integer cthSeq = 407020;
			final Long cpfDirCli = Long.valueOf("00690493940");
			final String municipioInstituicao = "431490";
			final String orgEmisAih = "M431490001";
			final Integer cnesHCPA = 2237601;
			
			final List<CursorCEAIVO> result = this.daoUnderTests.obterResultadoCursorCEAI(cthSeq , cpfDirCli, municipioInstituicao, orgEmisAih, cnesHCPA);
		
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
			
			Assert.assertTrue(true);
		}
	}


	@Test
	public void obterResultadoCursorCAtosMed() {

		if (isEntityManagerOk()) {
			try {
	
				
				final Integer cthSeq = 407020;
				final String codigoDaUPS = "2237601";
				final Short fogSgrGrpSeq = 7;
				
				final List<CursorCAtosMedVO> result = this.daoUnderTests.obterResultadoCursorCAtosMed(cthSeq, codigoDaUPS, fogSgrGrpSeq);
			
				if (result == null || result.isEmpty()) {
					logger.info("Não retornou registros.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}
				
				Assert.assertTrue(true);
			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void obterResultadoCursorCAtosProt() {

		if (isEntityManagerOk()) {
			final Integer cthSeq = 407020;
			final Short fogSgrGrpSeq = 7;
			
			final List<CursorCAtosProtVO> result = this.daoUnderTests.obterResultadoCursorCAtosProt(cthSeq, fogSgrGrpSeq);
		
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
			
			Assert.assertTrue(true);
		}
	}
	

	@Test
	public void obterResultadoCursorCBuscaRegCivil() {

		if (isEntityManagerOk()) {
			final Integer cthSeq = 407020;
			
			final List<CursorCBuscaRegCivilVO> result = this.daoUnderTests.obterResultadoCursorCBuscaRegCivil(cthSeq);
		
			if (result == null || result.isEmpty()) {
				logger.info("Não retornou registros.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
			}
			
			Assert.assertTrue(true);
		}
	}
}