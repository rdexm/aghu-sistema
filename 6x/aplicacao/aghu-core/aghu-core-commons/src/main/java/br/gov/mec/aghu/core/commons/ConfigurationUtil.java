package br.gov.mec.aghu.core.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;

public class ConfigurationUtil {
	
	private static final Log LOG = LogFactory.getLog(ConfigurationUtil.class);
	
	
	private static final String MSG_JBOSS_HOME_NAO_DEFINIDA = 
			"Erro ao imprimir relatório. A variável de ambiente JBOSS_HOME não está definida.";
	
	
	public static final String PASTA_PADRAO_CONFIGURACAO = File.separator + "modules" 
			+ File.separator + "br" 
			+ File.separator + "gov" 
			+ File.separator + "mec" 
			+ File.separator + "aghu" 
			+ File.separator + "configuration" 
			+ File.separator + "main" 
			+ File.separator + "properties";
	
	
	
	public static FileInputStream carregarImagem(String imageName) throws IOException {
		verificaVariavelAmbienteJbossHome();
		
		String imagemPath = System.getenv("JBOSS_HOME") + PASTA_PADRAO_CONFIGURACAO + File.separator + imageName ;
		
		File file = new File(imagemPath);
		FileInputStream imagemLogo = new FileInputStream(file);
		return imagemLogo;
	}
	
	
	public static Image carregarImagemParaItext(String imageName) throws BadElementException, IOException {
		verificaVariavelAmbienteJbossHome();
		
		// wildfly/modules/br/gov/mec/aghu/configuration/main/properties
		String imagePath = System.getenv("JBOSS_HOME") + ConfigurationUtil.PASTA_PADRAO_CONFIGURACAO + File.separator + imageName;
        Image image = Image.getInstance(imagePath);
        
        image.scalePercent(25f);
        image.setAlignment(Image.LEFT|Image.TEXTWRAP);
		return image;
	}
	
	private static void verificaVariavelAmbienteJbossHome() throws IOException {
		if (System.getenv("JBOSS_HOME") == null) {
			LOG.error(MSG_JBOSS_HOME_NAO_DEFINIDA);
			throw new IOException(MSG_JBOSS_HOME_NAO_DEFINIDA);
		}
	}
	
	
}
