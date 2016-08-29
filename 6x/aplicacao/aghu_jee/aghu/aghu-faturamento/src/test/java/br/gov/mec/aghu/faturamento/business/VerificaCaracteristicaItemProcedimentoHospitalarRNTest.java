package br.gov.mec.aghu.faturamento.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VerificaCaracteristicaItemProcedimentoHospitalarRNTest extends AGHUBaseUnitTest<VerificaCaracteristicaItemProcedimentoHospitalarRN>{

	@Mock
	FatCaractItemProcHospDAO mockedCiphDao = null;
	@Mock
	FatTipoCaractItemSeqCache mockedFatTipoCaractItemSeqCache = null;


	@Test
	public void testVerificarCaracteristicaItemProcHosp() {

		VerificaCaracteristicaItemProcedimentoHospitalarRN objRn = null;
		boolean result = false;
		Short iphPhoSeq = null;
		Integer iphSeq = null;
		DominioFatTipoCaractItem caracteristica = null;
		FatCaractItemProcHosp ciph = null;

		//setup
		iphPhoSeq = Short.valueOf((short) 11);
		iphSeq = Integer.valueOf(12);
		caracteristica = DominioFatTipoCaractItem.ACTP;
		//assert: false -> cihp == null
		result = systemUnderTest.verificarCaracteristicaItemProcHosp(iphPhoSeq, iphSeq, caracteristica);
		Assert.assertFalse(result);
		//setup: false -> ciph != null ciph.valChar != 'S'
		ciph = new FatCaractItemProcHosp();
		ciph.setValorChar(VerificaCaracteristicaItemProcedimentoHospitalarRN.STRING_S.toLowerCase());
		//assert: false -> ciph != null ciph.valChar != 'S'
		result = systemUnderTest.verificarCaracteristicaItemProcHosp(iphPhoSeq, iphSeq, caracteristica);
		Assert.assertFalse(result);
		//setup: true -> ciph.valChar == null
		ciph = new FatCaractItemProcHosp();
		ciph.setValorChar(null);
		//assert: true -> ciph.valChar == null
		result = systemUnderTest.verificarCaracteristicaItemProcHosp(iphPhoSeq, iphSeq, caracteristica);
		Assert.assertFalse(result);
		//setup: true -> ciph.valChar != 'S'
		ciph = new FatCaractItemProcHosp();
		ciph.setValorChar(VerificaCaracteristicaItemProcedimentoHospitalarRN.STRING_S.toLowerCase());
		//assert: true -> ciph.valChar != 'S'
		result = systemUnderTest.verificarCaracteristicaItemProcHosp(iphPhoSeq, iphSeq, caracteristica);
		Assert.assertFalse(result);
	}
}
