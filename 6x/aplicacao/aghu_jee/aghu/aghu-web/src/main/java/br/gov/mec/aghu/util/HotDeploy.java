package br.gov.mec.aghu.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * Autor: Rogério Vieira
 * 
 * Instruções de Uso:
 * 
 * 1 Entre em Run Configurations (Eclipse)
 * 2 Crie um novo Java Application
 * 3 Main -> Project: aghu-web
 * 4 Main -> Main Class: br.gov.mec.aghu.util.HotDeploy
 * 5 Arguments -> Primeiro Argumento: Endereço do WildFly (Required)
 * 				  Próximos: Coloque os arquivos para exportar ex: pages\casca\permissao\pesquisarPermissoes.xhtml
 * 				Utiliza -- para comentar o nome dos arquivos
 */

public class HotDeploy {
	
	private static final Log LOG = LogFactory.getLog(HotDeploy.class);
	private static String path_jboss = null; 
	
	private static String path_xhtml = "aghu-web"+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator;
	private static String path_tmp_vsf = File.separator+"standalone"+File.separator+"tmp"+File.separator+"vfs"+File.separator+"deployment"+File.separator;

	@SuppressWarnings("PMD")
	public static void main(String[] args) throws IOException {
		
		if (args.length>0 && (args[0].contains("wildfly") || args[0].contains("jboss"))){
			path_jboss=args[0];
		}
		
		// Ajusta o caminho local
		String pathLocal = HotDeploy.class.getResource("").getPath();
		if (StringUtils.isNotBlank(pathLocal)) {
			pathLocal = pathLocal.substring(0, pathLocal.indexOf("aghu-web"));
			path_xhtml = pathLocal + path_xhtml;
		}
		LOG.info("Path local: "+pathLocal);
		
		//Verifica qual o último deployment
		File pastaVfs = new File(path_jboss + path_tmp_vsf);	
		if (!pastaVfs.exists()){
			LOG.warn("Orgiem do Wildfly não existe no sistema: " + path_jboss + path_tmp_vsf);
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");	
		File ultimoDeployment = null;
		for (File arquivo : pastaVfs.listFiles()) {
			if(arquivo.isDirectory() && arquivo.getName().startsWith("deployment")){
				LOG.info(arquivo.getName() +" = "+  sdf.format(arquivo.lastModified()));
				if(ultimoDeployment == null || ultimoDeployment.lastModified() < arquivo.lastModified()){
					ultimoDeployment = arquivo;
				}
			}
		}
		LOG.info("Último modificado: "+ultimoDeployment.getName() +" = "+  sdf.format(ultimoDeployment.lastModified()));
		
		//Procura a pasta aghu.war dentro da deployment
		File[] pastasDeployment = ultimoDeployment.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.startsWith("aghu.war");
			}
		});
		if (pastasDeployment.length==0){
			LOG.warn("Sistema não encontrou nenhuma pasta de deployment no servidor. Verifique se o Server está no ar com o deploy.");
			return;
		}
		File pastaWar = pastasDeployment[0];
		
		//Ajusta o caminho para deploy
		String path_deployment  = pastaWar.getAbsolutePath();
		LOG.info("Path deployment: "+path_deployment);
		
		
		//Copia cada um dos arquivos passado por parametro
		File arquivoLocal = null;
		File arquivoDeployment = null;
		for (String nomeArquivo : args) {
			if (!nomeArquivo.contains("wildfly") && !nomeArquivo.contains("jboss")  && !nomeArquivo.startsWith("--")){			
				if (!nomeArquivo.startsWith(File.separator)){
					nomeArquivo=File.separator.concat(nomeArquivo);
				}
				
				if (nomeArquivo.contains("/webapp")) {
					if (nomeArquivo.contains("/pages")) {
						nomeArquivo = nomeArquivo.substring(
								nomeArquivo.indexOf("/pages/"),
								nomeArquivo.length());
					}

					if (nomeArquivo.contains("/resources")) {
						nomeArquivo = nomeArquivo.substring(
								nomeArquivo.indexOf("/resources/"),
								nomeArquivo.length());
					}

					if (nomeArquivo.contains("/templates")) {
						nomeArquivo = nomeArquivo.substring(
								nomeArquivo.indexOf("/templates/"),
								nomeArquivo.length());
					}
				}
				arquivoLocal = new File(path_xhtml+nomeArquivo);
				arquivoDeployment = new File(path_deployment + nomeArquivo);
				FileUtils.writeStringToFile(arquivoDeployment, FileUtils.readFileToString(arquivoLocal), "utf-8");
				LOG.info("Copiou : "+arquivoLocal.getAbsolutePath() +"  para "+  arquivoDeployment.getAbsolutePath());
			}	
		}
		LOG.info("***** Finalizado *****");
	}
}
