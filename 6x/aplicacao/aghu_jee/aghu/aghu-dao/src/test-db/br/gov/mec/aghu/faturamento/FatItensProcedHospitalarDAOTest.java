package br.gov.mec.aghu.faturamento;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.vo.CursorAtoMedicoAihVO;
import br.gov.mec.aghu.faturamento.vo.FatGrupoSubGrupoVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedServVO;

public class FatItensProcedHospitalarDAOTest extends AbstractDAOTest<FatItensProcedHospitalarDAO> {
	
	private static final Log log = LogFactory.getLog(FatItensProcedHospitalarDAOTest.class);

	@Override
	protected FatItensProcedHospitalarDAO doDaoUnderTests() {
		return new FatItensProcedHospitalarDAO() {
			private static final long serialVersionUID = 3599220044770794630L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatItensProcedHospitalarDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return FatItensProcedHospitalarDAOTest.this.isOracle();
			}
			
			@Override
			protected Query createHibernateQuery(String query) {
				return FatItensProcedHospitalarDAOTest.this.createHibernateQuery(query);
			}
			
		};
	}


	@Override
	protected void initMocks() {

	}

	@Test
	public void testObterListaPorIphCnvIdadeDataFaixaEtariaAtiva() {
		if (isEntityManagerOk()) {
			final Long codTabela = 12L;
			FatGrupoSubGrupoVO result = this.daoUnderTests.obterFatGrupoSubGrupoVOPorCodTabela(codTabela);
			logger.info("###############################################");
			if (result == null) {
				logger.info("Retornou vazio.");
			} else {
				logger.info("Retornou grupo=" + result.getGrupo() + ", subgrupo=" + result.getSubGrupo() + " resultados.");
			}
		}
	}
	
	@Test
	public void listarEspelhoItemContaHospPorCthSeq() {

		List<CursorAtoMedicoAihVO> result = null;

		if (isEntityManagerOk()) {
			//assert
			//sem dados
			result = this.daoUnderTests.listarAtosMedicosAih(16902);
			Assert.assertTrue(true);
			
			log.info("nro registros: " + result.size());
			for (CursorAtoMedicoAihVO vo : result) {
				log.info("dtAltaAdm: " + vo.getDtAltaAdministrativa());
				log.info("diaAltaAdm: " + vo.getDiaDaAlta());
				log.info("anoMesAltaAdm: " + vo.getAnoMesAlta());
				
				log.info("dtIntAdm: " + vo.getDtIntAdministrativa());
				log.info("anoAnoMesInt: " + vo.getAnoMesInt());
				log.info("diaMesInt: " + vo.getDiaMesInt());
			}
		}
	}
	
	@Test
	public void buscaQtdeLancTest() {
		if (isEntityManagerOk()) {
			final Short phoSeq = 12;
			final Integer cthSeq = 289813;
			final DominioSituacaoItenConta[] arrayIndSituacao = 
				{ DominioSituacaoItenConta.C, DominioSituacaoItenConta.D };
			final Long codTabela = 304100013L;
			final Integer phiSeq = 28220;
			final Date competencia = DateUtil.obterData(2012, 0, 1); // 01/01/2012
			
			Object[] result = this.daoUnderTests.buscaQtdeLanc(phoSeq, cthSeq,
					arrayIndSituacao, codTabela, phiSeq, competencia);
			
			if (result != null) {
				Assert.assertTrue(true);
				log.info("Result: " + result);
			} else {
				Assert.assertFalse(true);
			}
		}
	}	
	
	@Test
	public void pesquisarFatAssociacaoProcedimentos(){
		if (isEntityManagerOk()) {
			final Integer cidSeq = 2041;

			List<Long> result = this.daoUnderTests.pesquisarFatAssociacaoProcedimentos(cidSeq);

			if(result != null && !result.isEmpty()){
				Assert.assertTrue(true);
				for (Long i : result) {
					log.info(i);
				}
			} else {
				Assert.assertFalse(true);
			}
		}
	}
	
	@Test
	public void obterListaDeCodigoTabela() {
		if (isEntityManagerOk()) {
			
			final Integer phiSeq = 14746;
			final DominioSituacao phiIndSituacao = DominioSituacao.A;
			final DominioSituacao iphIndSituacao = DominioSituacao.A;
			final Short cpgCphCspCnvCodigo 	= 1;
			final Byte cpgCphCspSeq			= 1;
			final Short iphPhoSeq			= 1;
			
			List<Long> result = daoUnderTests.obterListaDeCodigoTabela(phiSeq, phiIndSituacao, iphIndSituacao, cpgCphCspCnvCodigo, cpgCphCspSeq, iphPhoSeq);

			if (result != null && !result.isEmpty()) {
				Assert.assertTrue(true);
				for (Long i : result) {
					log.info(i);
				}
			} else {
				Assert.assertFalse(true);
			}
		}
	}

	@Test
	public void obterFatProcedServVO() throws ApplicationBusinessException{
		if (isEntityManagerOk()) {
			
			List<FatProcedServVO> vos = this.daoUnderTests.obterFatProcedServVO();
			
			if(vos != null && !vos.isEmpty()){
				Assert.assertTrue(true);
			} else {
				Assert.assertFalse(true);
			}
		}
	}

	@Override
	protected void finalizeMocks() {}
}
