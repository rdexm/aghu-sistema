package br.gov.mec.aghu.sicon.business;

import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.sicon.business.ManterConvenioItemContratoON.ManterConvenioItemContratoONExceptionCode;
import br.gov.mec.aghu.sicon.dao.ScoConvItensContratoDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterConvenioItemContratoONTest extends AGHUBaseUnitTest<ManterConvenioItemContratoON>{

	@Mock
	private ScoConvItensContratoDAO mockedScoConvItensContratoDAO;

	@Test
	public void gravarParamNulo() throws BaseException {
		
		try {
			systemUnderTest.gravar(null, null, null);
		} catch (BaseException e) {
			Assert.assertEquals(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_PARAM_OBRIG,
					e.getCode());
		}
	}
	
	@Test
	public void validarValoresMaior() throws BaseException {
		
		BigDecimal somatorio = new BigDecimal(2);
		BigDecimal valorTotal = BigDecimal.ONE;
		
		try {
			systemUnderTest.validarValorTotal(somatorio, valorTotal);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			Assert.assertEquals(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_SOMA_VALOR_CONVENIOS_MAIOR_QUE_VALOR_ITEM,
					e.getCode());

		}
	}
	
	@Test
	public void validarValoresMenor() throws BaseException {
		
		BigDecimal somatorio = BigDecimal.ONE;
		BigDecimal valorTotal = new BigDecimal(2);
		
		try {
			systemUnderTest.validarValorTotal(somatorio, valorTotal);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			Assert.assertEquals(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_SOMA_VALOR_CONVENIOS_MENOR_QUE_VALOR_ITEM,
					e.getCode());

		}
	}
	
	@Test
	public void validarValoresIguais() throws BaseException {
		
		BigDecimal somatorio = BigDecimal.ONE;
		BigDecimal valorTotal = BigDecimal.ONE;
		
		try {
			systemUnderTest.validarValorTotal(somatorio, valorTotal);

		} catch (BaseException e) {
			fail("Ocorreu uma exceção!!!");

		}
	}
}
