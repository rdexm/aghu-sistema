package br.gov.mec.aghu.faturamento.business;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.hibernate.jdbc.Expectations;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSituacaoContaVO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;
import br.gov.mec.aghu.faturamento.vo.ParSsmSolicRealizVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FaturamentoRNTest extends AGHUBaseUnitTest<FaturamentoRN> {

	private static final Short MAGIC_SHORT_TIPO_GRP_SUS_EQ_32 = Short.valueOf((short) 32);
	static final Logger log = Logger.getAnonymousLogger();
	@Mock
	FatEspelhoAihDAO mockedEaiDao;
	@Mock
	FatContasHospitalaresDAO mockedCthDao;
	@Mock
	FatCaractFinanciamentoDAO mockedCfcDao;
	@Mock
	IParametroFacade mockedParametroFacade;

	
	final static AghParametros AGH_PARAM = new AghParametros("S");
	
	@Test
	public void testValidarAIHInvalido(){
		long nroAIH = 34L;
		
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_VALIDA_NRO_AIH)).thenReturn(AGH_PARAM);

			if(systemUnderTest.validaNumeroAIHInformadoManualmente(nroAIH)){
				Assert.fail("Deveria ter retornado false ");
			}
		} catch (ApplicationBusinessException e) {
			Assert.fail("Deveria ter retornado false ");
		}
	}	
	
	@Test
	public void testValidarAIHValido(){
		long nroAIH = 2295245470L;
		
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_VALIDA_NRO_AIH)).thenReturn(AGH_PARAM);

			if(!systemUnderTest.validaNumeroAIHInformadoManualmente(nroAIH)){
				Assert.fail("Deveria ter retornado false ");
			}
		} catch (ApplicationBusinessException e) {
			Assert.fail("Deveria ter retornado false ");
		}
	}

	@Test
	public void testSelecionarSsmEntreSolicitadoRealizado() {

		String result = null;
		DominioSituacaoSSM situacaoSSM = null;
		String ssmSol = null;
		String ssmReal = null;
		String msgSsmSolNull = null;
		String msgSsmRealNull = null;

		//setup null
		msgSsmSolNull = "msgSsmSolNull";
		msgSsmRealNull = "msgSsmRealNull";
		result = systemUnderTest.selecionarSsmEntreSolicitadoRealizado(situacaoSSM, ssmSol, ssmReal, msgSsmSolNull, msgSsmRealNull);
		//assert
		Assert.assertNull(result);
		//setup S && ssmSol = null
		situacaoSSM = DominioSituacaoSSM.S;
		result = systemUnderTest.selecionarSsmEntreSolicitadoRealizado(situacaoSSM, ssmSol, ssmReal, msgSsmSolNull, msgSsmRealNull);
		//assert
		Assert.assertNull(result);
		//setup R && ssmReal = null
		situacaoSSM = DominioSituacaoSSM.R;
		result = systemUnderTest.selecionarSsmEntreSolicitadoRealizado(situacaoSSM, ssmSol, ssmReal, msgSsmSolNull, msgSsmRealNull);
		//assert
		Assert.assertNull(result);
		//setup S && ssmSol != null
		situacaoSSM = DominioSituacaoSSM.S;
		ssmSol = "ssmSol";
		result = systemUnderTest.selecionarSsmEntreSolicitadoRealizado(situacaoSSM, ssmSol, ssmReal, msgSsmSolNull, msgSsmRealNull);
		//assert
		Assert.assertEquals(ssmSol, result);
		//setup R && ssmReal != null
		situacaoSSM = DominioSituacaoSSM.R;
		ssmReal = "ssmReal";
		result = systemUnderTest.selecionarSsmEntreSolicitadoRealizado(situacaoSSM, ssmSol, ssmReal, msgSsmSolNull, msgSsmRealNull);
		//assert
		Assert.assertEquals(ssmReal, result);
	}

	@Test
	public void testSelecionarSsmConformeSituacao() {

		String result = null;
		DominioSituacaoSSM situacaoSSM = null;
		DominioSituacaoConta[] arrSitConta = null;
		Integer cthSeq = null;
		String ssmSolA = null;
		String ssmRealA = null;
		String ssmSolB = null;
		String ssmRealB = null;

		// setup conta = null
		result = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(situacaoSSM, null, cthSeq, ssmSolA, ssmRealA, ssmSolB, ssmRealB);
		// assert
		Assert.assertNull(result);
		// setup conta != null
		arrSitConta = DominioSituacaoConta.values();
		for (DominioSituacaoConta situacaoConta : arrSitConta) {
			result = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(situacaoSSM, situacaoConta, cthSeq, ssmSolA, ssmRealA, ssmSolB, ssmRealB);
			// assert conta != null && ssm != null
			Assert.assertNull(result);
		}
		// setup
		arrSitConta = DominioSituacaoConta.values();
		situacaoSSM = DominioSituacaoSSM.R;
		for (DominioSituacaoConta situacaoConta : arrSitConta) {
			result = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(situacaoSSM, situacaoConta, cthSeq, ssmSolA, ssmRealA, ssmSolB, ssmRealB);
			// assert
			Assert.assertNull(result);
		}
		// assert conta != null && ssm != null && ssm != null
		Assert.assertNull(result);
		// setup
		arrSitConta = new DominioSituacaoConta[] {
				DominioSituacaoConta.E,
				DominioSituacaoConta.O,
				DominioSituacaoConta.R,
		};
		situacaoSSM = DominioSituacaoSSM.S;
		ssmSolA = "ssmSolA";
		for (DominioSituacaoConta situacaoConta : arrSitConta) {
			result = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(situacaoSSM, situacaoConta, cthSeq, ssmSolA, ssmRealA, ssmSolB, ssmRealB);
			// assert
			Assert.assertEquals(ssmSolA, result);
		}
		// setup
		arrSitConta = new DominioSituacaoConta[] {
				DominioSituacaoConta.A,
				DominioSituacaoConta.F,
		};
		situacaoSSM = DominioSituacaoSSM.S;
		ssmSolB = "ssmSolB";
		for (DominioSituacaoConta situacaoConta : arrSitConta) {
			result = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(situacaoSSM, situacaoConta, cthSeq, ssmSolA, ssmRealA, ssmSolB, ssmRealB);
			// assert
			Assert.assertEquals(ssmSolB, result);
		}
	}

	@Test
	public void testBuscaSSM() {

		String result = null;
		Integer pCthSeq = null;
		Short pCspCnvCodigo = null;
		Byte pCspSeq = null;
		DominioSituacaoSSM situacaoSSM = null;
		FatContasHospitalares cth = null;
		String ssm = null;
		Expectations expect = null;
		Short tipoGrpSus = null;
		String[] arrSsm = null;

		// setup conta = null
		tipoGrpSus = MAGIC_SHORT_TIPO_GRP_SUS_EQ_32;
		pCthSeq = Integer.valueOf(123);
		Mockito.when(mockedCthDao.obterPorChavePrimaria(pCthSeq)).thenReturn(cth);
		// assert
		//result = obj.buscaSSM(pCthSeq, pCspCnvCodigo, pCspSeq, situacaoSSM);
		Assert.assertNull(result);
		//setup conta != null
		pCthSeq = Integer.valueOf(321);
		cth = new FatContasHospitalares();
		cth.setIndSituacao(DominioSituacaoConta.O);
		Mockito.when(mockedCthDao.obterPorChavePrimaria(pCthSeq)).thenReturn(cth);
		// assert
		//result = obj.buscaSSM(pCthSeq, pCspCnvCodigo, pCspSeq, situacaoSSM);
		Assert.assertNull(result);
		//setup conta != null
		arrSsm = new String[] {
				"ssmSolA",
				"ssmRealA",
				"ssmSolB",
				"ssmRealB"
		};
		pCthSeq = Integer.valueOf(322);
		situacaoSSM = DominioSituacaoSSM.S;
		Mockito.when(mockedCthDao.obterPorChavePrimaria(pCthSeq)).thenReturn(cth);
		Mockito.when(mockedCthDao.buscaProcedimentoSolicitado(pCthSeq, pCspCnvCodigo, pCspSeq, tipoGrpSus)).thenReturn(arrSsm[2]);
		Mockito.when(mockedCthDao.buscaProcedimentoRealizado(pCthSeq, pCspCnvCodigo, pCspSeq, tipoGrpSus)).thenReturn(arrSsm[3]);
		Mockito.when(mockedEaiDao.buscaProcedimentoSolicitado(pCthSeq, pCspCnvCodigo, pCspSeq)).thenReturn(arrSsm[0]);
		Mockito.when(mockedEaiDao.buscaProcedimentoRealizado(pCthSeq, pCspCnvCodigo, pCspSeq)).thenReturn(arrSsm[1]);
		// assert
		for (DominioSituacaoSSM ssmSit : DominioSituacaoSSM.values()) {
			for (DominioSituacaoConta cthSit : DominioSituacaoConta.values()) {
				cth.setIndSituacao(cthSit);
				ssm = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(ssmSit, cthSit, pCthSeq, arrSsm[0], arrSsm[1], arrSsm[2], arrSsm[3]);
			//	result = obj.buscaSSM(pCthSeq, pCspCnvCodigo, pCspSeq, ssmSit);
			}
		}
	}

	@Test
	public void testBuscaSsmComplexidade() {

		String result = null;
		Integer pCthSeq = null;
		Short pCspCnvCodigo = null;
		Byte pCspSeq = null;
		Integer pCthPhiSeq = null;
		Integer pCthPhiSeqRealizado = null;
		DominioSituacaoSSM situacaoSSM = null;
		FatContasHospitalares cth = null;
		String ssm = null;
		Expectations expect = null;
		Short tipoGrpSus = null;
		String[] arrSsm = null;

		// setup conta = null
		tipoGrpSus = MAGIC_SHORT_TIPO_GRP_SUS_EQ_32;
		pCthSeq = Integer.valueOf(123);

		Mockito.when(mockedCthDao.obterPorChavePrimaria(pCthSeq)).thenReturn(cth);
		// assert
		//result = obj.buscaSsmComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq, pCthPhiSeqRealizado, situacaoSSM);
		Assert.assertNull(result);
		//setup conta != null
		pCthSeq = Integer.valueOf(321);
		cth = new FatContasHospitalares();
		cth.setIndSituacao(DominioSituacaoConta.O);
		Mockito.when(mockedCthDao.obterPorChavePrimaria(pCthSeq)).thenReturn(cth);
		// assert
		//result = obj.buscaSsmComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq, pCthPhiSeqRealizado, situacaoSSM);
		Assert.assertNull(result);
		//setup conta != null
		arrSsm = new String[] {
				"ssmSolA",
				"ssmRealA",
				"ssmSolB",
				"ssmRealB"
		};
		pCthSeq = Integer.valueOf(322);
		situacaoSSM = DominioSituacaoSSM.S;
		Mockito.when(mockedCthDao.obterPorChavePrimaria(pCthSeq)).thenReturn(cth);
		Mockito.when(mockedCfcDao.buscaProcedimentoSolicitadoComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq)).thenReturn(arrSsm[0]);
		Mockito.when(mockedCfcDao.buscaProcedimentoRealizadoComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeqRealizado)).thenReturn(arrSsm[1]);
		Mockito.when(mockedCfcDao.buscaProcedimentoSolicitadoAbertaFechadaComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq, tipoGrpSus)).thenReturn(arrSsm[2]);
		Mockito.when(mockedCfcDao.buscaProcedimentoRealizadoAbertaFechadaComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeqRealizado, tipoGrpSus)).thenReturn(arrSsm[3]);
		// assert
		for (DominioSituacaoSSM ssmSit : DominioSituacaoSSM.values()) {
			for (DominioSituacaoConta cthSit : DominioSituacaoConta.values()) {
				cth.setIndSituacao(cthSit);
				ssm = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(ssmSit, cthSit, pCthSeq, arrSsm[0], arrSsm[1], arrSsm[2], arrSsm[3]);
				//result = obj.buscaSsmComplexidade(pCthSeq, pCspCnvCodigo, pCspSeq, pCthPhiSeq, pCthPhiSeqRealizado, ssmSit);
			}
		}
	}

	@Test
	public void testPopularMapaVoComSsm() {

		Map<Integer, ParSsmSolicRealizVO> result = null;
		List<ParCthSeqSituacaoContaVO> listaCthSit = null;
		ParCthSeqSituacaoContaVO vo = null;
		ParSsmSolicRealizVO voRes = null;
		List<String> listaStrSsm = null;
		Short tipoGrpSus = null;
		String ssmSol = null;
		String ssmReal = null;

		//setup
		tipoGrpSus = MAGIC_SHORT_TIPO_GRP_SUS_EQ_32;
		listaCthSit = new LinkedList<ParCthSeqSituacaoContaVO>();
		//assert
		result = systemUnderTest.popularMapaVoComSsm(listaCthSit);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
		//setup
		listaStrSsm = new LinkedList<String>();
		listaStrSsm.add("ssm1");
		listaStrSsm.add("ssm2");
		listaStrSsm.add("ssm3");
		listaStrSsm.add("ssm4");
		vo = new ParCthSeqSituacaoContaVO();
		vo.setCthSeq(Integer.valueOf(123));
		vo.setListaSsmStr(listaStrSsm);
		vo.setSituacaoConta(DominioSituacaoConta.A);
		listaCthSit.add(vo);
		//assert
		result = systemUnderTest.popularMapaVoComSsm(listaCthSit);
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		ssmSol = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(
				DominioSituacaoSSM.S,
				vo.getSituacaoConta(),
				vo.getCthSeq(),
				listaStrSsm.get(0),
				listaStrSsm.get(1),
				listaStrSsm.get(2),
				listaStrSsm.get(3));
		ssmReal = systemUnderTest.selecionarSsmConformeSituacaoContaSsm(
				DominioSituacaoSSM.R,
				vo.getSituacaoConta(),
				vo.getCthSeq(),
				listaStrSsm.get(0),
				listaStrSsm.get(1),
				listaStrSsm.get(2),
				listaStrSsm.get(3));
		voRes = result.get(vo.getCthSeq());
		Assert.assertEquals(vo.getCthSeq(), voRes.getCthSeq());
		Assert.assertEquals(ssmSol, voRes.getSsmStrSolicitado());
		Assert.assertEquals(ssmReal, voRes.getSsmStrRealizado());
	}

	@Test
	public void testObterMapaVoParcialParaListSsm() {

		Map<Integer, ParCthSeqSituacaoContaVO> result = null;
		List<ParCthSeqSituacaoContaVO> listaCthSit = null;
		ParCthSeqSituacaoContaVO vo = null;
		Short tipoGrpSus = null;

		//setup
		tipoGrpSus = MAGIC_SHORT_TIPO_GRP_SUS_EQ_32;
		listaCthSit = new LinkedList<ParCthSeqSituacaoContaVO>();
		//assert
		result = systemUnderTest.obterMapaVoParcialParaListSsm(listaCthSit);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
		//setup
		vo = new ParCthSeqSituacaoContaVO();
		vo.setCthSeq(Integer.valueOf(123));
		listaCthSit.add(vo);
		//assert
		result = systemUnderTest.obterMapaVoParcialParaListSsm(listaCthSit);
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(vo, result.get(vo.getCthSeq()));
		Assert.assertEquals(4, vo.getListaSsmStr().size());
	}

	@Test
	public void testPopularListaStrSsm() {

		Map<Integer, ParCthSeqSituacaoContaVO> parcial;
		List<ParSsmSolicRealizVO> listaA;
		List<ParSsmSolicRealizVO> listaB;
		ParCthSeqSituacaoContaVO voMap = null;
		ParSsmSolicRealizVO voA = null;
		ParSsmSolicRealizVO voB = null;
		Short tipoGrpSus = null;
		List<String> listaStrSsm = null;
		Integer cthSeq = null;
		String[] ssmStr = null;

		//setup
		tipoGrpSus = MAGIC_SHORT_TIPO_GRP_SUS_EQ_32;
		parcial = new HashMap<Integer, ParCthSeqSituacaoContaVO>();
		listaA = new LinkedList<ParSsmSolicRealizVO>();
		listaB = new LinkedList<ParSsmSolicRealizVO>();
		//assert
		try {
			systemUnderTest.popularListaStrSsm(parcial, listaA, listaB);
		} catch (Exception e) {
			log.info(Arrays.toString(e.getStackTrace()));
			fail("Not expecting exception: " + e.getLocalizedMessage());
		}
		//setup
		ssmStr = new String[] {
				"ssm1",
				"ssm2",
				"ssm3",
				"ssm4",
		};
		cthSeq = Integer.valueOf(321);
		voA = new ParSsmSolicRealizVO();
		voA.setSsmStrSolicitado(ssmStr[0] + "A");
		voA.setSsmStrRealizado(ssmStr[1] + "A");
		listaA.add(voA);
		voB = new ParSsmSolicRealizVO();
		voB.setSsmStrSolicitado(ssmStr[2] + "B");
		voB.setSsmStrRealizado(ssmStr[3] + "B");
		listaB.add(voB);
		listaStrSsm = new LinkedList<String>();
		for (String s : ssmStr) {
			listaStrSsm.add(s);
		}
		voMap = new ParCthSeqSituacaoContaVO();
		voMap.setListaSsmStr(listaStrSsm);
		parcial = new HashMap<Integer, ParCthSeqSituacaoContaVO>();
		parcial.put(cthSeq, voMap);
		//assert
		try {
			systemUnderTest.popularListaStrSsm(parcial, listaA, listaB);
			Assert.assertFalse(parcial.isEmpty());
			Assert.assertEquals(voMap, parcial.get(cthSeq));
			listaStrSsm = voMap.getListaSsmStr();
			Assert.assertEquals(ssmStr.length, listaStrSsm.size());
			for (String s : ssmStr) {
				Assert.assertTrue(listaStrSsm.contains(s));
			}
		} catch (Exception e) {
			log.info(Arrays.toString(e.getStackTrace()));
			fail("Not expecting exception: " + e.getLocalizedMessage());
		}
		//setup
		voA.setCthSeq(cthSeq);
		voB.setCthSeq(null);
		listaStrSsm = voMap.getListaSsmStr();
		listaStrSsm.clear();
		for (String s : ssmStr) {
			listaStrSsm.add(s);
		}
		//assert
		try {
			systemUnderTest.popularListaStrSsm(parcial, listaA, listaB);
			Assert.assertFalse(parcial.isEmpty());
			Assert.assertEquals(voMap, parcial.get(cthSeq));
			listaStrSsm = voMap.getListaSsmStr();
			Assert.assertEquals(ssmStr.length, listaStrSsm.size());
			for (String s : ssmStr) {
				if (ssmStr[0].equals(s) || ssmStr[1].equals(s)) {
					Assert.assertTrue(s, listaStrSsm.contains(s + "A"));
				} else {
					Assert.assertTrue(s, listaStrSsm.contains(s));
				}
			}
		} catch (Exception e) {
			log.info(Arrays.toString(e.getStackTrace()));
			fail("Not expecting exception: " + e.getLocalizedMessage());
		}
		//setup
		voA.setCthSeq(null);
		voB.setCthSeq(cthSeq);
		listaStrSsm = voMap.getListaSsmStr();
		listaStrSsm.clear();
		for (String s : ssmStr) {
			listaStrSsm.add(s);
		}
		//assert
		try {
			systemUnderTest.popularListaStrSsm(parcial, listaA, listaB);
			Assert.assertFalse(parcial.isEmpty());
			Assert.assertEquals(voMap, parcial.get(cthSeq));
			listaStrSsm = voMap.getListaSsmStr();
			Assert.assertEquals(ssmStr.length, listaStrSsm.size());
			for (String s : ssmStr) {
				if (ssmStr[2].equals(s) || ssmStr[3].equals(s)) {
					Assert.assertTrue(s, listaStrSsm.contains(s + "B"));
				} else {
					Assert.assertTrue(s, listaStrSsm.contains(s));
				}
			}
		} catch (Exception e) {
			log.info(Arrays.toString(e.getStackTrace()));
			fail("Not expecting exception: " + e.getLocalizedMessage());
		}
		//setup
		voA.setCthSeq(cthSeq);
		voB.setCthSeq(cthSeq);
		listaStrSsm = voMap.getListaSsmStr();
		listaStrSsm.clear();
		for (String s : ssmStr) {
			listaStrSsm.add(s);
		}
		//assert
		try {
			systemUnderTest.popularListaStrSsm(parcial, listaA, listaB);
			Assert.assertFalse(parcial.isEmpty());
			Assert.assertEquals(voMap, parcial.get(cthSeq));
			listaStrSsm = voMap.getListaSsmStr();
			Assert.assertEquals(ssmStr.length, listaStrSsm.size());
			for (String s : ssmStr) {
				if (ssmStr[0].equals(s) || ssmStr[1].equals(s)) {
					Assert.assertTrue(s, listaStrSsm.contains(s + "A"));
				} else if (ssmStr[2].equals(s) || ssmStr[3].equals(s)) {
					Assert.assertTrue(s, listaStrSsm.contains(s + "B"));
				} else {
					Assert.assertTrue(s, listaStrSsm.contains(s));
				}
			}
		} catch (Exception e) {
			log.info(Arrays.toString(e.getStackTrace()));
			fail("Not expecting exception: " + e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testAgregarListas() {

		List<ParSsmSolicRealizVO> result = null;
		ParSsmSolicRealizVO resVo = null;
		List<ParCthSeqSsmVO> listaParSol = null;
		List<ParCthSeqSsmVO> listaParReal = null;
		ParCthSeqSsmVO voS = null;
		ParCthSeqSsmVO voR = null;
		Integer cthSeq = null;
		
		//setup
		listaParSol = new LinkedList<ParCthSeqSsmVO>();
		listaParReal = new LinkedList<ParCthSeqSsmVO>();
		//assert
		result = FaturamentoRN.agregarListas(listaParSol, listaParReal);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
		//setup
		cthSeq = Integer.valueOf(123);
		voS = new ParCthSeqSsmVO();	
		voS.setCthSeq(cthSeq);
		voS.setSsmStr("ssmS");
		listaParSol.add(voS);
		voR = new ParCthSeqSsmVO();
		voR.setCthSeq(cthSeq);
		voR.setSsmStr("ssmR");
		listaParReal.add(voR);
		//assert
		result = FaturamentoRN.agregarListas(listaParSol, listaParReal);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		resVo = result.get(0);
		Assert.assertEquals(cthSeq, resVo.getCthSeq());
		Assert.assertEquals(voS.getSsmStr(), resVo.getSsmStrSolicitado());
		Assert.assertEquals(voR.getSsmStr(), resVo.getSsmStrRealizado());
		//setup
		listaParSol.clear();
		listaParReal.clear();
		cthSeq = Integer.valueOf(123);
		voS = new ParCthSeqSsmVO();	
		voS.setCthSeq(cthSeq);
		voS.setSsmStr("ssmS");
		listaParSol.add(voS);
		voR = new ParCthSeqSsmVO();
		cthSeq = Integer.valueOf(321);
		voR.setCthSeq(cthSeq);
		voR.setSsmStr("ssmR");
		listaParReal.add(voR);
		//assert
		result = FaturamentoRN.agregarListas(listaParSol, listaParReal);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
		for (ParSsmSolicRealizVO vo : result) {
			cthSeq = vo.getCthSeq();
			if (voS.getCthSeq().equals(cthSeq)) {				
				Assert.assertEquals(voS.getSsmStr(), vo.getSsmStrSolicitado());
				Assert.assertEquals(null, vo.getSsmStrRealizado());
			} else if (voR.getCthSeq().equals(cthSeq)) {
				Assert.assertEquals(null, vo.getSsmStrSolicitado());
				Assert.assertEquals(voR.getSsmStr(), vo.getSsmStrRealizado());			
			} else {
				fail("CTH_SEQ desconhecido: " + cthSeq);
			}
		}
	}
}
