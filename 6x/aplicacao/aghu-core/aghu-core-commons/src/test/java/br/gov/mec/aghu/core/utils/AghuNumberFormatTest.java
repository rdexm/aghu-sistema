package br.gov.mec.aghu.core.utils;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author rcorvalao
 *
 */
public class AghuNumberFormatTest {

	/**
	 * Teste para milhar e Tres casas decimais. Positivo
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaMilharETresCasasDecimaisPositivo() {
		BigDecimal valor = new BigDecimal("1234.560");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '1.234,56', mas retornou: " + s, "1.234,56".equals(s));
	}

	
	/**
	 * Teste para milhar e Tres casas decimais. Negativo
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaMilharETresCasasDecimaisNegativo() {
		BigDecimal valor = new BigDecimal("-1234.560");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '-1.234,56', mas retornou: " + s, "-1.234,56".equals(s));
	}

	@Test
	public void testFormatarNumeroMoedaNumeroDoisDigitosSemDecimalNegativo() {
		BigDecimal valor = new BigDecimal("-12");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '-12,00', mas retornou: " + s, "-12,00".equals(s));
	}
	
	@Test
	public void testFormatarNumeroMoedaNumeroDoisDigitosSemDecimalPositivo() {
		BigDecimal valor = new BigDecimal("56");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '56,00', mas retornou: " + s, "56,00".equals(s));
	}
	
	@Test
	public void testFormatarNumeroMoedaNumeroTresDigitosSemDecimalNegativoObtidoSubtracao() {
		BigDecimal valor = new BigDecimal(56-1256);
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '-1.200,00', mas retornou: " + s, "-1.200,00".equals(s));
	}
	
	
	/**
	 * Teste para Dois digitos e Tres casas decimais. Positivo. Arredondamento neutro/trunc.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaDoisDigitosETresCasasDecimaisPositivoArredondamentoNeutro() {
		BigDecimal valor = new BigDecimal("23.564");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '23,56', mas retornou: " + s, "23,56".equals(s));
	}
	
	/**
	 * Teste para Dois digitos e Tres casas decimais. Positivo. Arredondamento neutro/trunc, valor x,xx5.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaDoisDigitosETresCasasDecimaisPositivoArredondamentoNeutroValor5() {
		BigDecimal valor = new BigDecimal("23.565");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '23,56', mas retornou: " + s, "23,56".equals(s));
	}
	
	/**
	 * Teste para Dois digitos e Tres casas decimais. Positivo. Arredondamento ParaCima.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaDoisDigitosETresCasasDecimaisPositivoArredondamentoParaCima() {
		BigDecimal valor = new BigDecimal("23.566");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '23,57', mas retornou: " + s, "23,57".equals(s));
	}
	
	
	/**
	 * Teste para Dois digitos e uma casa decimal. Positivo.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaDoisDigitosEUmaCasaDecimalPositivo() {
		BigDecimal valor = new BigDecimal("23.5");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '23,50', mas retornou: " + s, "23,50".equals(s));
	}
	
	/**
	 * Teste para Dois separador de milhar. Positivo.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaDoisSeparadorMilharPositivo() {
		BigDecimal valor = new BigDecimal("9745512");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '9.745.512,00', mas retornou: " + s, "9.745.512,00".equals(s));
	}

	/**
	 * Teste para Dois separador de milhar. Negativo.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaDoisSeparadorMilharNegativo() {
		BigDecimal valor = new BigDecimal("-9745512");
				
		String s = AghuNumberFormat.formatarNumeroMoeda(valor);
		
		Assert.assertTrue("Deveria retornar '-9.745.512,00', mas retornou: " + s, "-9.745.512,00".equals(s));
	}
	
	
	
	// ###########################################################################
	// ###########################################################################
	
	
	/**
	 * Teste para milhar e Tres casas decimais. Negativo
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaAlternativoMilharETresCasasDecimaisNegativo() {
		BigDecimal valor = new BigDecimal("-1234.560");
				
		String s = AghuNumberFormat.formatarNumeroMoedaAlternativo(valor);
		
		Assert.assertTrue("Deveria retornar '(1.234,56)', mas retornou: " + s, "(1.234,56)".equals(s));
	}
	
	@Test
	public void testFormatarNumeroMoedaAlternativoNumeroDoisDigitosSemDecimalNegativo() {
		BigDecimal valor = new BigDecimal("-12");
				
		String s = AghuNumberFormat.formatarNumeroMoedaAlternativo(valor);
		
		Assert.assertTrue("Deveria retornar '(12,00)', mas retornou: " + s, "(12,00)".equals(s));
	}
	
	@Test
	public void testFormatarNumeroMoedaAlternativoNumeroTresDigitosSemDecimalNegativoObtidoSubtracao() {
		BigDecimal valor = new BigDecimal(56-1256);
				
		String s = AghuNumberFormat.formatarNumeroMoedaAlternativo(valor);
		
		Assert.assertTrue("Deveria retornar '(1.200,00)', mas retornou: " + s, "(1.200,00)".equals(s));
	}
	
	/**
	 * Teste para Dois separador de milhar. Negativo.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaAlternativoDoisSeparadorMilharNegativo() {
		BigDecimal valor = new BigDecimal("-9745512");
				
		String s = AghuNumberFormat.formatarNumeroMoedaAlternativo(valor);
		
		Assert.assertTrue("Deveria retornar '(9.745.512,00)', mas retornou: " + s, "(9.745.512,00)".equals(s));
	}
	
	/**
	 * Teste para Dois separador de milhar. Negativo.
	 * 
	 */
	@Test
	public void testFormatarNumeroMoedaAlternativoDoisSeparadorMilharPositivo() {
		BigDecimal valor = new BigDecimal("9745512");
				
		String s = AghuNumberFormat.formatarNumeroMoedaAlternativo(valor);
		
		Assert.assertTrue("Deveria retornar '9.745.512,00', mas retornou: " + s, "9.745.512,00".equals(s));
	}

	
}
