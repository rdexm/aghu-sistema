/**
 * 
 */
package br.gov.mec.aghu.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


/**
 * @author rcorvalao
 * 
 */
public class MpmPrescricaoMdtosTest {

	private MpmPrescricaoMdto umMpmPrescricaoMdtos;

	@Before
	public void doBeforeEachTestCase() {
		umMpmPrescricaoMdtos = new MpmPrescricaoMdto();
	}

	/**
	 * Verifica se pega as informações apenas com o objeto instanciado, deve retornar string vazia.
	 */
	@Test
	public void getDescricaoFormatada001Test() {
		String atual = umMpmPrescricaoMdtos.getDescricaoFormatada();
		
		assertEquals("", atual);
	}

}
