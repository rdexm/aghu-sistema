package br.gov.mec.aghu.internacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.internacao.business.EscalaProfissionaisInternacaoON.EscalaProfissionaisInternacaoONExceptionCode;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinEscalasProfissionalIntId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EscalaProfissionaisInternacaoONTest extends AGHUBaseUnitTest<EscalaProfissionaisInternacaoON>{
	
	@Mock
	private EscalaProfissionaisInternacaoRN mockedEscalaProfissionaisInternacaoRN;
	
	@org.junit.Test
	public void testValidarDataFim() {
		try {
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			AinEscalasProfissionalInt escala = new AinEscalasProfissionalInt();
			escala.setDtFim(cal.getTime());
				
			systemUnderTest.validarDataFim(escala);
		} catch (BaseException e) {
			fail("Exceção não esperada: " + e.getCode());
		}
	}
	
	@org.junit.Test
	public void testValidarDataFimException() {
		try {
			final Calendar cal = Calendar.getInstance();
			final Calendar cal2 = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, - 2);
			cal2.add(Calendar.DAY_OF_MONTH, - 1);
			
			AinEscalasProfissionalInt escala = new AinEscalasProfissionalInt();
			escala.setDtFim(cal.getTime());

			AinEscalasProfissionalInt escala1 = new AinEscalasProfissionalInt();
			escala1.setDtFim(cal2.getTime());
			Mockito.when(mockedEscalaProfissionaisInternacaoRN.obterEscala(Mockito.any(AinEscalasProfissionalIntId.class))).thenReturn(escala1);

			
			systemUnderTest.validarDataFim(escala);
			fail("Exceção esperada não lançada");
		} catch (BaseException e) {
			assertEquals(EscalaProfissionaisInternacaoONExceptionCode.MENSAGEM_PERIODO_ENCERRADO_ALTERACAO_ESCALA, e.getCode());
		}
	}

}