package br.gov.mec.aghu.faturamento.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.faturamento.dao.FatProcedHospIntCidDAO;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FatProcedHospIntCidONTest extends AGHUBaseUnitTest<FatProcedHospIntCidON>{

	@Mock
	private FatProcedHospIntCidDAO mockedFatProcedHospIntCidDAO;
	
	/**
	 * Testa persistencia do FatProcedHospIntCid 
	 */
	@Test
	public void persistirFatProcedHospIntCid() throws Exception {

		FatProcedHospIntCid elemento = new FatProcedHospIntCid();
		Mockito.when(mockedFatProcedHospIntCidDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(elemento);
		systemUnderTest.persistirFatProcedHospIntCid(elemento, true);
	}	

}
