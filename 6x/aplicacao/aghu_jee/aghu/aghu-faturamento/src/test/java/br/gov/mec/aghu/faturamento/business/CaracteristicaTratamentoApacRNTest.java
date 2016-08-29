package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.faturamento.dao.FatTratamentoApacCaractDAO;
import br.gov.mec.aghu.faturamento.vo.DadosCaracteristicaTratamentoApacVO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CaracteristicaTratamentoApacRNTest extends AGHUBaseUnitTest<CaracteristicaTratamentoApacRN>{

	@Mock
	private CaracteristicaItemProcedimentoHospitalarRN mockedCaracteristicaItemProcedimentoHospitalarRN;
	@Mock
	private FatTratamentoApacCaractDAO mockedFatTratamentoApacCaractDAO;
	
	@Test
	public void testObterDadosCaracteristicaTratamentoApac() {

		FatCaractItemProcHosp cih = null;
		DadosCaracteristicaTratamentoApacVO dcVO = null;
		FatCaractItemProcHospId cihId = null;
		FatItensProcedHospitalarId iphId = null;

		//setup
		cih = new FatCaractItemProcHosp();
		cihId = new FatCaractItemProcHospId(
				Short.valueOf((short) 123),
				Integer.valueOf(456),
				Integer.valueOf(789));
		cih.setId(cihId);
		cih.setValorChar("val");
		cih.setValorNumerico(Integer.valueOf(321));
		cih.setValorData(new Date());
		iphId = new FatItensProcedHospitalarId(cihId.getIphPhoSeq(),
				cihId.getIphSeq());


		Mockito.when(mockedCaracteristicaItemProcedimentoHospitalarRN.obterCaracteristicaProcHospPorId(cihId)).thenReturn(cih);
		
		//assert
		dcVO = systemUnderTest.obterDadosCaracteristicaTratamentoApac(
				iphId, cihId);
		Assert.assertEquals(
				cih.getValorNumerico(), dcVO.getValorNumerico());
		Assert.assertEquals(
				cih.getValorChar(), dcVO.getValorChar());
		Assert.assertEquals(
				cih.getValorData(), dcVO.getValorData());
	}
}
