package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class AbstractGeracaoArquivoRNTest {

	final Log log = LogFactory.getLog(this.getClass());

	@Test
	public void testObterURIArquivo() {

		URI result = null;
		String path = null;
		String prefixoArq = null;
		String extensaoArq = null;
		int ndx = 0;

		//setup
		prefixoArq = "Prefixo";
		extensaoArq = ".ext";
		//assert
		try {
			result = AbstractGeracaoArquivoRN.obterURIArquivo(prefixoArq, extensaoArq);
			Assert.assertNotNull(result);
			path = result.getPath();
			ndx = path.lastIndexOf("/");
			if ((ndx >= 0) && (ndx < path.length())) {
				path = path.substring(ndx + 1, path.length());				
			}
			Assert.assertTrue(path.startsWith(prefixoArq));
			Assert.assertTrue(path.endsWith(extensaoArq));
		} catch (IOException e) {
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e);
		}
		//setup
		prefixoArq = null;
		extensaoArq = null;
		//assert
		try {
			result = AbstractGeracaoArquivoRN.obterURIArquivo(prefixoArq, extensaoArq);
			Assert.assertNotNull(result);
			path = result.getPath();
			ndx = path.lastIndexOf("/");
			if ((ndx >= 0) && (ndx < path.length())) {
				path = path.substring(ndx + 1, path.length());				
			}
			Assert.assertTrue(path.startsWith(AbstractGeracaoArquivoRN.PREFIXO_PADRAO));
			Assert.assertTrue(path.endsWith(AbstractGeracaoArquivoRN.EXTENSAO_PADRAO));
		} catch (IOException e) {
			this.log.info(e);
			Assert.fail("Not expecting exception: " + e);
		}
	}
}
