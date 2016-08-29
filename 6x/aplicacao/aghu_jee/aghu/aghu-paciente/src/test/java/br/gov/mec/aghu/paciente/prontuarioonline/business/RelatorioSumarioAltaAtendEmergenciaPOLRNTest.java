package br.gov.mec.aghu.paciente.prontuarioonline.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RelatorioSumarioAltaAtendEmergenciaPOLRNTest extends AGHUBaseUnitTest<RelatorioSumarioAltaAtendEmergenciaPOLRN>{

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	
	
	@Test
	public void processarAlergiasDescricaoTriagemTest() {
		StringBuilder vTexto = new StringBuilder();
		vTexto.append("");
		systemUnderTest.processarAlergiasDescricaoTriagem(new MpmAltaSumarioId(), null, vTexto);
		Assert.assertEquals("", vTexto.toString());
	}

}