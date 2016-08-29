package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.junit.Test;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaPedidoExameRNTest extends AGHUBaseUnitTest<ManterAltaPedidoExameRN>{

	@Test
	public void verificarDtHrConsultaTest() {
		
		Calendar novoDthrConsulta = Calendar.getInstance();
		novoDthrConsulta.set(Calendar.DAY_OF_MONTH, 27);
		novoDthrConsulta.set(Calendar.MONTH, Calendar.JUNE);
		novoDthrConsulta.set(Calendar.YEAR, 1985);
		
		try {
			
			systemUnderTest.verificarDtHrConsulta(novoDthrConsulta.getTime());
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02890", atual.getMessage());
			
		}
		
	}
	
	@Test
	public void verificarEquipeTest001() {
		
		Integer novoEqpSeq = null;
		String novoDescEquipe = "teste";
		
		try {
			
			systemUnderTest.verificarEquipe(novoEqpSeq, novoDescEquipe);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("MPM_02674", e.getMessage());
			
		}
		
	}
	
	@Test
	public void verificarEquipeTest002() {
		
		Integer novoEqpSeq = 10;
		String novoDescEquipe = null;
		
		try {
			
			systemUnderTest.verificarEquipe(novoEqpSeq, novoDescEquipe);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("MPM_02675", e.getMessage());
			
		}
		
	}

}
