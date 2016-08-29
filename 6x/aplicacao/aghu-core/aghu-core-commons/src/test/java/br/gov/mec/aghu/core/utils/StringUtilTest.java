package br.gov.mec.aghu.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Assert;
import org.junit.Test;

/**
 * Classe de teste para StringUtil.
 * 
 * @author rcorvalao
 *
 */
public class StringUtilTest {
	
	/**
	 * Testa string vazia com null.
	 * Deve indicar que nao ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado001() {
		Boolean retorno = StringUtil.modificado("", null);
		
		Assert.assertFalse("String nao foi modificada.", retorno);
	}

	/**
	 * Testa null com string vazia.
	 * Deve indicar que nao ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado002() {
		Boolean retorno = StringUtil.modificado(null, "");
		
		Assert.assertFalse("String nao foi modificada.", retorno);
	}

	/**
	 * Testa null e uma string com espaco.
	 * Deve indicar que nao ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado003() {
		Boolean retorno = StringUtil.modificado(null, " ");
		
		Assert.assertFalse("String nao foi modificada.", retorno);
	}

	/**
	 * Testa uma string com espaco e null.
	 * Deve indicar que nao ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado004() {
		Boolean retorno = StringUtil.modificado("  ", null);
		
		Assert.assertFalse("String nao foi modificada.", retorno);
	}

	/**
	 * Testa duas strings com valores diferentes.
	 * Deve indicar que ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado005() {
		Boolean retorno = StringUtil.modificado("asd", "asd qwe");
		
		Assert.assertTrue(retorno);
	}

	/**
	 * Testa duas strings com valores diferentes. Apenas uma espaco de diferente.
	 * Deve indicar que ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado006() {
		Boolean retorno = StringUtil.modificado("asd", "asd ");
		
		Assert.assertTrue(retorno);
	}

	/**
	 * Testa dois nulls.
	 * Deve indicar que NAO ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado007() {
		Boolean retorno = StringUtil.modificado(null, null);
		
		Assert.assertFalse("String nao foi modificada.", retorno);
	}

	/**
	 * Testa duas strings vazias.
	 * Deve indicar que NAO ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado008() {
		Boolean retorno = StringUtil.modificado("", " ");
		
		Assert.assertFalse("String nao foi modificada.", retorno);
	}
	
	/**
	 * Testa uma strings com null.
	 * Deve indicar que ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado009() {
		Boolean retorno = StringUtil.modificado("asd", null);
		
		Assert.assertTrue(retorno);
	}

	/**
	 * Testa null com uma string.
	 * Deve indicar que ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado010() {
		Boolean retorno = StringUtil.modificado(null, "asd");
		
		Assert.assertTrue(retorno);
	}
	
	/**
	 * Testa duas strings iguais.
	 * Deve indicar que NAO ocorreu modificacao.
	 * 
	 */
	@Test
	public void validaModificado011() {
		Boolean retorno = StringUtil.modificado("asd", "asd");
		
		Assert.assertFalse(retorno);
	}
	
	
	
	

	
	/**
	 * Concatena duas string nao vazia, com separador null.
	 * 
	 */
	@Test
	public void validaConcatenar001() {
		String retorno = StringUtil.concatenar("strObrigatoria", "strOpcional", null);
		
		Assert.assertEquals("strObrigatoria; strOpcional", retorno);
	}

	/**
	 * Concatena duas string nao vazia, com separador vazio.
	 * 
	 */
	@Test
	public void validaConcatenar002() {
		String retorno = StringUtil.concatenar("strObrigatoria", "strOpcional", "");
		
		Assert.assertEquals("strObrigatoria; strOpcional", retorno);
	}

	/**
	 * Concatena duas string nao vazia, com separador com espaco.
	 * 
	 */
	@Test
	public void validaConcatenar003() {
		String retorno = StringUtil.concatenar("strObrigatoria", "strOpcional", " ");
		
		Assert.assertEquals("strObrigatoria; strOpcional", retorno);
	}
	
	/**
	 * Concatena uma string nao vazia e string vazia, com separador :.
	 * 
	 */
	@Test
	public void validaConcatenar004() {
		String retorno = StringUtil.concatenar("strObrigatoria", "", ":");
		
		Assert.assertEquals("strObrigatoria", retorno);
	}

	/**
	 * Concatena uma string nao vazia e null, com separador :.
	 * 
	 */
	@Test
	public void validaConcatenar005() {
		String retorno = StringUtil.concatenar("strObrigatoria", null, ":");
		
		Assert.assertEquals("strObrigatoria", retorno);
	}

	/**
	 * Concatena uma string nao vazia e string com espaco, com separador :.
	 * 
	 */
	@Test
	public void validaConcatenar006() {
		String retorno = StringUtil.concatenar("strObrigatoria", "  ", ":");
		
		Assert.assertEquals("strObrigatoria", retorno);
	}
	
	
	/**
	 * Concatena null e uma string nao vazia, com separador :.
	 * 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void validaConcatenar007() {
		StringUtil.concatenar(null, "strObrigatoria", ":");
		
		Assert.fail("Deveria ocorrer de parametro obrigatorio faltando.");
	}
	
	/**
	 * Instancia a classe.
	 * 
	 */
	@Test
	public void validaConcatenar008() {
		StringUtil str = new StringUtil();
		
		Assert.assertTrue(str != null);
	}
	
	/**
	 * Testa o método trim com sucesso.
	 */
	@Test
	public void testTrimSuccess() {
		String actual = " espaco ";
		String expected = "espaco";
		
		assertEquals(expected, StringUtil.trim(actual));
	}
	
	/**
	 * Testa o método trim com erro.
	 */
	@Test
	public void testTrimFail() {
		String actual = " espaco ";
		String expected = " espaco";
		
		assertNotSame(expected, StringUtil.trim(actual));
	}
	
	/**
	 * Testa o método trim com erro, enviando um null.
	 */
	@Test
	public void testTrimFailNull() {
		String actual = null;
		String expected = " espaco";
		
		assertNotSame(expected, StringUtil.trim(actual));
	}
	
	/**
	 * Testa o método trim com erro, enviando uma string vazia.
	 */
	@Test
	public void testTrimFailEmpty() {
		String actual = "";
		String expected = " espaco";
		
		assertNotSame(expected, StringUtil.trim(actual));
	}
	

}
