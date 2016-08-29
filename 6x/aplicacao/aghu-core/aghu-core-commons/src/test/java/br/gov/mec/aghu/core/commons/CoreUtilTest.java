package br.gov.mec.aghu.core.commons;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CoreUtilTest {
	
	@Test
	public void testConverterRTF2TextComAcentuacao() {
		String s = "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1046{\\fonttbl{\\f0\\fmodern Courier New;}}" +
		"\\viewkind4\\uc1\\pard\\f0\\fs24" + 
		"\\par " +
		"\\par " +
		"\\par" +
		"\\par            Exame avaliado na urg\\'eancia pelo m\\'e9dico assistente." +
		"\\par }";
		
		String text = CoreUtil.converterRTF2Text(s);
		System.out.println("Text: " + text);

		Assert.assertTrue("Nao pode ser nulo", text != null);
		Assert.assertTrue("Nao pode ter caracter 'deff0'", !text.contains("deff0"));
		Assert.assertTrue("Nao pode ter caracter 'Courier New;'", !text.contains("Courier New;"));
	}
	
	
	@Test
	public void testConverterRTF2TextSemAcentuacao() {
		String s =
		"{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1046{\\fonttbl{\\f0\\fmodern Courier New;}}" +
		"\\viewkind4\\uc1\\pard\\f0\\fs20 Exame realizado no andar." +
		"\\par }";
		
		String text = CoreUtil.converterRTF2Text(s);
		System.out.println("Text: " + text);
		
		Assert.assertTrue("Nao pode ser nulo", text != null);
		Assert.assertTrue("Nao pode ter caracter 'deff0'", !text.contains("deff0"));
		Assert.assertTrue("Nao pode ter caracter 'Courier New;'", !text.contains("Courier New;"));
	}
	
	@Test
	public void testConverterRTF2TextSemCaracteresRTF() {
		String s =	
			"Número AP: 1364811" +
			"Material: " +
			"1.) PRÓSTATA:adenoma" +
			"" +
			"Macroscopia: " +
			"Recebido, fixado em formalina, espécime cirúrgico de prostatectomia suprapúbica pesando 33,0 g e medindo 6,0 x 3,7 x 3,5 cm. Externamente é pardo-clara e bosselada e aos cortes apresenta áreas sólidas e outras cribriformes. CS." +
			"" +
			"Microscopia: " +
			"" +
			"" +
			"Diagnóstico: " +
			"- HIPERPLASIA NODULAR DA PRÓSTATA." +
			"" +
			"Patologistas: " +
			"DEISE R. MAFFAZZONI  CREMERS: 12610" +
			"" +
			"CASSIANO S. SCHOLZE  CREMERS: 35031";
			
		String text = CoreUtil.converterRTF2Text(s);
		System.out.println("Text: " + text);	

		Assert.assertTrue("Nao pode ser nulo", text != null);
		Assert.assertTrue("Nao pode ter caracter 'deff0'", !text.contains("deff0"));
		Assert.assertTrue("Nao pode ter caracter 'Courier New;'", !text.contains("Courier New;"));
		
		Assert.assertTrue(s.equals(text));
	}	
	
	
	@Test
	public void testConverterRTF2TextComAcentuacaoMaisFormatacoes() {
		String s =
			"{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1046{\\fonttbl{\\f0\\fnil Courier New;}{\\f1\\fmodern Courier New;}{\\f2\\fmodern\\fcharset0 Courier New;}}" +
			"\\viewkind4\\uc1\\pard\\f0\\fs24  \\f1 " +
			"\\par " +
			"\\par \\f2           Exame avaliado na urgêancia pelo médico assistente.\\f1" + 
			"\\par " +
			"\\par \\pard\\f0" + 
			"\\par }";
	
		String text = CoreUtil.converterRTF2Text(s);
		System.out.println("Text: " + text);	

		Assert.assertTrue("Nao pode ser nulo", text != null);
		Assert.assertTrue("Nao pode ter caracter 'deff0'", !text.contains("deff0"));
		Assert.assertTrue("Nao pode ter caracter 'Courier New;'", !text.contains("Courier New;"));
	
	}
	
	@Test
	public void testConverterRTF2TextComTextoNulo() {
	
		String text = CoreUtil.converterRTF2Text(null);
		System.out.println("Text: " + text);
		Assert.assertTrue(text == null);
	
	}
	
	
	
	
	
	@Test
	public void testConverterRTF2TextComAcentuacaoCharset() {
		String s = 
			"{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1046{\\fonttbl{\\f0\\fnil Courier New;}{\\f1\\fmodern Courier New;}{\\f2\\fmodern\\fcharset0 Courier New;}}" +
			"\\viewkind4\\uc1\\pard\\f0\\fs24  \\f1 " +
			"\\par " +
			"\\par " +
			"\\par \\pard\\f2           Exame avaliado na urg\\'eancia pelo m\\'e9dico assistente.\\f0" + 
			"\\par }";
	
		String text = CoreUtil.converterRTF2Text(s);
		System.out.println("Text: " + text);
		
		Assert.assertTrue("Nao pode ser nulo", text != null);
	}	
	
	
	@Test
	public void testConverterRTF2TextComAcentuacaoCaracteresRtfNoTexto() {
		String s = 
			"{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1046{\\fonttbl{\\f0\\fnil Courier New;}{\\f1\\fmodern Courier New;}{\\f2\\fmodern\\fcharset0 Courier New;}}" +
			"\\viewkind4\\uc1\\pard\\f0\\fs24  \\f1 " +
			"\\par " +
			"\\par " +
			"\\par \\pard\\f2  caminho: /opt/test - {1} [2] (3) Exame avaliado na urg\\'eancia.\\f0" + 
			"\\par }";
	
		String text = CoreUtil.converterRTF2Text(s);
		System.out.println("Text: " + text);
		
		Assert.assertTrue("Nao pode ser nulo", text != null);
	}	

}
