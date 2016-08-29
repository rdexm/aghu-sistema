package br.gov.mec.aghu.paciente.prontuarioonline.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ImprimeAltaAmbulatorialPolRNTest extends AGHUBaseUnitTest<ImprimeAltaAmbulatorialPolRN> {

	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IProntuarioOnlineFacade mockedProntuarioOnlineFacade;
	@Mock
	private RelExameFisicoRecemNascidoPOLON mockedRelExameFisicoRecemNascidoPOLON;

	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */

	@Test
	public void buscaAssinaturaMedicoCrmTest() throws ApplicationBusinessException {
		final Integer matricula = 123456;
		final Short vinCodigo = 132;
		esperarBuscaConselhoProfissionalServidorVO();
		Assert.assertNotNull(systemUnderTest.buscaAssinaturaMedicoCrm(matricula, vinCodigo));
	}

	// Expectations para prontuarioOnlineFacade
	private void esperarBuscaConselhoProfissionalServidorVO() throws ApplicationBusinessException {
		Mockito.when(mockedRelExameFisicoRecemNascidoPOLON.formataNomeProf(Mockito.anyInt(), Mockito.anyShort())).thenReturn(
				"Maria CRM 12389");
	}

	@Test(expected = ApplicationBusinessException.class)
	public void getEnderecoCompletoTestException() throws ApplicationBusinessException {
		esperarGetEnderecoCompletoComExcecao();
		systemUnderTest.getEnderecoCompleto();
	}

	// Expectations para parametroFacade
	private void esperarGetEnderecoCompletoComExcecao() throws ApplicationBusinessException {
		Mockito.when(mockedParametroFacade.obterAghParametro(Mockito.any(AghuParametrosEnum.class))).thenThrow(
				new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE));
	}

	@Test()
	public void getEnderecoCompletoTest() throws ApplicationBusinessException {
		esperarGetEnderecoCompleto();
		Assert.assertNotNull(systemUnderTest.getEnderecoCompleto());
	}

	// Expectations para parametroFacade
	private void esperarGetEnderecoCompleto() throws ApplicationBusinessException {
		AghParametros parametro = new AghParametros();
		parametro.setVlrTexto("");
		Mockito.when(mockedParametroFacade.obterAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
	}

}
