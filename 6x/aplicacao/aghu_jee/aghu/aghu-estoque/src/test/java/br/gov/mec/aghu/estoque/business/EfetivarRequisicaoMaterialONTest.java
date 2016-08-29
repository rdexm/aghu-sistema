package br.gov.mec.aghu.estoque.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EfetivarRequisicaoMaterialONTest extends AGHUBaseUnitTest<EfetivarRequisicaoMaterialON>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private SceReqMateriaisRN mockedSceReqMateriaisRN;
	@Mock
	private SceItemRmsRN mockedSceItemRmsRN;
	
	@Test
	public void efetivarRequisicaoMaterialTest(){
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();			
		
			systemUnderTest.efetivarRequisicaoMaterial(sceReqMateriaisRetorno, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
	}
	
	@Test
	public void gravarItensRequisicaoMaterialTest(){
		try {
			
			final SceReqMaterial sceReqMateriaisRetorno = new SceReqMaterial();			
		
			systemUnderTest.efetivarRequisicaoMaterial(sceReqMateriaisRetorno, NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
	}

	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
	}

}
