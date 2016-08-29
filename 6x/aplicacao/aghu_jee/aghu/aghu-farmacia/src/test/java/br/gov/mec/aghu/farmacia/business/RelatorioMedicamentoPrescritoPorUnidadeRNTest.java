package br.gov.mec.aghu.farmacia.business;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.sicon.business.ISiconFacade;

public class RelatorioMedicamentoPrescritoPorUnidadeRNTest extends AGHUBaseUnitTest<RelatorioMedicamentoPrescritoPorUnidadeRN>{

	//Daos, ONs e entidades a serem mockadas
	@Mock
	private ISiconFacade mockedSiconFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IEstoqueFacade mockedEstoqueFacade;

	@Test
	public void obterCustoMedioPonderado() throws ApplicationBusinessException {
		AghParametros parametro = new AghParametros(new Date());
		AghParametros dataCompetencia = new AghParametros(new Date());
		parametro.setVlrNumerico(BigDecimal.ONE);
		
		Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)).thenReturn(parametro);
		Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_COMPETENCIA)).thenReturn(dataCompetencia);
		try {
			BigDecimal resultadoObtido = systemUnderTest.obterCustoMedioPonderado(new ScoMaterial(), null);
			Assert.assertNotNull(resultadoObtido);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deveria ter lançado exceção");
		}
		
 	}

}