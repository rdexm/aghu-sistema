package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmAltaOutraEquipeSumr;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.business.ManterAltaSumarioRN.ManterAltaSumarioRNExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Testes basicos para os metodos de verificacao da classe ManterAltaOutraEquipeSumrRN. <br>
 * 
 * @author gfmenezes
 *
 */
public class ManterAltaOutraEquipeSumrRNTest extends AGHUBaseUnitTest<ManterAltaOutraEquipeSumrRN>{
	
	@Mock
	private ManterAltaSumarioRN mockedManterAltaSumarioRN;
	
	/**
	 * Pre insert.
	 * Testa retorno de erro.
	 */
	@Test
	public void verificaPreInserirAltaOutraEquipeSumr001(){
		MpmAltaOutraEquipeSumr altaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
		
		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			systemUnderTest.preInserirAltaOutraEquipeSumr(altaOutraEquipeSumr);
			Assert.fail("Deveria retornar exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre insert.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreInserirAltaOutraEquipeSumr002(){
		MpmAltaOutraEquipeSumr altaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
		
		try {
			
			systemUnderTest.preInserirAltaOutraEquipeSumr(altaOutraEquipeSumr);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception.");
		}
	}
	
	/**
	 * Pre remove
	 * Testa retorno de erro
	 */
	//@Test
	public void verificaPreRemoverAltaOutraEquipeSumr001(){
		MpmAltaOutraEquipeSumr altaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
		
		try {
			
			systemUnderTest.removerAltaOutraEquipeSumr(altaOutraEquipeSumr);
			Assert.fail("Deveria retornar exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre remove
	 * Testa SEM retorno de erro
	 */
	@Test
	public void verificaPreRemoverAltaOutraEquipeSumr002(){
		MpmAltaOutraEquipeSumr altaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
		
		try {
			
			systemUnderTest.preRemoverAltaOutraEquipeSumr(altaOutraEquipeSumr);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception.");
		}
	}
	
}
