package br.gov.mec.aghu.estoque.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MovimentoMaterialONTest extends AGHUBaseUnitTest<MovimentoMaterialON>{

	@Mock
	private SceMovimentoMaterialDAO mockedSceMovimentoMaterialDAO;
	
	@Test
	public void pesquisarDatasCompetenciasMovimentosMateriaisPorMesAnoTest(){
		try {
			
			String objct = "";
		
			systemUnderTest.pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(objct);
			
		} catch (BaseException e) {
			Assert.fail();
		}
	}
}
