package br.gov.mec.aghu.casca.autenticacao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Este LoginModule autentica usuários usando uma connecção de banco de dados.
 * 
 * @author geraldo
 * 
 */
public class AghuDBConnectionLoginModule extends AghuBaseLoginModule {

	private static final Log LOG = LogFactory
			.getLog(AghuDBConnectionLoginModule.class);

	// Constante para string correspondente ao valor boolean true.
	protected static final String LDAP_BOOLEAN_TRUE = "TRUE";

	private String loginDatasource;

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

		super.initialize(subject, callbackHandler, sharedState, options);

		loginDatasource = (String) options.get("loginDatasource");

	}

	
	public void efetuarAutenticacao() throws LoginException {		

		Connection connection = null;

		try {
			InitialContext context = new InitialContext();

			DataSource dataSource = (DataSource) context
					.lookup(loginDatasource);
			// Abre e fecha a conexao apenas para testar usuario e senha
			connection = dataSource.getConnection(username, password);	

		} catch (NamingException e) {
			LOG.error(String.format("Erro ao encontrar datasource %s",
					loginDatasource));
			throw new LoginException(String.format(
					"Erro ao encontrar datasource %s", loginDatasource));
		} catch (SQLException e) {
			LOG.error(String.format(
					"Erro ao conectar no banco com o usuario %s. Erro: %s",
					username, e.getMessage()));
			throw new FailedLoginException(String.format(
					"Erro ao conectar no banco com o usuario %s. Erro: %s",
					username, e.getMessage()));
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				LOG.error(String.format(
						"Erro ao fechar conexão com o banco: %s",
						e.getMessage()));
			}
		}

		

	}

}