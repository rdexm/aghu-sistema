package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class IncluiNotasPOLRNTest extends AGHUBaseUnitTest<IncluiNotasPOLRN>{

	@Mock
	private IParametroFacade mockedParametroFacade;

	
	@Test()
	public void mamcGetProcNaPrjTest() throws ApplicationBusinessException {
		esperarObterAghParametro();
		Assert.assertNotNull(systemUnderTest.mamcGetProcNaPrj());
	}
	
	@Test()
	public void mamcGetProcDiagTest() throws ApplicationBusinessException {
		esperarObterAghParametro();
		Assert.assertNotNull(systemUnderTest.mamcGetProcDiag());
	}
	
	//Expectations para parametroFacade
	private void esperarObterAghParametro() throws ApplicationBusinessException {
		Mockito.when(mockedParametroFacade.obterAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(new AghParametros(new BigDecimal("94")));
	}
	
	
	
}
