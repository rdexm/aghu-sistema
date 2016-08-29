package br.gov.mec.aghu.exames.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Ignore
public class AelExameConselhoProfsRNTest {
	
	private AelExameConselhoProfsRN systemUnderTest;
	
	@Before
	public void doBeforeEachTestCase() {
		systemUnderTest = new AelExameConselhoProfsRN() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8923464739326523798L;

		};
	}
	
	@Test
	public void validarConselhoTest001() {
		
		final AelExameConselhoProfs aelExameConselhoProfs = new AelExameConselhoProfs();
		
		try {
			
			systemUnderTest.validarConselho(aelExameConselhoProfs);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("AEL_00437", e.getMessage());
			
		}
		
	}
	
	@Test
	public void validarConselhoTest002() {
		
		final AelExameConselhoProfs aelExameConselhoProfs = new AelExameConselhoProfs();
		RapConselhosProfissionais conselhosProfissionais = new RapConselhosProfissionais();
		conselhosProfissionais.setIndSituacao(DominioSituacao.I);
		aelExameConselhoProfs.setConselhosProfissionais(conselhosProfissionais);
		
		try {
			
			systemUnderTest.validarConselho(aelExameConselhoProfs);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException e) {
			
			assertEquals("AEL_00438", e.getMessage());
			
		}
		
	}
}
