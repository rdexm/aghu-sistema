package br.gov.mec.aghu.compras.business;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoFnRamoComerClasDAO;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.model.ScoFnRamoComerClasId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class CadastrarMateriaisRamoComercialFornecedorRNTest extends AGHUBaseUnitTest<CadastrarMateriaisRamoComercialFornecedorRN>{

	@Mock
	private ScoFnRamoComerClasDAO mockedScoFnRamoComerClasDAO;

	private ScoFnRamoComerClas getScoFnRamoComerClas(){
		ScoFnRamoComerClas scoFnRamoComerClas = new ScoFnRamoComerClas();
		ScoFnRamoComerClasId scoFnRamoComerClasId = new ScoFnRamoComerClasId();
		
		scoFnRamoComerClasId.setCn5Numero(130000000000l);
		scoFnRamoComerClasId.setFrmFrnNumero(355);
		scoFnRamoComerClasId.setFrmRcmCodigo(Short.valueOf("2"));

		scoFnRamoComerClas.setId(scoFnRamoComerClasId);
		return scoFnRamoComerClas;
	}
	
	@Test(expected = ApplicationBusinessException.class)
	public void inserirScoFnRamoComerClas1() throws Exception{		
		
		Mockito.when(mockedScoFnRamoComerClasDAO.obterPorChavePrimaria(Mockito.any(ScoFnRamoComerClasId.class))).thenReturn(this.getScoFnRamoComerClas());
		systemUnderTest.inserirScoFnRamoComerClas(this.getScoFnRamoComerClas());
	}
	
	@Test
	public void inserirScoFnRamoComerClas2() throws Exception{
		
		Mockito.when(mockedScoFnRamoComerClasDAO.obterPorChavePrimaria(Mockito.any(ScoFnRamoComerClasId.class))).thenReturn(null);
		systemUnderTest.inserirScoFnRamoComerClas(getScoFnRamoComerClas());
	}
}
