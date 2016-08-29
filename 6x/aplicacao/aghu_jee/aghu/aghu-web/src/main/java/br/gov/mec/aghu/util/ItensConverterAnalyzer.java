package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ItensConverterAnalyzer {
	
	private static final Log LOG = LogFactory.getLog(ItensConverterAnalyzer.class);
	
	private static String path_origem_controller = "web/src/main/java/";
	
	private List<String> listaNaoProcessados = new LinkedList<>();
	
	
	public void execute(String packageName) {
		String path = ItensConverterAnalyzer.class.getResource("").getPath();
		if (StringUtils.isNotBlank(path)) {
			path = path.substring(0, path.indexOf("aghu-web"));
			path_origem_controller = path + path_origem_controller;
		}
		LOG.info("Iniciando processo de ajuste dos arquivos da camada de apresentação: " + path);
		
		String pathFull = path_origem_controller + packageName;
		
		File folderOrigem = new File (pathFull);
		LOG.info("Total de Arquivos: " + folderOrigem.listFiles().length);
		int count = 0;
		for (File file : folderOrigem.listFiles()) {
			if (file.getAbsolutePath().endsWith("Converter.java")) {
				LOG.info(file.getAbsolutePath());
				try {
					if (this.incluirComentario(file)) {
						count = count + 1;
					} else {
						listaNaoProcessados.add(file.getAbsolutePath());
					}
				} catch (IOException e) {
					LOG.error("Erro ao processar: " + file.getAbsolutePath());
					listaNaoProcessados.add(file.getAbsolutePath());
				}
			} else {
				listaNaoProcessados.add(file.getAbsolutePath());
			}
		}//FOR
		LOG.info("Arquivos processados: " + count);
		
		
		for (String fileNameFull : listaNaoProcessados) {
			System.out.println(fileNameFull); //NOPMD
		}

	}


	private boolean incluirComentario(File file) throws IOException {
		boolean returnValue = false;
		
		String comentario = "\n /* \n"
				+ "ATENÇÃO: Este arquivo já se encontra em processo de adaptação para a nova arquitetura. \n"
				+ "Observe atentamente os campos abaixo para saber como proceder com merges provenientes \n"
				+ "de outros branches para cá. Se o ESTADO indicar que a tela já se encontra migrada, \n"
				+ "proceda com o merge manual fazendo as devidas adaptações, e em caso de dúvida procure \n"
				+ "os colegas envolvidos. \n" 
				+ "ESTADO: MIGRADO \n"
				+ "DESENVOLVEDOR: Migracao Arquitetura \n"
				+ "NOTA: classes do tipo converter nao tem mais necessidade na Nova Arquitetura.\n"
				+ "Estas classe foram substituidas por um classe generica BaseEntityConverter \n"
				+ "Apenas em casos especiais que o converter deve ser migrado. \n"
				+ "*/ \n";
		String strFile = FileUtils.readFileToString(file);
		
		StringBuilder builder = new StringBuilder(strFile);
		int initClass = builder.indexOf("public class");
		
		if (initClass > 0) {
			builder.insert(initClass-1, comentario);
			
			FileUtils.writeStringToFile(file, builder.toString());
			returnValue = true;
		}
		
		return returnValue;
	}
	
	

}
