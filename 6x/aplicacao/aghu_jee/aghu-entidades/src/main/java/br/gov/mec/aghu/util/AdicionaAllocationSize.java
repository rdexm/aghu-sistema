package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class AdicionaAllocationSize {

	private static final File BASE_PATH = new File("/home/geraldo/workspace-kepler/aghu-entidades2/src/main/java");
	
	private static final String ALLOCATION_SIZE_SRT = ", allocationSize = 1";
	
	public static void main(String[] args) {
		processaArquivos(BASE_PATH);
	}
	
	private static void processaArquivos(File baseDir) {
		File[] files = baseDir.listFiles();
		
		for (File file : files) {
			if (file.isDirectory()) {
				processaArquivos(file);
			} else {
				adicionaAllocationSize(file);
			}
		}
	}

	private static void adicionaAllocationSize(File file) {
		String conteudo;
		try {
			conteudo = FileUtils.readFileToString(file);
		
			if (conteudo.contains("@SequenceGenerator")) {
				if (!conteudo.contains("allocationSize")) {
					StringBuffer sb = new StringBuffer(conteudo);
					
					int startIndex = sb.indexOf("@SequenceGenerator");
					int endIndex = sb.indexOf(")", startIndex);
					
					sb.insert(endIndex, ALLOCATION_SIZE_SRT);
					
					FileUtils.writeStringToFile(file, sb.toString());
				} else {
					System.out.println("A classe " + file.getName() + " já possui allocationSize."); //NOPMD
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace(); //NOPMD
		}

	}
	
}