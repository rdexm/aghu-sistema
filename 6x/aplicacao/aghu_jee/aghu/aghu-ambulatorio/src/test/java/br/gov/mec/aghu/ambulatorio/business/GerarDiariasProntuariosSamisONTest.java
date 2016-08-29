package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacPeriodoReferenciaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.model.AacPeriodoReferencia;
import br.gov.mec.aghu.model.AghParametros;

public class GerarDiariasProntuariosSamisONTest  extends AGHUBaseUnitTest<GerarDiariasProntuariosSamisON> {

	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private AacPeriodoReferenciaDAO mockedPeriodoReferenciaDao;
	
	@Test
	public void fimDiariaTest() {
		AghParametros parametro = new AghParametros();
		parametro.setVlrData(new Date());
		List<AacPeriodoReferencia> refencias = new ArrayList<AacPeriodoReferencia>();
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
			Mockito.when(mockedPeriodoReferenciaDao.pesquisarPeriodoReferencia()).thenReturn(refencias);
			refencias.add(new AacPeriodoReferencia());
			systemUnderTest.fimDiaria(new Date());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Fim diaria error");
		} 
	}
}
