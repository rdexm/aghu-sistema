package br.gov.mec.aghu.faturamento.business;

import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CaracteristicaItemProcedimentoHospitalarRNTest extends AGHUBaseUnitTest<CaracteristicaItemProcedimentoHospitalarRN>{

	private Log log = LogFactory.getLog(this.getClass());

	@Mock
	private FatCaractItemProcHospDAO mockedFatCaractItemProcHospDAO;
	
	@Test
	public void testObterCaracteristicaProcHospPorId() {

		FatCaractItemProcHosp cih = null;
		FatCaractItemProcHosp returnCih = null;
		Expectations expect = null;
		FatCaractItemProcHospId id = null;

		//setup
		cih = new FatCaractItemProcHosp();
		id = new FatCaractItemProcHospId(
				Short.valueOf((short) 123),
				Integer.valueOf(456),
				Integer.valueOf(789));
		cih.setId(id);
		Mockito.when(mockedFatCaractItemProcHospDAO.obterPorChavePrimaria(id)).thenReturn(cih);
		//assert
		returnCih = systemUnderTest.obterCaracteristicaProcHospPorId(id);
		Assert.assertEquals(
				cih, returnCih);
		Assert.assertEquals(
				cih.getId(), returnCih.getId());
	}

	private FatCaractItemProcHosp[] initExpectCih(
			final FatItensProcedHospitalarId idProcHosp,
			final int[] tipoCaractSeq) {

		FatCaractItemProcHosp[] result = new FatCaractItemProcHosp[tipoCaractSeq.length];
		FatCaractItemProcHosp cih = null;
		FatCaractItemProcHospId cihId = null;

		for (int i = 0; i < tipoCaractSeq.length; i++) {
			cih = new FatCaractItemProcHosp();
			cihId = new FatCaractItemProcHospId(
					idProcHosp.getPhoSeq(),
					idProcHosp.getSeq(),
					Integer.valueOf(tipoCaractSeq[i]));
			cih.setId(cihId);
			result[i] = cih;
			Mockito.when(mockedFatCaractItemProcHospDAO.obterPorItemProcHospTipoCaract(idProcHosp,
					Integer.valueOf(tipoCaractSeq[i]))).thenReturn(cih);
		}

		return result;
	}

	private CaracteristicaItemProcedimentoHospitalarRN getObjRnForTestVerifica(
			final FatItensProcedHospitalarId idProcHosp,
			final int... returnValorCharSTrue) {

		CaracteristicaItemProcedimentoHospitalarRN result = null;
		FatCaractItemProcHosp[] retSTrue = null;
		int[] all = new int[] {
				CaracteristicaItemProcedimentoHospitalarRN.COBRA_BPA_FAT_TIPO_CARACT_ITEM_ID,
				CaracteristicaItemProcedimentoHospitalarRN.COBRA_BPI_FAT_TIPO_CARACT_ITEM_ID,
				CaracteristicaItemProcedimentoHospitalarRN.COBRA_SISCOLO_FAT_TIPO_CARACT_ITEM_ID,
				CaracteristicaItemProcedimentoHospitalarRN.PROC_PRINCIPAL_APAC_FAT_TIPO_CARACT_ITEM_ID,
				};
		int[] rest = new int[all.length - returnValorCharSTrue.length];
		int ndx = 0;
		boolean found = false;

		//true
		retSTrue = this.initExpectCih(
				 idProcHosp, returnValorCharSTrue);
		//false		
		ndx = 0;
		for (int i = 0; i < all.length; i++) {
			found = false;
			for (int j = 0; !found && (j < returnValorCharSTrue.length); j++) {
				if (all[i] == returnValorCharSTrue[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				rest[ndx++] = all[i];
			}
		}
		this.initExpectCih(
				  idProcHosp, rest);

		return result;
	}

	@Test
	public void testVerificaIncompatibilidadeCaracteristicaProcHospBPA() {

		FatItensProcedHospitalarId idProcHosp = null;
		Integer tipoCaract = null;

		//setup
		tipoCaract = Integer
				.valueOf(CaracteristicaItemProcedimentoHospitalarRN.COBRA_BPA_FAT_TIPO_CARACT_ITEM_ID);
		idProcHosp = new FatItensProcedHospitalarId(
				Short.valueOf((short) 123),
				Integer.valueOf(456));
		//assert OK BPA = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert OK BPA = 'S' && SISCOLO = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert NOK BPA = 'S' && BPI = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_00775, e.getCode());
		}
		//assert NOK BPA = 'S' && ProcPrinc = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_00775, e.getCode());
		}
	}

	@Test
	public void testVerificaIncompatibilidadeCaracteristicaProcHospBPI() {

		FatItensProcedHospitalarId idProcHosp = null;
		Integer tipoCaract = null;

		//setup
		tipoCaract = Integer
				.valueOf(CaracteristicaItemProcedimentoHospitalarRN.COBRA_BPI_FAT_TIPO_CARACT_ITEM_ID);
		idProcHosp = new FatItensProcedHospitalarId(
				Short.valueOf((short) 123),
				Integer.valueOf(456));
		//assert OK BPI = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert OK BPI = 'S' && SISCOLO = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert NOK BPI = 'S' && BPA = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_01076, e.getCode());
		}
		//assert NOK BPI = 'S' && ProcPrinc = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_01076, e.getCode());
		}
	}

	@Test
	public void testVerificaIncompatibilidadeCaracteristicaProcHospSiscolo() {

		FatItensProcedHospitalarId idProcHosp = null;
		Integer tipoCaract = null;

		//setup
		tipoCaract = Integer
				.valueOf(CaracteristicaItemProcedimentoHospitalarRN.COBRA_SISCOLO_FAT_TIPO_CARACT_ITEM_ID);
		idProcHosp = new FatItensProcedHospitalarId(
				Short.valueOf((short) 123),
				Integer.valueOf(456));
		//assert OK SISCOLO = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert OK SISCOLO = 'S' && BPA = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert OK SISCOLO = 'S' && BPI = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert NOK SISCOLO = 'S' && ProcPrinc = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_00776, e.getCode());
		}
	}

	@Test
	public void testVerificaIncompatibilidadeCaracteristicaProcHospProcPrinc() {

		CaracteristicaItemProcedimentoHospitalarRN objRn = null;
		Expectations expect = null;
		FatItensProcedHospitalarId idProcHosp = null;
		Integer tipoCaract = null;

		//setup
		tipoCaract = Integer
				.valueOf(CaracteristicaItemProcedimentoHospitalarRN.PROC_PRINCIPAL_APAC_FAT_TIPO_CARACT_ITEM_ID);
		idProcHosp = new FatItensProcedHospitalarId(
				Short.valueOf((short) 123),
				Integer.valueOf(456));
		//assert OK ProcPrinc = 'S'
		try {
			Assert.assertTrue(
					systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
							idProcHosp,
							tipoCaract));
		} catch (ApplicationBusinessException e) {
			this.log.info(e);
			fail("Not expecting exception: " + e.getCode());
		}
		//assert NOK ProcPrinc = 'S' && SISCOLO = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_00777, e.getCode());
		}
		//assert NOK ProcPrinc = 'S' && BPA = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_00777, e.getCode());
		}
		//assert NOK ProcPrinc = 'S' && BPI = 'S'
		try {
			systemUnderTest.verificaIncompatibilidadeCaracteristicaProcHosp(
					idProcHosp,
					tipoCaract);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					FaturamentoExceptionCode.FAT_00777, e.getCode());
		}
	}
}
