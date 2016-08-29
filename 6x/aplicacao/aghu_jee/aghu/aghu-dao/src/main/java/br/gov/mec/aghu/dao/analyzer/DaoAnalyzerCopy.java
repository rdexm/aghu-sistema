package br.gov.mec.aghu.dao.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class DaoAnalyzerCopy {

	private static final String DIR_NOVO_AGHUDAO = "/aghu-dao";
	private static final String DIR_MAVEN_SOURCE = "/src/main/java";
	private static final String DIR_BUSINESS = "/business" + DIR_MAVEN_SOURCE;
	
	private static final String DIR_BASE_PACOTE = "/br/gov/mec/aghu";
	private static final String DIR_DAO = "/dao";

	private String dir_base_origem;
	private String dir_base_destino;
	
	public DaoAnalyzerCopy(String origem, String destino) {
		dir_base_origem = origem;
		dir_base_destino = destino;
	}
	
	public void execute(String[] modulos) {
		Map<String, List<File>> mapModulosDaos = this.getMapModuloDao(modulos);
		
//		String[] classesBase = {"ConselhoRegionalMedicinaEnum.java", "ConselhoRegionalOdontologiaEnum.java", "EsquemasOracleEnum.java", "ObjetosBancoOracleEnum.java"};
//		String pathOrigemBase = DIR_BASE_ORIGEM + "/base" + DIR_MAVEN_SOURCE + DIR_BASE_PACOTE + "/util";
//		List<File> listPastas = new LinkedList<>();
//		for (String nomeArquivo : classesBase) {
//			listPastas.add(new File(pathOrigemBase + "/" + nomeArquivo));
//		}
//		executeBasePavaVO(listPastas, "/util");
		
		int totalModulos = 0;
		int totalClassesDaos = 0;
		for (String modulo: mapModulosDaos.keySet()) {
			List<File> listDirDAOs = mapModulosDaos.get(modulo);
			totalModulos = totalModulos + 1;
			System.out.println("Modulo: " + modulo + " listDir - tamanho: " + listDirDAOs.size());//NOPMD
			for (File file : listDirDAOs) {
				File[] listClassesDaos = file.listFiles();
				System.out.println("    " + file.getAbsolutePath() + " total: " + listClassesDaos.length);//NOPMD
				totalClassesDaos = totalClassesDaos + listClassesDaos.length;
				
				this.executeDaos(listClassesDaos, modulo);
				
			}// for list dir
		}//FOR map

		System.out.println("totalModulos: " + totalModulos);//NOPMD
		System.out.println("totalClassesDaos: " + totalClassesDaos);//NOPMD

	}

	private void executeDaos(File[] listClassesDaos, String modulo) {
		String caminhoDestino = dir_base_destino + DIR_NOVO_AGHUDAO + DIR_MAVEN_SOURCE + DIR_BASE_PACOTE + "/" + modulo + DIR_DAO;
		for (File fileDao : listClassesDaos) {
			System.out.println(fileDao.getAbsolutePath());	//NOPMD		
			try {
				String stConteudo = FileUtils.readFileToString(fileDao);
				
				File fileSaida = new File(caminhoDestino + "/" + fileDao.getName());
				FileUtils.writeStringToFile(fileSaida, stConteudo);
			} catch (IOException e) {
				System.out.println("Problema ao copiar arquivo: " + fileDao.getAbsolutePath());//NOPMD
			}
		}//for listClassesDaos
		
	}

	protected void executeBasePavaVO(List<File> listClassesBase, String caminhoArquivo) {
		String caminhoDestino = dir_base_destino + "aghu-vo" + DIR_MAVEN_SOURCE + DIR_BASE_PACOTE + "/" + caminhoArquivo;
		for (File fileBase : listClassesBase) {
			System.out.println(fileBase.getAbsolutePath());	//NOPMD		
			try {
				String stConteudo = FileUtils.readFileToString(fileBase);
				
				File fileSaida = new File(caminhoDestino + "/" + fileBase.getName());
				FileUtils.writeStringToFile(fileSaida, stConteudo);
			} catch (IOException e) {
				System.out.println("Problema ao copiar arquivo: " + fileBase.getAbsolutePath());//NOPMD
			}
		}//for listClassesDaos
		
	}

	
	private Map<String, List<File>> getMapModuloDao(String[] modulos) {
		Map<String, List<File>> mapModuloDao = new HashMap<>();
		
		for (String modulo : modulos) {
			File folderOrigem = new File (dir_base_origem + "/" +  modulo + DIR_BUSINESS);
			
			final List<File> listDirDAOs = new LinkedList<>();
			mostrarPastasDAO(folderOrigem, listDirDAOs);
			
			mapModuloDao.put(modulo, listDirDAOs);
		}
		
		return mapModuloDao;
	}
	
	private void mostrarPastasDAO(File fileOrigem, List<File> listDirDAOs) {
		if (fileOrigem.isDirectory()) {
			if ("dao".equalsIgnoreCase(fileOrigem.getName())) {
				listDirDAOs.add(fileOrigem);
			} else {
				File[] filhos = fileOrigem.listFiles();
				for (File file : filhos) {
					mostrarPastasDAO(file, listDirDAOs);
				}
			}
		}// is dir
	}

}
