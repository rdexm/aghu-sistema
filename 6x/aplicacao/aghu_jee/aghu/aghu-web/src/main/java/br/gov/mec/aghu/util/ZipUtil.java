package br.gov.mec.aghu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	private static final Integer BUFFER = 1024;

	public static List<String> unZip(InputStream stream) throws FileNotFoundException, IOException {
		final List<String> listaArquivos = new ArrayList<String>();
		
		ZipInputStream zin = new ZipInputStream(stream);
		
		ZipEntry ze = null;
		BufferedOutputStream bw = null;
		while ((ze = zin.getNextEntry()) != null) {
			listaArquivos.add(ze.getName());
			
			bw = new BufferedOutputStream(new FileOutputStream(ze.getName()));
			
			for (int c = zin.read(); c != -1; c = zin.read()) {
				bw.write(c);
			}
			
			zin.closeEntry();
			bw.close();
		}
		zin.close();
		
		return listaArquivos;
	}

	/**
	 * Descompacta um arquivo .zip passado como parâmetro e retorna a lista de arquivos extraídos
	 * Adaptado de http://java.sun.com/developer/technicalArticles/Programming/compression/
	 * 
	 * @param caminhoArquivo
	 * @throws Exception
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "PMD.SignatureDeclareThrowsException"})
	public static List<String> unZip(String caminhoArquivo) throws Exception {
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		List<String> listaArquivos = new ArrayList<String>();
		ZipEntry entry;
		ZipFile zipfile = new ZipFile(caminhoArquivo);
		Enumeration e = zipfile.entries();
		while (e.hasMoreElements()) {
			entry = (ZipEntry) e.nextElement();
			//System.out.println("Extracting: " + FileUtil.obtemPasta(caminhoArquivo) + entry);
			
			
			
			listaArquivos.add(FileUtil.obtemPasta(caminhoArquivo) + entry);
			is = new BufferedInputStream(zipfile.getInputStream(entry));
			int count;
			byte data[] = new byte[BUFFER];
			FileOutputStream fos = new FileOutputStream(FileUtil.obtemPasta(caminhoArquivo) + entry.getName());
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
			is.close();
		}
		zipfile.close();
		return listaArquivos;
	}

	// @SuppressWarnings("rawtypes")
	/*
	 * public static void descompactaZip(String caminhoArquivo) throws
	 * IOException { Enumeration entries; ZipFile zipFile; zipFile = new
	 * ZipFile(caminhoArquivo);
	 * 
	 * entries = zipFile.entries();
	 * 
	 * while (entries.hasMoreElements()) { ZipEntry entry = (ZipEntry)
	 * entries.nextElement();
	 * 
	 * if (entry.isDirectory()) { // Assume directories are stored parents first
	 * then // children. System.err.println("Extracting directory: " +
	 * entry.getName()); // This is not robust, just for demonstration purposes.
	 * (new File(entry.getName())).mkdir(); continue; }
	 * 
	 * System.err.println("Extracting file: " + entry.getName());
	 * copyInputStream( zipFile.getInputStream(entry), new
	 * BufferedOutputStream(new FileOutputStream(entry .getName()))); }
	 * 
	 * zipFile.close(); }
	 * 
	 * private static void copyInputStream(InputStream in, OutputStream out)
	 * throws IOException { byte[] buffer = new byte[1024]; int len;
	 * 
	 * while ((len = in.read(buffer)) >= 0) return this.criarStreamedContentPdf(buffer, 0, len);
	 * 
	 * in.close(); out.close(); }
	 */

}
