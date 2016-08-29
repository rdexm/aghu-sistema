package br.gov.mec.aghu.casca.autenticacao;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Este LoginModule autentica usuários usando apenas negocialmente no aghu.
 * 
 * @author geraldo
 * 
 */
public class AghuApenasNegocialLoginModule extends AghuBaseLoginModule {

	private static final Log LOG = LogFactory
			.getLog(AghuApenasNegocialLoginModule.class);

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

	}

	
	public void efetuarAutenticacao() throws LoginException {		
		LOG.info("Não Faz nada aqui.");
	}

}