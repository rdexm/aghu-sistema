package br.gov.mec.aghu.faturamento.business;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Priority;
import org.junit.Assert;
import org.junit.Test;

public class GeracaoArquivoLogTest {

	final Log log = LogFactory.getLog(this.getClass());

	@SuppressWarnings("deprecation")
	@Test
	public void testLog()
			throws Exception {

		GeracaoArquivoLog logger = null;
		Priority prioridade = null;
		String mensagem = null;
		Throwable excessao = null;

		//setup
		prioridade = Priority.INFO;
		mensagem = "Test log";
		logger = new GeracaoArquivoLog();
		//assert
		try {
			logger.log(prioridade, mensagem, excessao);
		} catch (Exception e) {
			this.log.error(Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}

}
