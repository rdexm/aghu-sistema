package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GerarRequisicaoMaterialONTest extends AGHUBaseUnitTest<GerarRequisicaoMaterialON>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private SceReqMateriaisRN mockedSceReqMateriaisRN;
	@Mock
	private SceItemRmsRN mockedSceItemRmsRN;
	
	@Test
	public void persistirRequisicaoMaterialTest(){
		try {
			
			SceReqMaterial sceReqMateriais = new SceReqMaterial();			
		
			systemUnderTest.persistirRequisicaoMaterial(sceReqMateriais, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			Assert.fail();
		}
	}
}
