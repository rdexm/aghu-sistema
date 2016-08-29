package br.gov.mec.aghu.core.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe que encapsula os parâmetros do sistema, permitindo acesso apenas de
 * leitura.
 * 
 * @author geraldo
 * 
 */
@ApplicationScoped
@Named("parametros")
public class ParametrosSistema {

	private static final Log LOG = LogFactory.getLog(ParametrosSistema.class);

	private Properties parametros;

	@PostConstruct
	public void inicializar() {
		parametros = new Properties();

		InputStream prorpetiesStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("app-parameters.properties");
		try {
			parametros.load(prorpetiesStream);
		} catch (IOException e) {
			LOG.error("Não foi possível ler arquivo de configuração", e);
		}

	}

	/**
	 * Método usado para obter o valor de um parâmetro do sistema.
	 * 
	 * @param parametro
	 * @return
	 */
	public String getParametro(String parametro) {
		return parametros.getProperty(parametro);
	}

}
