package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesDependentesId;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author fwinck
 *
 */
public class ManterExamesMaterialDependenteRNTest extends AGHUBaseUnitTest<ManterExamesMaterialDependenteRN>{

	@Mock
	private VAelExameMatAnaliseDAO mockedVAelExameMatAnaliseDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	
	private AelExamesDependentes exaDependente;
	
	@Before
	public void doBeforeEachTestCase() {
		exaDependente = new AelExamesDependentes();
		
		final VAelExameMatAnalise vAelExameMatAnalise = new VAelExameMatAnalise();
		vAelExameMatAnalise.setIndDependente("N");
		
		Mockito.when(mockedVAelExameMatAnaliseDAO.buscarVAelExameMatAnalisePelaSiglaESeq(Mockito.anyString(), Mockito.anyInt())).thenReturn(vAelExameMatAnalise);

	}
	
	/**
	 * Método que pré-popula os objetos de teste com valores iguais.
	 */
	private void popularExameDependente() {
		AelExamesDependentesId id = new AelExamesDependentesId();
		id.setEmaExaSigla("MUS");
		id.setEmaManSeq(67);
		id.setEmaExaSiglaEhDependente("PECRO");
		id.setEmaManSeqEhDependente(170);
		
		exaDependente.setId(id);
		exaDependente.setIdAux(id);

	}

	@Test
	public void validaEhDependente(){
		try {
			popularExameDependente();
			systemUnderTest.validaEhDependente(this.exaDependente);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (BaseException e) {
			assertEquals("AEL_00834", e.getMessage());
		}
	}

	@Test
	public void validaExameDependente(){
		try {
			popularExameDependente();
			systemUnderTest.validaExameDependente(this.exaDependente);
		} catch (BaseException e) {
			assertEquals("AEL_00425", e.getMessage());
		}
	}
}