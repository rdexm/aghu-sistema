package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmAltaConsultoria;
import br.gov.mec.aghu.model.MpmAltaConsultoriaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaConsultoriaRNTest extends AGHUBaseUnitTest<ManterAltaConsultoriaRN>{

	/**
	 * Deve retornae exceção MPM_02635.
	 */
	@Test
	public void verificarAtualizacao001Test() {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		AghEspecialidades especialidades = new AghEspecialidades();
		
		novoAltaConsultoria.setIndCarga(Boolean.FALSE);
		novoAltaConsultoria.setDescConsultoria("teste1");
		novoAltaConsultoria.setAghEspecialidade(especialidades);
		
		antigoAltaConsultoria.setDescConsultoria("teste2");
		antigoAltaConsultoria.setAghEspecialidade(especialidades);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaConsultoria, antigoAltaConsultoria);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02635", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornae exceção MPM_02653.
	 */
	@Test
	public void verificarAtualizacao002Test() {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		AghEspecialidades especialidades1 = new AghEspecialidades();
		especialidades1.setSeq(Short.valueOf("1"));
		
		AghEspecialidades especialidades2 = new AghEspecialidades();
		especialidades2.setSeq(Short.valueOf("2"));
		
		novoAltaConsultoria.setIndCarga(Boolean.FALSE);
		novoAltaConsultoria.setDescConsultoria("teste1");
		novoAltaConsultoria.setAghEspecialidade(especialidades1);
		
		antigoAltaConsultoria.setDescConsultoria("teste1");
		antigoAltaConsultoria.setAghEspecialidade(especialidades2);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaConsultoria, antigoAltaConsultoria);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02653", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornae exceção MPM_02636.
	 */
	@Test
	public void verificarAtualizacao003Test() {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		AghEspecialidades especialidades1 = new AghEspecialidades();
		AghEspecialidades especialidades2 = new AghEspecialidades();
		
		novoAltaConsultoria.setIndCarga(Boolean.TRUE);
		novoAltaConsultoria.setDescConsultoria("teste1");
		novoAltaConsultoria.setAghEspecialidade(especialidades1);
		
		antigoAltaConsultoria.setDescConsultoria("teste1");
		antigoAltaConsultoria.setAghEspecialidade(especialidades2);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaConsultoria, antigoAltaConsultoria);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02636", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornae exceção MPM_02637.
	 */
	@Test
	public void verificarAtualizacao004Test() {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		AghEspecialidades especialidades1 = new AghEspecialidades();
		AghEspecialidades especialidades2 = new AghEspecialidades();
		
		novoAltaConsultoria.setIndCarga(Boolean.FALSE);
		novoAltaConsultoria.setDescConsultoria("teste1");
		novoAltaConsultoria.setAghEspecialidade(especialidades1);
		novoAltaConsultoria.setSolicitacaoConsultoria(new MpmSolicitacaoConsultoria());
		
		antigoAltaConsultoria.setDescConsultoria("teste1");
		antigoAltaConsultoria.setAghEspecialidade(especialidades2);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaConsultoria, antigoAltaConsultoria);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02637", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar uma exceção.
	 *  
	 */
	@Test(expected=BaseException.class)
	public void verificarPermissaoAlteracao001Test() throws ApplicationBusinessException {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		novoAltaConsultoria.setIndCarga(Boolean.TRUE);
		
		MpmAltaConsultoriaId novoAltaConsultoriaId = new MpmAltaConsultoriaId();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoriaId antigoAltaConsultoriaId = new MpmAltaConsultoriaId();
		
		novoAltaConsultoriaId.setAsuApaAtdSeq(Integer.valueOf("1"));
		antigoAltaConsultoriaId.setAsuApaAtdSeq(Integer.valueOf("2"));
		novoAltaConsultoria.setId(novoAltaConsultoriaId);
		antigoAltaConsultoria.setId(antigoAltaConsultoriaId);
		
		AghEspecialidades especialidade = new AghEspecialidades();
		novoAltaConsultoria.setAghEspecialidade(especialidade);
		antigoAltaConsultoria.setAghEspecialidade(especialidade);
					
		systemUnderTest.verificarPermissaoAlteracao(novoAltaConsultoria, antigoAltaConsultoria);
		
	}
	
	/**
	 * Deve retornar uma exceção.
	 *  
	 */
	@Test(expected=BaseException.class)
	public void verificarPermissaoAlteracao002Test() throws ApplicationBusinessException {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		novoAltaConsultoria.setIndCarga(Boolean.TRUE);
		
		MpmAltaConsultoriaId novoAltaConsultoriaId = new MpmAltaConsultoriaId();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoriaId antigoAltaConsultoriaId = new MpmAltaConsultoriaId();
		
		novoAltaConsultoriaId.setAsuApaSeq(Integer.valueOf("1"));
		antigoAltaConsultoriaId.setAsuApaSeq(Integer.valueOf("2"));
		novoAltaConsultoria.setId(novoAltaConsultoriaId);
		antigoAltaConsultoria.setId(antigoAltaConsultoriaId);
		
		AghEspecialidades especialidade = new AghEspecialidades();
		novoAltaConsultoria.setAghEspecialidade(especialidade);
		antigoAltaConsultoria.setAghEspecialidade(especialidade);
					
		systemUnderTest.verificarPermissaoAlteracao(novoAltaConsultoria, antigoAltaConsultoria);
		
	}
	
	/**
	 * Deve retornar uma exceção.
	 *  
	 */
	@Test(expected=BaseException.class)
	public void verificarPermissaoAlteracao003Test() throws ApplicationBusinessException {
		
		MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
		novoAltaConsultoria.setIndCarga(Boolean.TRUE);
		
		MpmAltaConsultoriaId novoAltaConsultoriaId = new MpmAltaConsultoriaId();
		MpmAltaConsultoria antigoAltaConsultoria = new MpmAltaConsultoria();
		MpmAltaConsultoriaId antigoAltaConsultoriaId = new MpmAltaConsultoriaId();
		
		novoAltaConsultoriaId.setAsuSeqp(Short.valueOf("1"));
		antigoAltaConsultoriaId.setAsuSeqp(Short.valueOf("2"));
		novoAltaConsultoria.setId(novoAltaConsultoriaId);
		antigoAltaConsultoria.setId(antigoAltaConsultoriaId);
		
		AghEspecialidades especialidade = new AghEspecialidades();
		novoAltaConsultoria.setAghEspecialidade(especialidade);
		antigoAltaConsultoria.setAghEspecialidade(especialidade);
					
		systemUnderTest.verificarPermissaoAlteracao(novoAltaConsultoria, antigoAltaConsultoria);
		
	}

}
