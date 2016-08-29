package br.gov.mec.aghu.faturamento;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;

public class FatAtoMedicoAihDAOTest extends AbstractDAOTest<FatAtoMedicoAihDAO> {
	
	@Override
	protected FatAtoMedicoAihDAO doDaoUnderTests() {
		return new FatAtoMedicoAihDAO() {
			private static final long serialVersionUID = -2220285789400697606L;

			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return FatAtoMedicoAihDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return FatAtoMedicoAihDAOTest.this.isOracle();
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatAtoMedicoAihDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Test
	public void obterNiveisAtosMedico(){
		if (isEntityManagerOk()) {
			final Short iphPhoSeq = Short.valueOf("12");
			
			final Integer cthSeq=1;
			final Integer iphSeq=1;
			this.daoUnderTests.obterNiveisAtosMedico(cthSeq, iphPhoSeq, iphSeq);
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * TESTA CURSOR DE FATC_BUSCA_PROCED_DO_ITEM_NEW.C_REGISTRO
	 */
	@Test
	public void obterQtdMaximaExecucao(){
		if (isEntityManagerOk()) {

			final Short phoSeq = Short.valueOf("12");
			final Integer iphSeq = 2285;
			final Integer eaiCthSeq = 432383;
			final Byte taoSeq = Byte.valueOf("1");
			
			// ORACLE
			final String competenciaUTI = "201109";
			final Date competencia = DateUtil.obterData(2010, 00, 01);
			
			// POSTGRES
//			final String competenciaUTI = "201203";
//			final Date competencia = DateUtil.obterData(2011, 00, 01);
			
			final Long quantidadeMaxima = this.daoUnderTests.obterQtdMaximaExecucao(phoSeq, iphSeq, eaiCthSeq, taoSeq , competenciaUTI, competencia);

			Assert.assertNotNull(quantidadeMaxima);
			logger.info( " Quantidade_Maxima "+quantidadeMaxima);
		}
	}
	
	/**
	 * Testa cursor: FATK_CTHN_RN_UN.C_ATO_PROT
	 */
	@Test
	public void listarPorIphCodSusCthGrpSit() {
		if (isEntityManagerOk()) {
			final Short phoSeq = 12; // buscarVlrShortAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO)
			final Integer seq = 2500;
			final DominioSituacao situacao = DominioSituacao.A;
			final Integer cthSeq = 430462;
			final Short sgrGrpSeq = Short.valueOf("7"); // buscarVlrShortAghParametro(AghuParametrosEnum.P_GRUPO_OPM)
			
			List<Integer> seqs = this.daoUnderTests.listarPorIphCodSusCthGrpSit(phoSeq, seq, situacao, cthSeq, sgrGrpSeq);
			
			if(seqs != null && !seqs.isEmpty()){
				for (Integer vSeq : seqs) {
					logger.info( " seq "+vSeq);
				}
				Assert.assertTrue(true);
			} else {
				Assert.assertFalse(true);
			}
		}
	}
	
	/**
	 * TESTA CURSOR DE FATC_BUSCA_PROCED_DO_ITEM_NEW.C_REGISTRO
	 */
	@Test
	public void obterQtdMaximaExecucao3(){
		if (isEntityManagerOk()) {
			final Short iphPhoSeq = Short.valueOf("12"); 
			final Integer iphSeq = 5912;
			final Integer eaiCthSeq = 418025;
			final Date competencia = DateUtil.obterData(2010, 00, 01);
			final Byte codigoSus = 1;
			final Object[] quantidadeMaxima = this.daoUnderTests.obterQtdMaximaExecucao( iphPhoSeq, iphSeq, eaiCthSeq, competencia, codigoSus, 1, 
					DominioIndCompatExclus.ICP, DominioIndCompatExclus.PCI);
			
			Assert.assertNotNull(quantidadeMaxima);
			
			logger.info( " Quantidade_Maxima " + quantidadeMaxima);
		}
		
	}

	@Override
	protected void finalizeMocks() {}
}
