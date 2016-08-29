package br.gov.mec.aghu.util.migrador;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({ "PMD" })
public class ControllersMapFactory {
	
	private static final Log LOG = LogFactory.getLog(ControllersMapFactory.class);

	public static Map<String, String> controllersMap = new HashMap<String, String>();

	private static String PATH_BASE;

	private static final String PATH_PROJETO_WEB_MAVEN = "/aghu-web/src/main/java";
	
	
	
	public ControllersMapFactory(String path){
		PATH_BASE = path+"/aghu";
		carregarControllerMap();
	}

	private  void carregarControllerMap() {

		File pastaInicialControllers = new File(PATH_BASE
				+ PATH_PROJETO_WEB_MAVEN);

		try {
			processaArquivos(pastaInicialControllers);
		} catch (Exception e) {
			LOG.error(e.getCause() , e);
		}
	}

	private  void processaArquivos(File baseDir) throws IOException {
		File[] files = baseDir.listFiles();

		for (File file : files) {
			if (file.getName().endsWith("svn")) {
				continue;
			}
			if (file.isDirectory()) {
				processaArquivos(file);
			} else {
				processarArquivoClasse(file);
			}
		}
	}

	private void processarArquivoClasse(File file) throws IOException {
		if (file.getName().endsWith("Controller.java") || file.getName().endsWith("Paginator.java")){
			controllersMap.put(file.getName().replace(".java", ""), file.getAbsolutePath());
		}
		
		

	}

}
