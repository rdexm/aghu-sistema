package br.gov.mec.aghu.faturamento;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;

public class FatContasHospitalaresDAOTest extends AbstractDAOTest<FatContasHospitalaresDAO> {

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
	protected FatContasHospitalaresDAO doDaoUnderTests() {
		return new FatContasHospitalaresDAO() {
			private static final long serialVersionUID = 2107802329281368947L;

			@Override
			protected Query createHibernateQuery(String query) {
				return FatContasHospitalaresDAOTest.this.createHibernateQuery(query);
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
	public void testListarSsmParaListaCthSeq() {

		List<ParCthSeqSsmVO> resS = null;
		List<ParCthSeqSsmVO> resR = null;
		String ssmSol = null;
		String ssmReal = null;
		List<Integer> listaCthSeq = null;
		Short cnvCodigo = null;
		Byte cspSeq = null;
		Short tipoGrupoContaSUS = null;
		Integer cthSeq = null;
		Integer oldCthSeq = null;
		
		if (isEntityManagerOk()) {
			//setup
			tipoGrupoContaSUS = Short.valueOf((short)6);
			cnvCodigo = Short.valueOf((short) 1);
			cspSeq = Byte.valueOf((byte) 1);
			listaCthSeq = Arrays.asList(ARRAY_CTH_SEQ_CASOS_TESTES);
			//assert
			resS = getDaoUnderTests().listarSsmParaListaCthSeq(listaCthSeq, cnvCodigo, cspSeq, tipoGrupoContaSUS, DominioSituacaoSSM.S);
			resR = getDaoUnderTests().listarSsmParaListaCthSeq(listaCthSeq, cnvCodigo, cspSeq, tipoGrupoContaSUS, DominioSituacaoSSM.R);
			Assert.assertNotNull(resS);
			Assert.assertNotNull(resR);
			logger.info(resS.toString());
			logger.info(resR.toString());
			oldCthSeq = null;
			for (ParCthSeqSsmVO pair : resS) {
				cthSeq = pair.getCthSeq();
				if (!cthSeq.equals(oldCthSeq)) {
					Assert.assertTrue("cth " + cthSeq, listaCthSeq.contains(cthSeq));
					ssmSol = getDaoUnderTests().buscaProcedimentoSolicitado(cthSeq, cnvCodigo, cspSeq, tipoGrupoContaSUS);
					Assert.assertEquals("cth " + cthSeq, ssmSol, pair.getSsmStr());
				}
				oldCthSeq = cthSeq;
			}
			oldCthSeq = null;
			for (ParCthSeqSsmVO pair : resR) {
				cthSeq = pair.getCthSeq();
				if (!cthSeq.equals(oldCthSeq)) {
					Assert.assertTrue("cth " + cthSeq, listaCthSeq.contains(cthSeq));
					ssmReal = getDaoUnderTests().buscaProcedimentoRealizado(cthSeq, cnvCodigo, cspSeq, tipoGrupoContaSUS);
					Assert.assertEquals("cth " + cthSeq, ssmReal, pair.getSsmStr());
				}
				oldCthSeq = cthSeq;
			}
		}
	}
}