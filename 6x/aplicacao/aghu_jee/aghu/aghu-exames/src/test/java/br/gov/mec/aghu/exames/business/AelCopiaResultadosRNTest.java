package br.gov.mec.aghu.exames.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelCopiaResultadosDAO;
import br.gov.mec.aghu.model.AelCopiaResultados;
import br.gov.mec.aghu.model.AelCopiaResultadosId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelCopiaResultadosRNTest extends AGHUBaseUnitTest<AelCopiaResultadosRN>{
	
	@Mock
	private AelCopiaResultadosDAO mockedCopiaResultadosDAO;
	
	@Test
	public void verificarOrigemAtendimentoTest001() {
		
		final AelCopiaResultados aelCopiaResultados = new AelCopiaResultados();
		final AelCopiaResultadosId id = new AelCopiaResultadosId();
		id.setCnvCodigo(Short.valueOf("1"));
		id.setEmaExaSigla("teste");
		id.setEmaManSeq(1);
		id.setOrigemAtendimento(DominioOrigemAtendimento.T);
		aelCopiaResultados.setId(id);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		AelExamesMaterialAnaliseId idema = new AelExamesMaterialAnaliseId();
		idema.setExaSigla("teste");
		idema.setManSeq(1);
		examesMaterialAnalise.setId(idema);
		aelCopiaResultados.setExamesMaterialAnalise(examesMaterialAnalise);
		
		Mockito.when(mockedCopiaResultadosDAO.existeItem(Mockito.anyShort(), Mockito.anyString(), Mockito.anyInt())).thenReturn(true);
		
    	
    	try {
    		
			systemUnderTest.verificarOrigemAtendimento(aelCopiaResultados);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("AEL_01057", atual.getMessage());
			
		}
		
	}
	
	@Test
	public void verificarOrigemAtendimentoTest002() {
		
		final AelCopiaResultados aelCopiaResultados = new AelCopiaResultados();
		final AelCopiaResultadosId id = new AelCopiaResultadosId();
		id.setCnvCodigo(Short.valueOf("1"));
		id.setEmaExaSigla("teste");
		id.setEmaManSeq(1);
		id.setOrigemAtendimento(DominioOrigemAtendimento.I);
		aelCopiaResultados.setId(id);
		
		AelExamesMaterialAnalise examesMaterialAnalise = new AelExamesMaterialAnalise();
		AelExamesMaterialAnaliseId idema = new AelExamesMaterialAnaliseId();
		idema.setExaSigla("teste");
		idema.setManSeq(1);
		examesMaterialAnalise.setId(idema);
		aelCopiaResultados.setExamesMaterialAnalise(examesMaterialAnalise);
		
		Mockito.when(mockedCopiaResultadosDAO.existeItemOrigemAtendimentoTodos(Mockito.anyShort(), Mockito.anyString(), Mockito.anyInt())).thenReturn(true);

    	try {
    		
			systemUnderTest.verificarOrigemAtendimento(aelCopiaResultados);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("AEL_01058", atual.getMessage());
			
		}
		
	}
	
	@Test
	public void executarRestricoesTest001() {
		
		final AelCopiaResultados aelCopiaResultados = new AelCopiaResultados();
    	
    	try {
    		
			systemUnderTest.executarRestricoes(aelCopiaResultados);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("AEL_00251", atual.getMessage());
			
		}
		
	}
	
	@Test
	public void executarRestricoesTest002() {
		
		final AelCopiaResultados aelCopiaResultados = new AelCopiaResultados();
		aelCopiaResultados.setNumero(Byte.valueOf("-1"));
    	
    	try {
    		
			systemUnderTest.executarRestricoes(aelCopiaResultados);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("AEL_00251", atual.getMessage());
			
		}
		
	}

}
