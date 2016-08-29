package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Baseado em: <a href="http://www.exampledepot.com/egs/java.util.zip/CreateZip.html">http://www.exampledepot.com/egs/java.util.zip/CreateZip.html</a>
 * @author gandriotti
 *
 */
class GeracaoArquivoZip {

	public static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;

	public static String obterNomeArquivo(
			final String nomeArquivo,
			final boolean removerUltimoIfem) {

		String result = null;
		int ifemNdx = 0;
		int pontoNdx = 0;

		//check
		if (nomeArquivo == null) {
			throw new IllegalArgumentException("Parametro nomeArquivo nao informado!!!");
		}
		//algo
		result = nomeArquivo;
		if (removerUltimoIfem) {
			pontoNdx = result.lastIndexOf('.');
			ifemNdx = result.lastIndexOf('-');
			if ((ifemNdx >= 0) && (ifemNdx < pontoNdx)) {
				result = result.substring(0, ifemNdx) + result.substring(pontoNdx, result.length());
			}
		}

		return result;
	}

	public static String obterNomeArquivo(
			final URI uriArquivo,
			final boolean removerUltimoIfem) {

		String result = null;
		File fileArq = null;

		//check
		if (uriArquivo == null) {
			throw new IllegalArgumentException("Parametro uriArquivo nao informado!!!");
		}
		//algo
		fileArq = new File(uriArquivo);
		result = obterNomeArquivo(fileArq, removerUltimoIfem);

		return result;
	}

	public static String obterNomeArquivo(
			final File fileArquivo,
			final boolean removerUltimoIfem) {

		String result = null;
		String nomeArquivo = null;

		//check
		if (fileArquivo == null) {
			throw new IllegalArgumentException("Parametro fileArquivo nao informado!!!");
		}
		//algo
		nomeArquivo = fileArquivo.getName();
		result = obterNomeArquivo(nomeArquivo, removerUltimoIfem);

		return result;
	}

	@SuppressWarnings("ucd")
	public static void gerarArquivoZip(
			final URI arquivoZip,
			final List<URI> arquivos,
			final boolean removerUltimoIfem)
			throws IOException {

		// Create a buffer for reading the files
		byte[] buffer = null;
		int qtdEscrito = 0;
		// Create the ZIP file
		ZipOutputStream out = null;
		FileInputStream in = null;
		ZipEntry entrada = null;
		File arqFile = null;
		File outFile = null;
		String nomeArq = null;

		//check
		if (arquivoZip == null) {
			throw new IllegalArgumentException("Parametro arquivoZip nao informado!!!");
		}
		if (arquivos == null) {
			throw new IllegalArgumentException("Parametro arquivos nao informado!!!");
		}
		if (arquivos.isEmpty()) {
			throw new IllegalArgumentException();
		}
		//algo
		buffer = new byte[BUFFER_SIZE_EQ_1M];
		outFile = new File(arquivoZip);
		out = new ZipOutputStream(new FileOutputStream(outFile));
		// Compress the files
		for (URI arq : arquivos) {
			arqFile = new File(arq);
			in = new FileInputStream(arqFile);
			nomeArq = obterNomeArquivo(arqFile, removerUltimoIfem);
			entrada = new ZipEntry(nomeArq);
			// Add ZIP entry to output stream.
			out.putNextEntry(entrada);
			// Transfer bytes from the file to the ZIP file
			while ((qtdEscrito = in.read(buffer)) > 0) {
				out.write(buffer, 0, qtdEscrito);
			}
			// Complete the entry
			out.closeEntry();
			in.close();
		}
		// Complete the ZIP file
		out.close();
	}
}
