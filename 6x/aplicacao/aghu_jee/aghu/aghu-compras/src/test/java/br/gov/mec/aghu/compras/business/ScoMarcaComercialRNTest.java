package br.gov.mec.aghu.compras.business;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class ScoMarcaComercialRNTest extends AGHUBaseUnitTest<ScoMarcaComercialRN>{
	
	@Mock
	private IComprasFacade mockedComprasFacade;
	
	@Test
	public void rnMompVerMcm() throws Exception{
		final ScoMarcaComercial marcaComercial = new ScoMarcaComercial();
		marcaComercial.setIndSituacao(DominioSituacao.I);
		
		Mockito.when(mockedComprasFacade.obterScoMarcaComercialPorChavePrimaria(Mockito.anyInt())).thenReturn(marcaComercial);
		
		systemUnderTest.insereScoMarcaComercial(marcaComercial);
	}
	
	
}
