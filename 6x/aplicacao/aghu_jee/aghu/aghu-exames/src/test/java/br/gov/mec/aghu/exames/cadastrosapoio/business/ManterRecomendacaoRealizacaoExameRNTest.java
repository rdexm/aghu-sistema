package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lalegre
 *
 */
public class ManterRecomendacaoRealizacaoExameRNTest extends AGHUBaseUnitTest<ManterRecomendacaoRealizacaoExameRN>{

//	private Mockery mockingContext;
	
	
	@Test
	public void validarUpdateTest() {
		
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
			
			systemUnderTest.validarUpdate(servidor, novoServidor);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("AEL_00369", e.getMessage());
			
		}
		
	}
	
}
