package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class TipoFrequenciaAprazamentoRNTest extends AGHUBaseUnitTest<TipoFrequenciaAprazamentoRN>{
	
	@Mock
	private IParametroFacade mockedParametroFacade;
	
	@Test(expected=ApplicationBusinessException.class)
	public void preDeleteTipoFrequenciaAprazamentoExceptionTest() throws ApplicationBusinessException {
		MpmTipoFrequenciaAprazamento entity = new MpmTipoFrequenciaAprazamento();
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DATE, -15);
		entity.setCriadoEm(data.getTime());

		AghParametros aghp = new AghParametros();
		aghp.setVlrNumerico(BigDecimal.valueOf(10));				
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM)).thenReturn(aghp);
		
		systemUnderTest.preDeleteTipoFrequenciaAprazamento(entity);
	}
	
	
	@Test(expected=ApplicationBusinessException.class)
	public void verificarMatriculaExceptionTest() throws ApplicationBusinessException {
		MpmTipoFrequenciaAprazamento entity = new MpmTipoFrequenciaAprazamento();
		systemUnderTest.verificaMatricula(entity);
	}
	
}
