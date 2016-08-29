package br.gov.mec.aghu.faturamento;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;
import br.gov.mec.aghu.model.FatContasHospitalares;

public class FatCaractFinanciamentoDAOTest extends AbstractDAOTest<FatCaractFinanciamentoDAO> {

	@Override
	protected FatCaractFinanciamentoDAO doDaoUnderTests() {
		return new FatCaractFinanciamentoDAO() {
			private static final long serialVersionUID = -7510983806158395796L;

			@Override
			protected Query createHibernateQuery(String query) {
				return FatCaractFinanciamentoDAOTest.this.createHibernateQuery(query);
			}
		};
	}

	public static final Integer[] ARRAY_CTH_SEQ_CASOS_TESTES = new Integer[] {
			// Integer.valueOf(403020), // ssm s
			Integer.valueOf(384404), // complex
			Integer.valueOf(392502), // ssm
			Integer.valueOf(397061), // ssm s + status
			Integer.valueOf(397099), // ssm r
			Integer.valueOf(397116), // ssm r
			Integer.valueOf(397338), // complex + ssm s
			Integer.valueOf(399535), // ssm s + r
			Integer.valueOf(403234), // ssm s
	};
	
	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}

	@Test
	public void testListarSsmComplexParaListaCthSeq() {

		List<ParCthSeqSsmVO> resS = null;
		List<ParCthSeqSsmVO> resR = null;
		String ssmSol = null;
		String ssmReal = null;
		List<Integer> listaCthSeq = null;
		Short cnvCodigo = null;
		Byte cspSeq = null;
		Integer cthSeq = null;
		Integer oldCthSeq = null;
		FatContasHospitalares cth = null;

		if (isEntityManagerOk()) {
			//setup
			cnvCodigo = Short.valueOf((short) 1);
			cspSeq = Byte.valueOf((byte) 1);
			listaCthSeq = Arrays.asList(ARRAY_CTH_SEQ_CASOS_TESTES);
			//assert
			try {
				resS = getDaoUnderTests().listarSsmComplexParaListaCthSeq(listaCthSeq, cnvCodigo, cspSeq, DominioSituacaoSSM.S);
				resR = getDaoUnderTests().listarSsmComplexParaListaCthSeq(listaCthSeq, cnvCodigo, cspSeq, DominioSituacaoSSM.R);
				Assert.assertNotNull(resS);
				Assert.assertNotNull(resR);
				logger.info(resS.size() + ":" + resS.toString());
				logger.info(resR.size() + ":" + resR.toString());
				oldCthSeq = null;
				for (ParCthSeqSsmVO pair : resS) {
					cthSeq = pair.getCthSeq();
					if (!cthSeq.equals(oldCthSeq)) {
						cth = this.entityManager.find(FatContasHospitalares.class, cthSeq);
						Assert.assertTrue("cth " + cthSeq, listaCthSeq.contains(cthSeq));
						ssmSol = getDaoUnderTests().buscaProcedimentoSolicitadoComplexidade(cthSeq, cnvCodigo, cspSeq, cth.getProcedimentoHospitalarInterno().getSeq());
						Assert.assertEquals("cth " + cthSeq, ssmSol, pair.getSsmStr());
					}
					oldCthSeq = cthSeq;
				}
				oldCthSeq = null;
				for (ParCthSeqSsmVO pair : resR) {
					cthSeq = pair.getCthSeq();
					if (!cthSeq.equals(oldCthSeq)) {
						cth = this.entityManager.find(FatContasHospitalares.class, cthSeq);
						Assert.assertTrue("cth " + cthSeq, listaCthSeq.contains(cthSeq));
						ssmReal = getDaoUnderTests().buscaProcedimentoRealizadoComplexidade(cthSeq, cnvCodigo, cspSeq, cth.getProcedimentoHospitalarInternoRealizado()
								.getSeq());
						Assert.assertEquals("cth " + cthSeq, ssmReal, pair.getSsmStr());
					}
					oldCthSeq = cthSeq;
				}
			} catch (Exception e) {
				logger.info(Arrays.toString(e.getStackTrace()));
				fail("Not expecting exception: " + e.getLocalizedMessage());
			}
		}
	}

	@Test
	public void testListarSsmAbertaFechadaComplexidadeParaListaCthSeq() {

		List<ParCthSeqSsmVO> resS = null;
		List<ParCthSeqSsmVO> resR = null;
		String ssmSol = null;
		String ssmReal = null;
		List<Integer> listaCthSeq = null;
		Short cnvCodigo = null;
		Byte cspSeq = null;
		Integer cthSeq = null;
		FatContasHospitalares cth = null;
		Short tipoGrupoContaSUS = null;
		Integer oldCthSeq = null;

		if (isEntityManagerOk()) {
			//setup
			tipoGrupoContaSUS = Short.valueOf((short) 6);
			cnvCodigo = Short.valueOf((short) 1);
			cspSeq = Byte.valueOf((byte) 1);
			listaCthSeq = Arrays.asList(ARRAY_CTH_SEQ_CASOS_TESTES);
			//assert
			try {
				resS = getDaoUnderTests().listarSsmAbertaFechadaComplexidadeParaListaCthSeq(listaCthSeq, cnvCodigo, cspSeq, tipoGrupoContaSUS, DominioSituacaoSSM.S);
				resR = getDaoUnderTests().listarSsmAbertaFechadaComplexidadeParaListaCthSeq(listaCthSeq, cnvCodigo, cspSeq, tipoGrupoContaSUS, DominioSituacaoSSM.R);
				Assert.assertNotNull(resS);
				Assert.assertNotNull(resR);
				logger.info(resS.size() + ":" + resS.toString());
				logger.info(resR.size() + ":" + resR.toString());
				oldCthSeq = null;
				for (ParCthSeqSsmVO pair : resS) {
					cthSeq = pair.getCthSeq();
					if (!cthSeq.equals(oldCthSeq)) {
						cth = this.entityManager.find(FatContasHospitalares.class, cthSeq);
						Assert.assertTrue("cth " + cthSeq, listaCthSeq.contains(cthSeq));
						ssmSol = getDaoUnderTests().buscaProcedimentoSolicitadoAbertaFechadaComplexidade(cthSeq, cnvCodigo, cspSeq, cth
								.getProcedimentoHospitalarInterno().getSeq(), tipoGrupoContaSUS);
						Assert.assertEquals("cth " + cthSeq, ssmSol, pair.getSsmStr());
					}
					oldCthSeq = cthSeq;
				}
				oldCthSeq = null;
				for (ParCthSeqSsmVO pair : resR) {
					cthSeq = pair.getCthSeq();
					if (!cthSeq.equals(oldCthSeq)) {
						cth = this.entityManager.find(FatContasHospitalares.class, cthSeq);
						Assert.assertTrue("cth " + cthSeq, listaCthSeq.contains(cthSeq));
						ssmReal = getDaoUnderTests().buscaProcedimentoRealizadoAbertaFechadaComplexidade(cthSeq, cnvCodigo, cspSeq, cth
								.getProcedimentoHospitalarInternoRealizado().getSeq(), tipoGrupoContaSUS);
						Assert.assertEquals("cth " + cthSeq, ssmReal, pair.getSsmStr());
					}
					oldCthSeq = cthSeq;
				}
			} catch (Exception e) {
				logger.info(Arrays.toString(e.getStackTrace()));
				fail("Not expecting exception: " + e.getLocalizedMessage());
			}
		}
	}
}
