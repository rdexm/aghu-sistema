package br.gov.mec.aghu.exames.contratualizacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.exames.contratualizacao.business.ReceberArquivoXmlON.ReceberArquivoXmlONExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ReceberArquivoXmlONTest extends AGHUBaseUnitTest<ReceberArquivoXmlON>{
	
	private static final Log log = LogFactory.getLog(ReceberArquivoXmlONTest.class);

	/**
	 * Testa um nome de arquivo válido.
	 */
	@Test
	public void testVerificarFormatoArquivoXmlSucesso()  {
		String nomeXml2 = "ExamesPrefeitura_31012011_101010.xml";


		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
		} catch (BaseException e) {
			fail("Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());
		}	
	}

	/**
	 * Testa todas as datas de 2011 com hora padrão 10:10:10 com sucesso.
	 */
	@Test
	public void testVerificarFormatoArquivoXmlSucessoDatas2011()  {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 01);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2011);

		Calendar cal2 = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 31);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.YEAR, 2011);

		String INICIO_ARQUIVO = "ExamesPrefeitura";
		String SEPARATOR = "_";
		String HORA_PADRAO = "101010";
		String EXTENSAO = ".xml";
		String nomeXml2 = "";

		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

		do {
			nomeXml2 = INICIO_ARQUIVO + SEPARATOR + dateFormat.format(cal.getTime()) + SEPARATOR + HORA_PADRAO + EXTENSAO;
			try {
				systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			} catch (BaseException e) {
				fail("Data com problema: " + dateFormat.format(cal.getTime()) +" Não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());
				break;
			}	
			cal.add(Calendar.DAY_OF_MONTH, 1);
		} while (cal.before(cal2));
	}


	/**
	 * Testa um nome de arquivo inválido (ExamePrefeitura ao invés de ExamesPrefeitura).
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroNomenclaturaInicial()  {
		String nomeXml2 = "ExamePrefeitura_01012001_235959.xml";
		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois a nomenclatura é inválida.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}

	/**
	 * Testa um nome de arquivo inválido (Data inválida = 32/01/2001).
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroData()  {
		String nomeXml2 = "ExamePrefeitura_32012001_235959.xml";
		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois a nomenclatura é inválida.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}

	/**
	 * Testa um nome de arquivo inválido (Data inválida = 30/02/2001).
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroData2() {
		String nomeXml2 = "ExamePrefeitura_30022001_235959.xml";
		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois a nomenclatura é inválida.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}

	/**
	 * Testa um nome de arquivo inválido (Hora inválida = 24:00:00).
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroHora()  {
		String nomeXml2 = "ExamePrefeitura_30022001_240000.xml";
		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois a nomenclatura é inválida.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}

	/**
	 * Testa um nome de arquivo inválido (Hora inválida = 23:60:00).
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroHora2()  {
		String nomeXml2 = "ExamePrefeitura_30012012_235959.xml";
		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois a nomenclatura é inválida.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}

	/**
	 * Testa um nome de arquivo inválido (Hora inválida = 23:59:60).
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroHora3()  {
		String nomeXml2 = "ExamePrefeitura_30022001_235960.xml";
		try {
			systemUnderTest.verificarFormatoArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois a nomenclatura é inválida.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}
	
//	/**
//	 * Testa um arquivo válido.
//	 */
//	@Test
//	public void verificarEstruturaArquivoXmlSuccess()  {
//		String caminhoAbsolutoXml = "jar/src/test/java/br/gov/mec/aghu/exames/contratualizacao/business/ExamesPrefeitura_30012012_235959.xml";
//		try {
//			assertNotNull(systemUnderTest.verificarEstruturaArquivoXml(caminhoAbsolutoXml));
//		} catch (BaseException e) {
//			fail("Arquivo com problema, não deveria ocorrer uma exceção. Exceção gerada: " + e.getMessage());
//		}
//	}
	
	/**
	 * Testa um arquivo inexistente
	 */
	@Test
	public void testVerificarFormatoArquivoXmlErroNaoEncontrado()  {
		String nomeXml2 = "jar/src/test/java/br/gov/mec/aghu/exames/contratualizacao/business/ExamePrefeitura_30012012_235959.xml.";
		try {
			systemUnderTest.verificarEstruturaArquivoXml(nomeXml2);
			fail("Deveria ocorrer uma exceção, pois o arquivo não existe.");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_FORMATO);
		}
	}
	
	/**
	 * Testa permissoes diretorio rede
	 */
//	@Test
//	public void testVerificarDiretorioEntradaOK()  {
//		String diretorio = "jar/src/test/java/br/gov/mec/aghu/exames/contratualizacao/business";
//		System.out
//				.println("ReceberArquivoXmlONTest.testVerificarDiretorioEntrada(): diretorio = [" + diretorio + "].");
//		try {
//			systemUnderTest.verificarPermissaoPasta(diretorio);
//		} catch (BaseException e) {
//			Assert.fail();
//		}
//	}
//	
	/**
	 * Testa permissoes diretorio rede
	 */
	@Test
	public void testVerificarDiretorioNulo()  {
		String diretorio = null;
		try {
			systemUnderTest.verificarPermissaoPasta(diretorio);
			Assert.fail("Deveria mostrar erro");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MENSAGEM_NOME_DIRETORIO_VAZIO);
		}
		diretorio = "";
		try {
			systemUnderTest.verificarPermissaoPasta(diretorio);
			Assert.fail("Deveria mostrar erro");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MENSAGEM_NOME_DIRETORIO_VAZIO);
		}
	}
	
	/**
	 * Testa permissoes diretorio rede
	 */
//	@Test
//	public void testVerificarDiretorioArquivo()  {
//		String diretorio = "jar/src/test/java/br/gov/mec/aghu/exames/contratualizacao/business/ReceberArquivoXmlONTest.java";
//		System.out
//				.println("ReceberArquivoXmlONTest.testVerificarDiretorioEntrada(): diretorio = [" + diretorio + "].");
//		try {
//			systemUnderTest.verificarPermissaoPasta(diretorio);
//			Assert.fail("Deveria mostrar erro");
//		} catch (BaseException e) {
//			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MENSAGEM_CAMINHO_NAO_DIRETORIO);
//		}
//	}
	
	/**
	 * Testa permissoes diretorio rede
	 */
	@Test
	public void testVerificarDiretorioNaoExistente()  {
		String diretorio = "xyz" + Math.random();
		log.info("ReceberArquivoXmlONTest.testVerificarDiretorioEntrada(): diretorio = [" + diretorio + "].");
		try {
			systemUnderTest.verificarPermissaoPasta(diretorio);
			Assert.fail("Deveria mostrar erro");
		} catch (BaseException e) {
			assertEquals(e.getCode(), ReceberArquivoXmlONExceptionCode.MENSAGEM_DIRETORIO_NAO_EXISTE);
		}
	}
}
