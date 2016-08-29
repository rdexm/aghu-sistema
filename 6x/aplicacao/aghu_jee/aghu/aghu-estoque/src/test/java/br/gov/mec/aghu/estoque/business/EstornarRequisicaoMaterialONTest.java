package br.gov.mec.aghu.estoque.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EstornarRequisicaoMaterialONTest extends AGHUBaseUnitTest<EstornarRequisicaoMaterialON>{

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private SceReqMateriaisRN mockedSceReqMateriaisRN;
	
	@Test
	public void estornarRequisicaoMaterialTest(){
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();			
		
			systemUnderTest.estornarRequisicaoMaterial(sceReqMateriaisRetorno, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
	}

	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
	}

}
