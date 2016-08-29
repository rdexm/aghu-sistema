/**
 * 
 */
package br.gov.mec.aghu.casca.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.ParametrosSistema;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;

/**
 * @author marcelofilho
 *
 */
@Stateless
public class SenhaON extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(SenhaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private ParametrosSistema parametrosSistema;

	@EJB
	private UsuarioON usuarioON;	

	@Inject
	private EmailUtil emailUtil;

	private static final long serialVersionUID = 1590841693403473176L;

	protected enum SenhaONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_LOGIN_INEXISTENTE,
		CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO,
		CASCA_MENSAGEM_PARAMETRO_EMAIL_NAO_CADASTRADO,
		CASCA_MENSAGEM_ERRO_GERAR_TOKEN,
		CASCA_MENSAGEM_CORPO_EMAIL_NAO_CADASTRADO;
	}
	
	
	/**
	 * Senha gerada randomicamente para o token gerado no caso do usuário que
	 * esqueceu a senha.
	 * 
	 * @return
	 */
//	private String geraSenhaRandomica() {
//		return String.valueOf(new Date().getTime());
//	}

	/**
	 * Envia email com a senha para o email informado
	 * 
	 * @param email
	 * @param senha
	 * @throws ApplicationBusinessException 
	 */
	private void enviaEmail(String email, String login, String url) throws ApplicationBusinessException {
		String from = parametrosSistema.getParametro("email");
		
		if (from == null) {
			throw new ApplicationBusinessException(SenhaONExceptionCode.CASCA_MENSAGEM_PARAMETRO_EMAIL_NAO_CADASTRADO, "P_AGHU_EMAIL_ENVIO");
		}

		String corpo = super.getResourceBundleValue("CASCA_MENSAGEM_CORPO_EMAIL_SENHA");
		if(corpo != null) {
			corpo = java.text.MessageFormat.format(corpo, login, url);
		} else {
			throw new ApplicationBusinessException(SenhaONExceptionCode.CASCA_MENSAGEM_CORPO_EMAIL_NAO_CADASTRADO);
		}
		String titulo = super.getResourceBundleValue("CASCA_MENSAGEM_TITULO_EMAIL_SENHA");
	
		getEmailUtil().enviaEmail(from, email.toLowerCase(), null, titulo,
				corpo);
	}

	public UsuarioON getUsuarioON() {
		return usuarioON;
	}
	
	public EmailUtil getEmailUtil() {
		return emailUtil;
	}
	
	
	/**
	 * Busca usuario por login, gera um token para autentica-lo no sistema e envia o token
	 * por email, para que o usuario use esse token para alterar sua senha
	 * 
	 * @param login
	 * @param url
	 *  
	 */
	public void enviarTokenSenhaUsuario(String login, String token, String url) throws ApplicationBusinessException {	
		
		Usuario usuario = usuarioON.obterUsuarioAtivo(login);
		if(usuario == null) {
			throw new ApplicationBusinessException(SenhaONExceptionCode.CASCA_MENSAGEM_LOGIN_INEXISTENTE);
		}

		if(usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
			throw new ApplicationBusinessException(SenhaONExceptionCode.CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO);
		}
		
		String separator = "?";
		if (url.contains(separator)) {
			separator = "&";
		}
		url = url + separator + "tkn=" + token;

		enviaEmail(usuario.getEmail(), usuario.getLogin(), url);
		
	}	
	
// FIXME Buscar de outro lugar que nao seja o AGH_PARAMETROS
//	public AghParametrosON getAghParametrosON() {
//		return aghParametrosON;
//	}

}