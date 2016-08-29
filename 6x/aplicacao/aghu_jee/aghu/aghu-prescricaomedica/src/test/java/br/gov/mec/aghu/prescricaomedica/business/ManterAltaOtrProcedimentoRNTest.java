package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author bsoliveira - 27/10/2010
 * 
 */
public class ManterAltaOtrProcedimentoRNTest extends AGHUBaseUnitTest<ManterAltaOtrProcedimentoRN>{

	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;


	/**
	 * Teste exceção. Gera mensagem MPM_02631.
	 * 
	 */
	@Test
	public void verificarComplTest001() throws BaseException {

		try {

			systemUnderTest.verificarCompl("TESTE", Boolean.TRUE, null, null,null);
			fail("Deveria ter ocorrido uma exceção do tipo BaseException!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02631", e.getMessage());

		}

	}

	/**
	 * Teste exceção. Gera mensagem MPM_02632.
	 * 
	 */
	@Test
	public void verificarComplTest002() throws BaseException {


		try{

			systemUnderTest.verificarCompl(null, Boolean.TRUE, null, null, null);
			fail("Deveria ter ocorrido uma exceção do tipo BaseException!!!");

		} catch (BaseException e) {

			assertEquals("MPM_02632", e.getMessage());

		}

	}

}
