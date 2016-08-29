package br.gov.mec.aghu.casca.autenticacao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Este LoginModule autentica usuários contra um AD.
 * 
 * @author geraldo
 * 
 */
public class AghuLdapLoginModule extends AghuBaseLoginModule {
	
	private static final Log LOG = LogFactory.getLog(AghuLdapLoginModule.class);

	// Constante para string correspondente ao valor boolean true.
	protected static final String LDAP_BOOLEAN_TRUE = "TRUE";

	// endereço do servidor (configuravel)
	protected String serverAddress = "localhost";

	// porta do servidor AD (configuravel)
	protected int serverPort = 389;

	protected String userDNPrefix = "uid=";

	protected String userDNSuffix = ",ou=Person,dc=acme,dc=com";

	// atributo para verificar se o usuário está ativo (configurável)
	protected String enabledAttribute = null;

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
		
		this.serverAddress = parametros.getProperty("ldap_server_address");

		this.serverPort = Integer.valueOf(parametros.getProperty("ldap_server_port"));

		this.userDNPrefix = parametros.getProperty("ldap_user_DN_prefix");

		this.userDNSuffix = parametros.getProperty("ldap_user_DN_suffix");

		super.initialize(subject, callbackHandler, sharedState, options);		

	}

	public void efetuarAutenticacao() throws LoginException {
		
		if (password == null || password.isEmpty()) {
			throw new FailedLoginException("Favor Informar Senha.");
		}

		final String securityPrincipal = getUserDN(username);

		InitialLdapContext ctx = null;
		try {
			ctx = initialiseContext(securityPrincipal, password);

			if (getEnabledAttribute() != null) {
				boolean achouAtributo = false;
				Attributes attribs = ctx.getAttributes(securityPrincipal,
						new String[] { getEnabledAttribute() });
				Attribute enabledAttrib = attribs.get(getEnabledAttribute());
				if (enabledAttrib != null) {
					for (int r = 0; r < enabledAttrib.size(); r++) {
						Object value = enabledAttrib.get(r);
						if (LDAP_BOOLEAN_TRUE.equals(value)) {
							achouAtributo = true;
						}
					}
				}
				if (!achouAtributo) {
					throw new FailedLoginException("Usuário desabilitado");
				}
			}
		} catch (AuthenticationException ex) {
			throw new FailedLoginException("Usuário ou senha incorretos");
		} catch (NamingException ex2) {
			throw new LoginException("Erro na comunicação com o AD");
		} finally { // NOPMD
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ex) {
					throw new LoginException("Erro na comunicação com o AD");
				}
			}
		}

		

	}

	/**
	 * 
	 * Inicializa o contexto de comunicação com o AD, fazendo a verificação de
	 * autenticação.
	 * 
	 * @param principal
	 * @param credentials
	 * @return
	 * @throws NamingException
	 */
	protected final InitialLdapContext initialiseContext(String principal,
			String credentials) throws NamingException {
		Properties env = new Properties();

		env.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

		String providerUrl = String.format("ldap://%s:%d", getServerAddress(),
				getServerPort());
		env.setProperty(Context.PROVIDER_URL, providerUrl);

		env.setProperty(Context.SECURITY_PRINCIPAL, principal);
		env.setProperty(Context.SECURITY_CREDENTIALS, credentials);

		InitialLdapContext ctx = new InitialLdapContext(env, null);
		return ctx;
	}

	protected String getUserDN(String username) {
		return String.format("%s%s%s", getUserDNPrefix(), username,
				getUserDNSuffix());
	}

	protected String getServerAddress() {
		return serverAddress;
	}

	protected void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	protected int getServerPort() {
		return serverPort;
	}

	protected void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	protected String getEnabledAttribute() {
		return enabledAttribute;
	}

	protected void setEnabledAttribute(String enabledAttribute) {
		this.enabledAttribute = enabledAttribute;
	}

	protected String getUserDNPrefix() {
		return userDNPrefix;
	}

	protected void setUserDNPrefix(String userDNPrefix) {
		this.userDNPrefix = userDNPrefix;
	}

	protected String getUserDNSuffix() {
		return userDNSuffix;
	}

	protected void setUserDNSuffix(String userDNSuffix) {
		this.userDNSuffix = userDNSuffix;
	}

}