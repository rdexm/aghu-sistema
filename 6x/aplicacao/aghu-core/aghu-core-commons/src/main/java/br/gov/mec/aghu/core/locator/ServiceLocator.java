package br.gov.mec.aghu.core.locator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author rcorvalao
 *
 */
public class ServiceLocator {
	
	private static final Log LOG = LogFactory.getLog(ServiceLocator.class);
	
	private enum LookupType {
		REMOTE,
		LOCAL
	}
	
	private static Properties properties = getProps();
	
	private static Properties getProps() {		
		Properties prop = new Properties();		
		
			try {
				InputStream prorpetiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("app-parameters.properties");
				prop.load(prorpetiesStream);
			} catch (IOException e) {
				LOG.error("Não carregou arquivo de propriedades: ");
			}
			
		return prop;
	}
	
	
	/**
	 * Busca o Servico que contenha nome seguindo o padrao:<br/>
	 * 
	 * nome do EJB deve ser <i>nomeDoServico</i><br/>
	 * deve possuir uma interface com nome <b>I</b> + <i>nomeDoServico</i>.<br/> 
	 * 
	 * <b>a busca serah por servico LOCAL</b><br/>
	 * 
	 * Por exemplo: <br/>
	 * nomeDoServico: ParametroSistemaFacade <br/>
	 * interface: IParametroSistemaFacade<br/>
	 * 
	 * @param <T>
	 * @param className
	 * @return
	 */
	public static <T> T getBean(Class<T> className, String modulo) {
		String appContext = properties.getProperty("app_context");
		
		return getBean(className, LookupType.LOCAL, modulo, appContext);
	}
	
	/**
	 * Busca o Servico que contenha nome seguindo o padrao:<br/>
	 * 
	 * nome do EJB deve ser <i>nomeDoServico</i><br/>
	 * deve possuir uma interface com nome <b>I</b> + <i>nomeDoServico</i>.<br/> 
	 * 
	 * <b>a busca serah por servico REMOTE</b><br/>
	 * 
	 * Por exemplo: <br/>
	 * nomeDoServico: ParametroSistemaFacade <br/>
	 * interface: IParametroSistemaFacade<br/>
	 * 
	 * @param <T>
	 * @param className
	 * @return
	 */
	public static <T> T getBeanRemote(Class<T> className, String modulo) {
		String appContext = properties.getProperty("app_context_remote");
		
		return getBean(className, LookupType.REMOTE, modulo, appContext);
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T> T getBean(Class<T> className, LookupType type, String modulo, String appContextValue) {
		String beanName = removeFirstLetter(className);
		
		try {
			if (appContextValue == null || "".equals(appContextValue.trim())) {
				appContextValue = "aghu"; 
			}
			
			StringBuilder jndi = new StringBuilder();
			if (LookupType.REMOTE == type) {
				jndi.append("ejb:");
			} else {
				jndi.append("java:global").append("/");
			}
			jndi.append(appContextValue).append("/").append(modulo).append("/").append("/").append(beanName);
			String viewClassName = className.getName();
			jndi.append("!").append(viewClassName);

			LOG.debug("Jndi Name: " + jndi);
			
			return (T) getBusinessContext(type).lookup(jndi.toString());
		} catch (NamingException e) {
			throw new IllegalStateException("Erro ao realizar lookup para " + className, e);
		} catch (Throwable e) { //NOPMD
			LOG.error(e.getMessage(), e);
			throw new IllegalStateException("Erro ao realizar lookup para " + className, e);
		}
	}
	
	private static Context getBusinessContext(LookupType type) throws NamingException {
		Context businessContext = null;
		
		if (LookupType.LOCAL == type) {
			businessContext = new InitialContext();
		} else if (LookupType.REMOTE == type) {
			Properties props = new Properties();
			props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			businessContext = new InitialContext(props);
		}
		
		return businessContext;
	}
	
	/**
	 * Remove a primeira letra do nome da classe quando esta iniciar com <b>I</b><br/>
	 * 
	 * @param interfaceName
	 * @return
	 */
	private static <T> String removeFirstLetter(Class<T> className) {
		if (className == null) {
			throw new IllegalArgumentException("Deve ser informado uma classe para lookup.");
		}
		String interfaceName = className.getSimpleName();
		if (interfaceName == null || interfaceName.charAt(0) != 'I') {
			throw new IllegalArgumentException("Classe informada para lookup nao esta no padrao.");			
		}
		return interfaceName.substring(1);
	}

	
}
