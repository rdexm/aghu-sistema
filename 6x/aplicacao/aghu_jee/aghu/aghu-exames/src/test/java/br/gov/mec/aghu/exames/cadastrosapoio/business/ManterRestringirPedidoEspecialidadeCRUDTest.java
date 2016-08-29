package br.gov.mec.aghu.exames.cadastrosapoio.business;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author fwinck
 *
 */
public class ManterRestringirPedidoEspecialidadeCRUDTest extends AGHUBaseUnitTest<ManterRestringirPedidoEspecialidadeCRUD>{

	
	private AelExamesEspecialidade exameEspecialidade;
	
	@Before
	public void doBeforeEachTestCase() {
		
		exameEspecialidade = new AelExamesEspecialidade();

	}
	
	/**
	 * Método que pré-popula os objetos de teste com valores iguais.
	 */
	private void popularExameDependente() {
		
		AghEspecialidades especialidades = new AghEspecialidades();
		especialidades.setIndSituacao(DominioSituacao.A);
		exameEspecialidade.setAghEspecialidades(especialidades);

	}

	@Test
	public void verificaStatusEspecialidadeTest(){
		try {
			popularExameDependente();
			systemUnderTest.verificaStatusEspecialidade(exameEspecialidade);

		} catch (BaseException e) {
			assertEquals("AEL_00434", e.getMessage());
		}
	}
}