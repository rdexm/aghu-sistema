package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterDocumentoFiscalEntradaONTest extends AGHUBaseUnitTest<ManterDocumentoFiscalEntradaON>{
	
	@Mock
	private SceDocumentoFiscalEntradaRN mockedSceDocumentoFiscalEntradaRN;
	
	@Test
	public void persistirDocumentoFiscalEntradaTest(){
		try {
			
			SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada = new SceDocumentoFiscalEntrada();			

			systemUnderTest.persistirDocumentoFiscalEntrada(sceDocumentoFiscalEntrada);
			
		} catch (BaseException e) {
			Assert.fail();
		}
	}
}
