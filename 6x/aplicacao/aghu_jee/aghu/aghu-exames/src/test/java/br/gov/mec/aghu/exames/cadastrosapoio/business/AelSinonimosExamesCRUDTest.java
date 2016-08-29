package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lalegre
 *
 */
public class AelSinonimosExamesCRUDTest extends AGHUBaseUnitTest<AelSinonimosExamesCRUD>{

	@Test
	public void validarSinonimoSeqpTest() {
		
		Short seqp = 1;
		
		try {
			
			systemUnderTest.validarSinonimoSeqp(seqp);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("AEL_00357", e.getMessage());
			
		}
		
	}
	
	@Test
	public void validarAlteracaoTest() {
		
		final AelSinonimoExame aelSinonimoExame = new AelSinonimoExame();
		final AelSinonimoExame antigoSinonimoExame = new AelSinonimoExame();
		
		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		Short vinCodigo = 111;
		id.setMatricula(1);
		id.setVinCodigo(vinCodigo);
		servidor.setId(id);
		
		Date d = new Date();

		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(d);
		calendarioTemp.add(Calendar.DATE, Integer.valueOf(10));
		
		aelSinonimoExame.setCriadoEm(d);
		aelSinonimoExame.setRapServidor(servidor);
		antigoSinonimoExame.setRapServidor(servidor);
		antigoSinonimoExame.setCriadoEm(calendarioTemp.getTime());
		
		try {
			
			systemUnderTest.validarAlteracao(aelSinonimoExame, antigoSinonimoExame);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("AEL_00369", e.getMessage());
			
		}
		
	}
	
}
