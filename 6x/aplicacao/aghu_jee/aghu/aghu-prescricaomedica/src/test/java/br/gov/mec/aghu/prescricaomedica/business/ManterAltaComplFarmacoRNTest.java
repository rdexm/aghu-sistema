package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmAltaComplFarmaco;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.business.ManterAltaSumarioRN.ManterAltaSumarioRNExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Testes basicos para os metodos de verificacao da classe ManterAltaComplFarmacoRN.<br>
 * A classe nao necessitou testes abrangentes, pois nao tinha muitas regras.<br>
 * As regras estao implementadas em ManterAltaComplFarmacoRN 
 * e a classe de teste desta deve fazer os testes necessarios.<br>
 * 
 * @author gfmenezes
 */
public class ManterAltaComplFarmacoRNTest extends AGHUBaseUnitTest<ManterAltaComplFarmacoRN>{

	@Mock
	private ManterAltaSumarioRN mockedManterAltaSumarioRN;
	
	/**
	 * Pre insert.
	 * Testa o retorno de erro.
	 */
	@Test
	public void verificaPreInserirAltaComplFarmaco001() {
		MpmAltaComplFarmaco altaComplFarmaco = new MpmAltaComplFarmaco();


		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			systemUnderTest.preInserirAltaComplFarmaco(altaComplFarmaco);
			Assert.fail("Deveria retornar exception.");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre insert.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreInserirAltaComplFarmaco002() {
		MpmAltaComplFarmaco altaComplFarmaco = new MpmAltaComplFarmaco();
		
		try {
			
			systemUnderTest.preInserirAltaComplFarmaco(altaComplFarmaco);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception.");
		}
	}
	
	/**
	 * Pre update.
	 * Testa o retorno de erro. 
	 */
	@Test
	public void verificaPreAtualizaraltaComplFarmaco001() {
		MpmAltaComplFarmaco altaComplFarmaco = new MpmAltaComplFarmaco();
    	
		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			systemUnderTest.preAtualizaraltaComplFarmaco(altaComplFarmaco);
			Assert.fail("Deveria retornar exception.");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre update.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreAtualizaraltaComplFarmaco002() {
		MpmAltaComplFarmaco altaComplFarmaco = new MpmAltaComplFarmaco();
    	
		try {
			systemUnderTest.preAtualizaraltaComplFarmaco(altaComplFarmaco);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception.");
		}
	}
	
	/**
	 * Pre remove.
	 * Testa o retorno de erro.
	 */
	@Test
	public void verificaPreRemoverAltaComplFarmaco001() {
		MpmAltaComplFarmaco altaComplFarmaco = new MpmAltaComplFarmaco();
    	
		try {
			Mockito.doThrow(new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.MPM_03735)).when(mockedManterAltaSumarioRN).verificarAltaSumarioAtivo(Mockito.any(MpmAltaSumario.class));
			systemUnderTest.preRemoverAltaComplFarmaco(altaComplFarmaco);
			Assert.fail("Deveria retornar exception.");
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Pre remove.
	 * Testa SEM retorno de erro.
	 */
	@Test
	public void verificaPreRemoverAltaComplFarmaco002() {
		MpmAltaComplFarmaco altaComplFarmaco = new MpmAltaComplFarmaco();
    	
		try {
			systemUnderTest.preRemoverAltaComplFarmaco(altaComplFarmaco);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NAO Deveria retornar exception.");
		}
	}
	
}
