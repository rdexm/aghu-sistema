package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lalegre
 *
 */
public class ManterProvaExameRNTest extends AGHUBaseUnitTest<ManterProvaExameRN>{


	@Test
	public void verificarProva() {

		final AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		final AelExamesMaterialAnalise examesMaterialAnaliseEhprova = new AelExamesMaterialAnalise();
		AelExamesMaterialAnaliseId idEhProva = new AelExamesMaterialAnaliseId();

		id.setExaSigla("teste");
		id.setManSeq(1);
		idEhProva.setExaSigla("teste");
		idEhProva.setManSeq(1);

		examesMaterialAnalise.setId(id);
		examesMaterialAnaliseEhprova.setId(idEhProva);

		try {

			systemUnderTest.verificarProva(examesMaterialAnalise, examesMaterialAnaliseEhprova);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_00428", e.getMessage());

		}


	}

	@Test
	public void validarServidorTest() {

		RapServidores servidor = new RapServidores();
		RapServidores novoServidor = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		Short vinCodigo = 111;
		id.setMatricula(1);
		id.setVinCodigo(vinCodigo);
		servidor.setId(id);

		RapServidoresId id2 = new RapServidoresId();
		id2.setMatricula(2);
		id2.setVinCodigo(vinCodigo);
		novoServidor.setId(id2);

		try {

			systemUnderTest.validarServidor(servidor, novoServidor);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException e) {

			assertEquals("AEL_00369", e.getMessage());

		}

	}

}
