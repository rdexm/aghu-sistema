package br.gov.mec.aghu.casca.autenticacao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Este LoginModule autentica usuários contra um AD.
 * 
 * @author geraldo
 * 
 */
public class AghuAdLoginModule extends AghuLdapLoginModule {
	
	
	private static final Log LOG = LogFactory.getLog(AghuAdLoginModule.class);

	// dominio (configuravel)
	private String domain;

	/**
	 * Initialize this <code>LoginModule</code>.
	 * 
	 * <p>
	 * 
	 * @param subject
	 *            the <code>Subject</code> to be authenticated.
	 *            <p>
	 * 
	 * @param callbackHandler
	 *            a <code>CallbackHandler</code> for communicating with the end
	 *            user (prompting for user names and passwords, for example).
	 *            <p>
	 * 
	 * @param sharedState
	 *            shared <code>LoginModule</code> state.
	 *            <p>
	 * 
	 * @param options
	 *            options specified in the login <code>Configuration</code> for
	 *            this particular <code>LoginModule</code>.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<java.lang.String, ?> sharedState,
			Map<java.lang.String, ?> options) {		
		
		Properties parametros = new Properties();

		InputStream prorpetiesStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("app-parameters.properties");
		
		try {
			parametros.load(prorpetiesStream);
		} catch (IOException e) {
			LOG.error("Erro ao ler arquivo de configuração da aplicação", e);
		}
		
		this.domain = parametros.getProperty("ad_domain");		
		
		this.serverAddress = parametros.getProperty("ad_server_address");

		this.serverPort = Integer.valueOf(parametros.getProperty("ad_server_port"));

		this.userDNPrefix = parametros.getProperty("ldap_user_DN_prefix");

		this.userDNSuffix = parametros.getProperty("ldap_user_DN_suffix");		
		
		super.initialize(subject, callbackHandler, sharedState, options);
	}

	/**
	 * retorna o user-dn no formato username@domain
	 * 
	 * @param username
	 * @return
	 */
	protected String getUserDN(String username) {
		return username + "@" + domain;
	}

}