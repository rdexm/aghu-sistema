package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lalegre
 *
 */
public class ManterHorarioColetaExameRNTest extends AGHUBaseUnitTest<ManterHorarioColetaExameRN>{

	@Test
	public void validarTest() {

		// cria o formatador
		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
		
		try {
			Date horaMaior = formatador.parse("15:00");
			Date horaMenor = formatador.parse("14:20");
			AelExameHorarioColeta horarioExame = new AelExameHorarioColeta();
			horarioExame.setHorarioInicial(horaMaior);
			horarioExame.setHorarioFinal(horaMenor);
			systemUnderTest.validaHorarioColeta(horarioExame);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_02679", e.getMessage());

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			assertEquals(e1.getCause(),e1.getMessage());
		}


	}

}
