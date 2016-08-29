package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;

/**
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
public class AbstractGeracaoArquivoRN extends AbstractFatDebugExtraFileLogEnable {

	private static final Charset CHARSET_ISO = Charset.forName("ISO-8859-1");
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	protected static final String PREFIXO_PADRAO = "file-";
	protected static final String EXTENSAO_PADRAO = ".dta";
	private static final String NOVA_LINHA_ARQ_SUS_EQ_CR_LF = "\r\n";
	/**
	 * 
	 */
	private static final long serialVersionUID = -2637164098218838032L;

	protected static URI obterURIArquivo(final String prefixoArq, final String extensaoArq)
			throws IOException {

		URI result = null;
		File file = null;
		String prefixoReal = null;
		String extensaoReal = null;

		if ((prefixoArq == null) || prefixoArq.isEmpty()) {
			prefixoReal = PREFIXO_PADRAO;
		} else {
			prefixoReal = prefixoArq;
		}
		if ((extensaoArq == null) || extensaoArq.isEmpty()) {
			extensaoReal = EXTENSAO_PADRAO;
		} else {
			extensaoReal = extensaoArq;
		}
		file = File.createTempFile(prefixoReal, extensaoReal);
		result = file.toURI();

		return result;
	}

	protected void gravarListaEntradasEmArquivo(
			final URI arquivo,
			final List<String> listaEntrada,
			final boolean converterParaIso)
			throws IOException {

		BufferedOutputStream out = null;
		File file = null;
		FileOutputStream writter = null;
		String linha = null;
		Charset coding = null;
		int gerados = 0;

		file = new File(arquivo);
		writter = new FileOutputStream(file);
		out = new BufferedOutputStream(writter);
		coding = converterParaIso
				? CHARSET_ISO
				: CHARSET_UTF8;
		for (String e : listaEntrada) {
			if ((e != null) && !e.isEmpty()) {
				linha = e + NOVA_LINHA_ARQ_SUS_EQ_CR_LF;
				out.write(linha.getBytes(coding));
				gerados++;
			}
		}
		out.flush();
		out.close();
		this.logFile(
				FaturamentoDebugCode.ARQ_GERADO,
				Integer.valueOf(gerados), Integer.valueOf(listaEntrada.size()),
				arquivo.toString());
	}

	public AbstractGeracaoArquivoRN(final GeracaoArquivoLog logger) {

		super(logger);

		if (logger == null) {
			throw new IllegalArgumentException("Parametro logger nao informado!!!");
		}
	}
}
