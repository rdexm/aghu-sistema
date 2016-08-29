package br.gov.mec.aghu.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.print.PrintException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.CupsException;

@SuppressWarnings("PMD.PackagePrivateSeamContextsManager")
public class CupsUtil {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6325788427446768603L;
	private static final Log LOG = LogFactory.getLog(CupsUtil.class);
	private static String cupsTempPath;
	private static final String CUPS_TEMP_DIR = "cupstemp/";

	static {
		String temp = System.getProperty("java.io.tmpdir");
		StringBuilder sb = new StringBuilder(temp);

		if (!temp.endsWith(File.separator)) {
			sb.append(File.separator);
		}

		sb.append(CUPS_TEMP_DIR);

		cupsTempPath = sb.toString();
		File diretorio = new File(cupsTempPath);
		if (!diretorio.exists()) {
			diretorio.mkdir();
		}
	}

	/**
	 * Executa comando para enviar arquivo para a impressora
	 * 
	 * @param ipCups
	 * @param filaImpressora
	 * @param caminho
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws CupsException
	 */
	private static void executarImpressao(String ipCups, String filaImpressora,
			File arquivo, String[] opcoes) throws IOException,
			InterruptedException, CupsException {

		String[] command = montaComando(ipCups, filaImpressora, opcoes, arquivo.getAbsolutePath());

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		LOG.info("Comando envio para a impressora: " + ArrayUtils.toString(command));

		Process p = processBuilder.start();
		int outputComando = p.waitFor();
		LOG.info("Saida do comando: " + outputComando);

		BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line = null;
		while ((line = error.readLine()) != null) {
			LOG.error(line + "\n");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		line = null;
		while ((line = in.readLine()) != null) {
			LOG.info(line + "\n");
		}

		// Remove o arquivo do sistema de arquivos.
		arquivo.delete();

		// Observa saída com comando para subir exception.
		if (outputComando > 0) {
			throw new CupsException(ipCups, filaImpressora);
		}
	}

	/**
	 * Envia impressão para uma fila no servidor CUPS.
	 * 
	 * @param servidor
	 *            ip ou nome do servidor CUPS
	 * @param fila
	 *            nome da fila
	 * @param documento
	 *            documento texto plano, normalmente código ZPL
	 * @param fileName
	 *            nome para geração do arquivo temporário
	 * @throws PrintException
	 */
	public static void envia(String servidor, String fila, String documento, String fileName) throws PrintException {
		LOG.debug(String.format("enviando para cups %s fila %s fileName %s", servidor, fila, fileName));
		try {
			// gravar documento no file system
			String fullPath = getFullPath(fileName);
			Writer writer = new FileWriter(fullPath);
			IOUtils.write(documento, writer);
			IOUtils.closeQuietly(writer);
			LOG.debug(String.format("arquivo temporario em %s", fullPath));

			executarImpressao(servidor, fila, new File(fullPath), null);

		} catch (IOException e) {
			throw new PrintException("erro na gravação de arquivo temporário.", e);
		} catch (InterruptedException e) {
			throw new PrintException("comando lpr interrompido.", e);
		} catch (CupsException e) {
			throw new PrintException(e.getMessage(), e);
		}
	}

	/**
	 * Envia impressão para uma fila no servidor CUPS.
	 * 
	 * @param servidor
	 *            ip ou nome do servidor CUPS
	 * @param fila
	 *            nome da fila
	 * @param documento
	 *            documento texto plano, normalmente código ZPL
	 * @throws PrintException
	 */
	public static void envia(String servidor, String fila, String documento) throws PrintException {
		CupsUtil.envia(servidor, fila, documento, null);
	}

	/**
	 * Envia impressão para uma fila no servidor CUPS.
	 * 
	 * @param servidor
	 *            ip ou nome do servidor CUPS
	 * @param fila
	 *            nome da fila
	 * @param bytes
	 *            documento em bytes, normalmente um pdf
	 * @param fileName
	 *            nome para geração do arquivo temporário
	 * @throws PrintException
	 */
	public static void envia(String servidor, String fila, byte[] bytes, String fileName) throws PrintException {
		envia(servidor, fila, null, bytes, fileName);
	}
	
	public static void envia(String servidor, String fila, String[] opcoes,
			byte[] bytes, String fileName) throws PrintException {
		LOG.debug(String.format("enviando para cups %s fila %s fileName %s",
				servidor, fila, fileName));
		try {
			// gravar documento no file system
			String fullPath = getFullPath(fileName);
			OutputStream os = new FileOutputStream(fullPath);
			IOUtils.write(bytes, os);
			IOUtils.closeQuietly(os);
			LOG.debug(String.format("arquivo temporario em %s", fullPath));

			executarImpressao(servidor, fila, new File(fullPath), opcoes);

		} catch (IOException e) {
			throw new PrintException("erro na gravação de arquivo temporário.", e);
		} catch (InterruptedException e) {
			throw new PrintException("comando lpr interrompido.", e);
		} catch (CupsException e) {
			throw new PrintException(e.getMessage(), e);
		}
	}

	/**
	 * Envia impressão para uma fila no servidor CUPS.
	 * 
	 * @param servidor
	 *            ip ou nome do servidor CUPS
	 * @param fila
	 *            nome da fila
	 * @param bytes
	 *            documento em bytes, normalmente um pdf
	 * @throws PrintException
	 */
	public static void envia(String servidor, String fila, byte[] bytes) throws PrintException {
		CupsUtil.envia(servidor, fila, bytes, null);
	}
	
	/**
	 * @deprecated mantido para atender classe CupsON também depreciada
	 * @param servidor
	 * @param fila
	 * @param file
	 * @throws PrintException
	 */
	public static void envia(String servidor, String fila, File file)
			throws PrintException {
		try {
			CupsUtil.executarImpressao(servidor, fila, file, null);
		} catch (IOException e) {
			throw new PrintException("erro na gravação de arquivo temporário.",
					e);
		} catch (InterruptedException e) {
			throw new PrintException("comando lpr interrompido.", e);
		} catch (CupsException e) {
			throw new PrintException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna path completo para um arquivo temporário.<br />
	 * O nome do arquivo fornecido é concatenado em um UUID.<br/>
	 * Exemplo:<br />
	 * getFullPath("RelatorioNome");
	 * <p>
	 * \\home\\user\\cupstemp\\RelatorioNome-b429320b-ed78-46ed-a16b-3df4de1e0d1 e
	 * </p>
	 * 
	 * @param fileName
	 *            nome legível
	 * @return
	 */
	private static String getFullPath(String fileName) {
		StringBuilder builder = new StringBuilder(cupsTempPath);
		if (fileName != null) {
			fileName = check(fileName);
			builder.append(fileName);
			builder.append('-');
		}
		builder.append(UUID.randomUUID().toString());
		return builder.toString();
	}

	/**
	 * Retorna o nome do arquivo com os caracteres inválidos substituidos por '_'.
	 * 
	 * @param fileName
	 * @return
	 */
	private static String check(String fileName) {
		fileName = fileName.replace('\\', '_') //
				.replace('/', '_') //
				.replace('*', '_') //
				.replace('?', '_') //
				.replace('<', '_') //
				.replace('>', '_');

		return fileName;
	}
	

	protected static String[] montaComando(String ipCups, String filaImpressora,
			String[] opcoes, String caminho) {
		
		List<String> pList = new ArrayList<String>();
		if (CoreUtil.isWindows()) {
			pList.add("lpr");
			pList.add("-S");
			pList.add(ipCups);
			pList.add("-P");
			pList.add(filaImpressora);
		}
		if (CoreUtil.isUnix()) {
			pList.add("lp");
			pList.add("-h");
			pList.add(ipCups);
			pList.add("-d");
			pList.add(filaImpressora);
		}

		// adiciona as opcoes
		if (CoreUtil.isUnix() && opcoes != null) {
			pList.addAll(Arrays.asList(opcoes));
		}

		pList.add(caminho);

		return pList.toArray(new String[] {});
	}
}
