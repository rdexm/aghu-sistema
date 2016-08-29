package br.gov.mec.aghu.estoque.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EstornarNotaRecebimentoONTest extends AGHUBaseUnitTest<EstornarNotaRecebimentoON>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private SceNotaRecebimentoRN mockedSceNotaRecebimentoRN;
	@Mock
	private SceNotaRecebimentoDAO mockedSceNotaRecebimentoDAO;
	
	@Test
	public void estornarNotaRecebimentoTest(){
		try {
			
			SceNotaRecebimento sceReqMateriais = new SceNotaRecebimento();			
		
			Mockito.when(mockedSceNotaRecebimentoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(new SceNotaRecebimento());
			Mockito.when(mockedSceNotaRecebimentoDAO.obterOriginal(Mockito.anyInt())).thenReturn(new SceNotaRecebimento());
			
			systemUnderTest.estornarNotaRecebimento(sceReqMateriais.getSeq(), NOME_MICROCOMPUTADOR);
			
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
	}

	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
	}

}
