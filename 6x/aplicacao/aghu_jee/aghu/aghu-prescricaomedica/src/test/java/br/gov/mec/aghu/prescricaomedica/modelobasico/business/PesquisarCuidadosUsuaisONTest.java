package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PesquisarCuidadosUsuaisONTest extends AGHUBaseUnitTest<PesquisarCuidadoUsualON>{

	/**
	 * Classe responsável pelos testes unitários da classe
	 * PesquisarCuidadoUsualON.<br>
	 * 
	 * @author mgoulart
	 * 
	 */

	@Test
	public void remover() {

		MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();

		try {
			systemUnderTest.excluir(cuidadoUsual.getSeq());
		} catch (ApplicationBusinessException e) {

			assertEquals("MENSAGEM_CUIDADO_USUAL_PARAMETRO_OBRIGATORIO", e
					.getMessage());
		}
	}

}
