package br.gov.mec.aghu.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class MpmPrescricaoDietaTest {
	
	private MpmPrescricaoDieta systemUnderTest;
	
	@Before
    public void doBeforeEachTestCase() {
    	
        systemUnderTest = new MpmPrescricaoDieta();
        
    }
	
	/**
	 * Verifica se pega a informação de avaliação nutricionista.
	 */
	@Test
	public void getDescricaoFormatada001Test() {
		
		systemUnderTest.setIndAvalNutricionista(Boolean.TRUE);
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals("Avaliação Nutricionista;", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de bomba de infusão.
	 */
	@Test
	public void getDescricaoFormatada002Test() {
		
		systemUnderTest.setIndBombaInfusao(Boolean.TRUE);
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals("BI;", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de observacao.
	 */
	@Test
	public void getDescricaoFormatada003Test() {
		
		systemUnderTest.setObservacao("TESTE");
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals("TESTE;", atual);
		
	}

}
