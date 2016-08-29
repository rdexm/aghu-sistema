package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaMotivoRNTest extends AGHUBaseUnitTest<ManterAltaMotivoRN>{

	@Mock
	private ManterAltaSumarioRN mockedAltaSumarioRN;
	@Mock
	private MpmMotivoAltaMedicaDAO mockedMotivoAltaMedicaDAO;
	
	/**
	 * Deve retornar exceção MPM_02681.
	 */
	@Test
	public void verificarAtualizacao001Test() {
					
		try {
		
			systemUnderTest.verificarAtualizacao("teste1", "teste2", Short.valueOf("1"), Short.valueOf("1"));
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02681", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02682.
	 */
	@Test
	public void verificarAtualizacao002Test() {
					
		try {
		
			systemUnderTest.verificarAtualizacao("teste1", "teste1", Short.valueOf("2"), Short.valueOf("1"));
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02682", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02682.
	 */
	@Test
	public void verificarAtualizacao003Test() {
					
		try {
		
			systemUnderTest.verificarAtualizacao(null, null, Short.valueOf("2"), Short.valueOf("1"));
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02682", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Não deve retornar exceção.
	 *  
	 * @throws NumberFormatException 
	 */
	@Test
	public void verificarAtualizacao004Test() throws NumberFormatException, BaseException {
					
		systemUnderTest.verificarAtualizacao(null, null, Short.valueOf("2"), Short.valueOf("2"));
		
	}
	
	/**
	 * Deve retornar exceção MPM_02683.
	 */
	@Test
	public void verificarCompl001Test() {
		
		final MpmMotivoAltaMedica motivoAltaMedica = null;
		
		Short novoManSeq = Short.valueOf("1");
		Short oldManSeq = Short.valueOf("1");
		String novoComplMotivo = "teste";
		String oldComplMotivo = "teste";
		
		Mockito.when(mockedMotivoAltaMedicaDAO.obterMotivoAltaMedicaPeloId(Mockito.anyShort())).thenReturn(motivoAltaMedica);
		
		try {
		
			systemUnderTest.verificarCompl(novoManSeq, oldManSeq, novoComplMotivo, oldComplMotivo);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02683", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02696.
	 */
	@Test
	public void verificarCompl002Test() {
		
		final MpmMotivoAltaMedica motivoAltaMedica = new MpmMotivoAltaMedica();
		motivoAltaMedica.setIndExigeComplemento(Boolean.TRUE);
		
		Short novoManSeq = Short.valueOf("1");
		Short oldManSeq = Short.valueOf("1");
		String novoComplMotivo = null;
		String oldComplMotivo = "teste";
		
		Mockito.when(mockedMotivoAltaMedicaDAO.obterMotivoAltaMedicaPeloId(Mockito.anyShort())).thenReturn(motivoAltaMedica);
		
		try {
		
			systemUnderTest.verificarCompl(novoManSeq, oldManSeq, novoComplMotivo, oldComplMotivo);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02696", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02686.
	 */
	@Test
	public void verificarCompl003Test() {
		
		final MpmMotivoAltaMedica motivoAltaMedica = new MpmMotivoAltaMedica();
		motivoAltaMedica.setIndExigeComplemento(Boolean.FALSE);
		
		Short novoManSeq = Short.valueOf("1");
		Short oldManSeq = Short.valueOf("1");
		String novoComplMotivo = "teste";
		String oldComplMotivo = "teste";
		
		Mockito.when(mockedMotivoAltaMedicaDAO.obterMotivoAltaMedicaPeloId(Mockito.anyShort())).thenReturn(motivoAltaMedica);

		try {
		
			systemUnderTest.verificarCompl(novoManSeq, oldManSeq, novoComplMotivo, oldComplMotivo);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (BaseException atual) {
			
			assertEquals("MPM_02686", atual.getMessage());
			
		}
		
	}	
	
	

}
