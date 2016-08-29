package br.gov.mec.aghu.exames.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author amalmeida
 *
 */
public class AelExameConselhosProfsRNTest extends AGHUBaseUnitTest<AelExameConselhoProfsRN>{

	private AelExameConselhoProfs exameConselhosProfs;
	private RapConselhosProfissionais conselhosProfissional;

	@Before
	public void doBeforeEachTestCase() {
		exameConselhosProfs =  new AelExameConselhoProfs();
		conselhosProfissional = new RapConselhosProfissionais();
	}

	@Test
	public void validarConselhoAtivoTest() {

		try {
			
			conselhosProfissional.setIndSituacao(DominioSituacao.I);
			exameConselhosProfs.setConselhosProfissionais(conselhosProfissional);
			
			systemUnderTest.validarConselho(exameConselhosProfs);
			
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_00438", e.getMessage());

		}


	}

}
