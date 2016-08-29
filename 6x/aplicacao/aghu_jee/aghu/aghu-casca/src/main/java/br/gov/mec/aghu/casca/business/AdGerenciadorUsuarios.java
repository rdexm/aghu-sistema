package br.gov.mec.aghu.casca.business;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.exceptioncode.AdGerenciadorUsuariosExceptionCode;
import br.gov.mec.aghu.core.commons.ParametrosSistema;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;

/**
 * Classe responsável por operações de segurança relacionadas aos usuários do
 * sistema no AD.
 * 
 * @author geraldo
 * 
 */
public class AdGerenciadorUsuarios implements IGerenciadorUsuarios {

	private static final Log LOG = LogFactory
			.getLog(AdGerenciadorUsuarios.class);

	
	// Constante para string correspondente ao valor boolean true.
	private static final String LDAP_BOOLEAN_TRUE = "TRUE";

	private String bindDN = "cn=Manager,dc=acme,dc=com"; //"aghuldap@hcpa"

	private String bindCredentials = "secret"; // "asclepios@2011"

	private String serverAddress = "localhost"; // omega

	private int serverPort = 389; //  3268

	private String userNameAttribute = "sAMAccountName"; //  sAMAccountName

	private int searchScope = SearchControls.SUBTREE_SCOPE;

	private String[] userObjectClasses = { "person", "uidObject" } ;//{ "organizationalPerson" }

	private String objectClassAttribute = "objectClass";

	private String userContextDN = "ou=Person,dc=acme,dc=com";  // "DC=hcpa";

	private String[] userObjectCategories;

	private String objectCategoryAttribute = "objectCategory";

	private String userDNPrefix = "uid="; //"cn="

	private String userDNSuffix = ",ou=Person,dc=acme,dc=com"; // ",dc=aghu"

	/**
	 * Time limit for LDAP searches, in milliseconds
	 */
	private int searchTimeLimit = 10000;

	private String enabledAttribute = null;

	private String domain = null; //hcpa
	
	@Inject
	private ParametrosSistema parametros;
	
	@PostConstruct
	public void iniciatizar() {
		
		initAdBindDN();		
		initBindCredentials();		
		initServerAddress();		
		initServerPort();		
		initUserNameAttribute();	
		initUserObjectClasses();		
		initObjectClassAttribute();		
		initUserContextDN();		
		initUserObjectCategories();		
		initObjectCategoryAttribute();		
		String parametroUserDNPrefix= parametros.getParametro("ldap_user_DN_prefix");
		if (parametroUserDNPrefix != null && !parametroUserDNPrefix.isEmpty()) {
			this.userDNPrefix =  parametroUserDNPrefix;
		}		
		String parametrouUserDNSuffix= parametros.getParametro("ldap_user_DN_suffix");
		if (parametrouUserDNSuffix != null && !parametrouUserDNSuffix.isEmpty()) {
			this.userDNSuffix =  parametrouUserDNSuffix;
		}		
		String parametroEnabledAttribute= parametros.getParametro("ldap_enabled_attribute");
		if (parametroEnabledAttribute != null && !parametroEnabledAttribute.isEmpty()) {
			this.enabledAttribute =  parametroEnabledAttribute;
		}		
		String parametroDomain= parametros.getParametro("ad_domain");	
		if (parametroDomain != null && !parametroDomain.isEmpty()) {
			this.domain =  parametroDomain;
		}	
	}

	private void initObjectCategoryAttribute() {
		String parametroObjectCategoryAttribute= parametros.getParametro("ad_object_category_attribute");	
		if (parametroObjectCategoryAttribute != null && !parametroObjectCategoryAttribute.isEmpty()) {
			this.objectCategoryAttribute =  parametroObjectCategoryAttribute;
		}
	}

	private void initUserObjectCategories() {
		String parametroUserObjectCategories= parametros.getParametro("ad_user_object_categories");	
		if (parametroUserObjectCategories != null && !parametroUserObjectCategories.isEmpty()) {
			this.userObjectCategories =  parametroUserObjectCategories.replaceAll(" ", "").split(",");
		}
	}

	private void initUserContextDN() {
		String parametroUserContextDN= parametros.getParametro("ad_user_context_DN");	
		if (parametroUserContextDN != null && !parametroUserContextDN.isEmpty()) {
			this.userContextDN =  parametroUserContextDN;
		}
	}

	private void initObjectClassAttribute() {
		String parametroObjectClassAttribute= parametros.getParametro("ad_object_class_attribute");	
		if (parametroObjectClassAttribute != null && !parametroObjectClassAttribute.isEmpty()) {
			this.objectClassAttribute =  parametroObjectClassAttribute;
		}
	}

	private void initUserObjectClasses() {
		String parametroUserObjectClasses= parametros.getParametro("ad_user_object_classes");
		if (parametroUserObjectClasses != null && !parametroUserObjectClasses.isEmpty()) {
			this.userObjectClasses =  parametroUserObjectClasses.replaceAll(" ", "").split(",") ;
		}
	}

	private void initUserNameAttribute() {
		String parametroUserNameAttribute= parametros.getParametro("ad_user_name_attribute");
		if (parametroUserNameAttribute != null && !parametroUserNameAttribute.isEmpty()) {
			this.userNameAttribute = parametroUserNameAttribute;
		}
	}

	private void initServerPort() {
		String parametroServerPort= parametros.getParametro("ad_server_port");	
		if (parametroServerPort != null && !parametroServerPort.isEmpty()) {
			this.serverPort = Integer.parseInt(parametroServerPort);
		}
	}

	private void initServerAddress() {
		String parametroServerAddress= parametros.getParametro("ad_server_address");
		if (parametroServerAddress != null && !parametroServerAddress.isEmpty()) {
			this.serverAddress = parametroServerAddress;
		}
	}

	private void initBindCredentials() {
		String parametroBindCredentials= parametros.getParametro("ad_bind_credentials");
		if (parametroBindCredentials != null && !parametroBindCredentials.isEmpty()) {
			this.bindCredentials = parametroBindCredentials;
		}
	}

	private void initAdBindDN() {
		String parametroAdBindDN = parametros.getParametro("ad_bind_DN");	
		if (parametroAdBindDN != null && !parametroAdBindDN.isEmpty()) {
			this.bindDN = parametroAdBindDN;
		}
	}

	@Override
	public List<String> listarLoginsRegistrados(String filtro) {
		List<String> users = this.listUsers(filtro);

		Collections.sort(users, new Comparator<String>() {
			public int compare(String value1, String value2) {
				return value1.compareTo(value2);
			}
		});

		return users;
	}

	/**
	 * Metodo adaptado do LdapIdentityStore. Foi feita uma alteração no filtro
	 * de pesquisa, para incluir também o nome do usuario na String de pesquisa,
	 * para que os resultados vindos do AD ja venham filtrados. No algorítimo
	 * original, todos os usuários eram recuperados e o filtro era feito dentro
	 * do método.
	 * 
	 * Provavelmente o filtro era feito dentro do método para tratar caracteres
	 * maiúsculos e minúsculos. Mas como no AD a pesquisa é case insensitive,
	 * isto não é necessário.
	 * 
	 * Este novo método resolve o problema de limite máximo de registros que
	 * acontecia no algoritmo original do LdapIdentityStore
	 * 
	 * @see LdapIdentityStore#listUsers(String)
	 */
	protected List<String> listUsers(String filter) {
		List<String> users = new ArrayList<String>();

		InitialLdapContext ctx = null;
		try {
			ctx = initialiseContext(getBindDN(), getBindCredentials());

			if (LOG.isDebugEnabled()) {
				LOG.debug("Listando os usuários para o filtro: " + filter);
			}

			String[] userAttr = { getUserNameAttribute() };

			SearchControls controls = new SearchControls();
			// FIXME Scope forcado. Deveria vir dos parametros em components.xml
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(userAttr);
			controls.setTimeLimit(getSearchTimeLimit());

			StringBuilder userFilter = new StringBuilder("(&");

			for (int i = 0; i < getUserObjectClasses().length; i++) {
				userFilter.append(String.format("(%s=%s)",
						getObjectClassAttribute(), getUserObjectClasses()[i]));
			}
			
			if (getUserObjectCategories() != null) {
				for (int i = 0; i < getUserObjectCategories().length; i++) {
					userFilter.append(String.format("(%s=%s)",
							getUserObjectCategories()[i]));
				}
			}

			filter = StringUtils.isBlank(filter) ? "*" : "*" + filter + "*";
			userFilter.append(String.format("(%s=%s)", getUserNameAttribute(),filter)).append(')');

			NamingEnumeration answer = ctx.search(getUserContextDN(),
					userFilter.toString(), controls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				Attribute user = attrs.get(getUserNameAttribute());

				users.add(user.get().toString());
			}

			answer.close();

			return users;

			// Se o limite for atingido, retornar os ja recuperados.
		} catch (SizeLimitExceededException e) {
			LOG.error("Limite de dados consultados excedido, retornando quantidade de dados permitidos ("
					+ e.getExplanation() + ").");
			return users;
		} catch (NamingException e) {
			throw new BaseRuntimeException(
					AdGerenciadorUsuariosExceptionCode.ERRO_LISTAR_USUARIOS, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Retornando [" + users.size()
						+ "] do Active Directory.");
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ex) {
					LOG.error("Erro ao tentar fechar contexto: ", ex);
				}
			}
		}
	}

	protected InitialLdapContext initialiseContext(String principal,
			String credentials) throws NamingException {
		Properties env = new Properties();

		env.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

		String providerUrl = String.format("ldap://%s:%d", serverAddress,
				serverPort);
		env.setProperty(Context.PROVIDER_URL, providerUrl);

		env.setProperty(Context.SECURITY_PRINCIPAL, principal);
		env.setProperty(Context.SECURITY_CREDENTIALS, credentials);

		InitialLdapContext ctx = new InitialLdapContext(env, null);
		return ctx;
	}

	/**
	 * Altera a senha do usuário no Active Directory
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 * @see LdapIdentityStore#changePassword(String, String)
	 */
	@Override
	public void changePassword(String login, String password)
			throws ApplicationBusinessException {

		InitialLdapContext context = null;
		try {
			context = initialiseSecureContext();

			String userDN = findUserDN(context, login);

			if (userDN == null) {
				throw new ApplicationBusinessException(
						AdGerenciadorUsuariosExceptionCode.ERRO_ALTERAR_SENHA_USUARIO);
			}

			Attributes passwdAttribs = new BasicAttributes();

			passwdAttribs.put(new BasicAttribute("unicodePwd",
					("\"" + password + "\"").getBytes("UTF-16LE")));

			context.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE,
					passwdAttribs);

		} catch (NamingException e) {
			throw new ApplicationBusinessException(
					AdGerenciadorUsuariosExceptionCode.ERRO_ALTERAR_SENHA_USUARIO,
					e);
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationBusinessException(
					AdGerenciadorUsuariosExceptionCode.ERRO_ALTERAR_SENHA_USUARIO,
					e);
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException ex) {
					LOG.error("Erro ao tentar fechar contexto: ", ex);
				}
			}
		}
	}

	/**
	 * Recupera o UserDN de um usuario de acordo com o login. Como os métodos de
	 * manipulação de usuário em um LDAP e como no AD o login está armazenado no
	 * atributo sAMAccountName, é necessário que seja feita uma pesquisa para
	 * recurar o DN verdadeiro deste usuário.
	 * 
	 * @param context
	 *            DirContext já devidamente aberto
	 * @param login
	 *            Login do usuário a ser pesquisado
	 * @return o DN do usuário
	 * @throws NamingException
	 */
	protected String findUserDN(DirContext context, String login)
			throws NamingException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Executando busca pelo DN para o login [" + login + "]");
		}
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		String userFilter = String.format("(&(%s=%s))", getUserNameAttribute(),
				login);

		NamingEnumeration<SearchResult> answer = context.search(
				getUserContextDN(), userFilter, controls);

		String userDN = null;

		if (answer.hasMoreElements()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Encontrou vários elementos...");
			}
			SearchResult result = (SearchResult) answer.next();

			userDN = result.getNameInNamespace();
		}

		answer.close();

		return userDN;

	}

	/**
	 * Inicializa um contexto seguro. O método de inicialização de contexto
	 * original, da classe LDAPIdentityStore, cria apenas conexões não seguras.
	 * Como o AD necessita de uma conexão segura para algumas operações, este
	 * método é utilizado no lugar do original.
	 * 
	 * @return Um contexto que utiliza uma conexão segura.
	 * @throws NamingException
	 */
	// TODO Sobrescrever initialiseContext e so deixar o AD ser utilizado via
	// SSL?
	protected InitialLdapContext initialiseSecureContext()
			throws NamingException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Executando inicialização do contexto...");
		}
		Properties env = new Properties();

		env.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

		// FIXME parametrizar porta ssl?
		Integer portaLDAP = 636;
		if (getServerPort() > 0) {
			portaLDAP = getServerPort();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Usando a porta: " + portaLDAP);
			}
		}
		String providerUrl = String.format("ldaps://%s:%d", getServerAddress(),
				portaLDAP);
		env.setProperty(Context.PROVIDER_URL, providerUrl);

		env.setProperty(Context.SECURITY_PRINCIPAL, getBindDN());
		env.setProperty(Context.SECURITY_CREDENTIALS, getBindCredentials());
		env.setProperty(Context.SECURITY_PROTOCOL, "ssl");

		return new InitialLdapContext(env, null);
	}

	@Override
	public boolean authenticate(String username, String password)
			throws ApplicationBusinessException {
		final String securityPrincipal = getUserDN(username);

		InitialLdapContext ctx = null;
		try {
			ctx = initialiseContext(securityPrincipal, password);

			if (getEnabledAttribute() != null) {
				Attributes attribs = ctx.getAttributes(securityPrincipal,
						new String[] { getEnabledAttribute() });
				Attribute enabledAttrib = attribs.get(getEnabledAttribute());
				if (enabledAttrib != null) {
					for (int r = 0; r < enabledAttrib.size(); r++) {
						Object value = enabledAttrib.get(r);
						if (LDAP_BOOLEAN_TRUE.equals(value)){
							return true;
						}
					}
				}
				return false;
			}

			return true;
		} catch (NamingException ex) {
			throw new ApplicationBusinessException(
					AdGerenciadorUsuariosExceptionCode.ERRO_AUTENTICACAO, ex);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ex) {
					LOG.error("Erro ao feixar contexto ldap",ex);
				}
			}
		}
	}

	protected String getUserDN(String username) {
		return username + "@" + domain;
	}

	public String[] getUserObjectCategories() {
		return userObjectCategories;
	}

	public void setUserObjectCategories(String[] userObjectCategories) {
		this.userObjectCategories = userObjectCategories;
	}

	public String getObjectCategoryAttribute() {
		return objectCategoryAttribute;
	}

	public void setObjectCategoryAttribute(String objectCategoryAttribute) {
		this.objectCategoryAttribute = objectCategoryAttribute;
	}

	public String getBindDN() {
		return bindDN;
	}

	public void setBindDN(String bindDN) {
		this.bindDN = bindDN;
	}

	public String getBindCredentials() {
		return bindCredentials;
	}

	public void setBindCredentials(String bindCredentials) {
		this.bindCredentials = bindCredentials;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserNameAttribute() {
		return userNameAttribute;
	}

	public void setUserNameAttribute(String userNameAttribute) {
		this.userNameAttribute = userNameAttribute;
	}

	public int getSearchScope() {
		return searchScope;
	}

	public void setSearchScope(int searchScope) {
		this.searchScope = searchScope;
	}

	public String[] getUserObjectClasses() {
		return userObjectClasses;
	}

	public void setUserObjectClasses(String[] userObjectClasses) {
		this.userObjectClasses = userObjectClasses;
	}

	public String getObjectClassAttribute() {
		return objectClassAttribute;
	}

	public void setObjectClassAttribute(String objectClassAttribute) {
		this.objectClassAttribute = objectClassAttribute;
	}

	public String getUserContextDN() {
		return userContextDN;
	}

	public void setUserContextDN(String userContextDN) {
		this.userContextDN = userContextDN;
	}

	public int getSearchTimeLimit() {
		return searchTimeLimit;
	}

	public void setSearchTimeLimit(int searchTimeLimit) {
		this.searchTimeLimit = searchTimeLimit;
	}

	public String getUserDNPrefix() {
		return userDNPrefix;
	}

	public void setUserDNPrefix(String userDNPrefix) {
		this.userDNPrefix = userDNPrefix;
	}

	public String getUserDNSuffix() {
		return userDNSuffix;
	}

	public void setUserDNSuffix(String userDNSuffix) {
		this.userDNSuffix = userDNSuffix;
	}

	public String getEnabledAttribute() {
		return enabledAttribute;
	}

	public void setEnabledAttribute(String enabledAttribute) {
		this.enabledAttribute = enabledAttribute;
	}

}



