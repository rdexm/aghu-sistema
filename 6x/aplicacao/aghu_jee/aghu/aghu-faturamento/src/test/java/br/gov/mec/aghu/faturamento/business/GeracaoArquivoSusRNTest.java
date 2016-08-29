package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GeracaoArquivoSusRNTest extends AGHUBaseUnitTest<GeracaoArquivoSusRN>{

	final Log log = LogFactory.getLog(this.getClass());
	
	@Mock
	BuscaCpfCboResponsavelPorIchRN mockedCpfCboRn;
	@Mock
	private IParametroFacade mockedParametroFacade;

	@Before
	public void inciar() throws BaseException {
		AghParametros parametro = new AghParametros();
		parametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
	}
	
	@Test
	public void testObterRegistroAihComum() {

		RegistroAihComum result = null;
		FatEspelhoAih eai = null;
		String orgLocRec = null;
		BigInteger cnes = null;
		Integer codMunicipio = null;

		//setup
		eai = new FatEspelhoAih();
		eai.setContaHospitalar(new FatContasHospitalares());
		orgLocRec = "org";
		cnes = BigInteger.ONE;
		codMunicipio = Integer.valueOf(1);
		//assert
		try {
			result = systemUnderTest.obterRegistroAihComum(eai, eai.getTahSeq(), orgLocRec, cnes, codMunicipio);
			Assert.assertNotNull(result);
			Assert.assertEquals(result.getCnesHosp(), cnes);
			Assert.assertEquals(result.getOrgEmisAih(), orgLocRec);
		} catch (Exception e) {
		}
	}
}
