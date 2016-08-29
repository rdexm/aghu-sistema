/**
 * 
 */
package br.gov.mec.casca.app.business;

import java.net.URLEncoder;

import org.jboss.seam.Component;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.security.Identity;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.parametros.Parametros;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.seam.business.SeamContextsManager;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;
import br.gov.mec.seam.security.TokenIdentity;
import br.gov.mec.seam.util.email.EmailUtil;

/**
 * @author marcelofilho
 *
 */
class SenhaON extends SeamContextsManager {
	
	protected enum SenhaONExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_LOGIN_INEXISTENTE,
		CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO,
		CASCA_MENSAGEM_CPF_NAO_CADASTRADO,
		CASCA_MENSAGEM_CPF_NAO_CONFERE,
		CASCA_MENSAGEM_PARAMETRO_EMAIL_NAO_CADASTRADO,
		CASCA_MENSAGEM_ERRO_GERAR_TOKEN,
		CASCA_MENSAGEM_CORPO_EMAIL_NAO_CADASTRADO;
	}
	
	/**
	 * Busca usuario por login, gera um token para autentica-lo no sistema e envia o token
	 * por email, para que o usuario use esse token para alterar sua senha
	 * 
	 * @param login
	 * @param url
	 *  
	 */
	void enviarTokenSenhaUsuario(String login, String url) throws CascaException {

		UsuarioON usuarioON = getUsuarioON();
		
		Usuario usuario = usuarioON.recuperarUsuario(login);
		if(usuario == null) {
			throw new CascaException(SenhaONExceptionCode.CASCA_MENSAGEM_LOGIN_INEXISTENTE);
		}

		if(usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
			throw new CascaException(SenhaONExceptionCode.CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO);
		}
		
		try {
			String token = getTokenIdentity().createToken();
			token = URLEncoder.encode(token, "UTF-8");
			String separator = "?";
			if(url.contains(separator)){
				separator = "&";
			}
			url = url + separator + "tkn=" + token;
		
			enviaEmail(usuario.getEmail(), usuario.getLogin(), url);
		} catch (Exception e) {
			throw new CascaException(SenhaONExceptionCode.CASCA_MENSAGEM_ERRO_GERAR_TOKEN, e);
		}
	}

	/**
	 * Envia email com a senha para o email informado
	 * 
	 * @param email
	 * @param senha
	 * @throws CascaException 
	 */
	private void enviaEmail(String email, String login, String url) throws CascaException {
		Parametros parametros = this.obterDoContexto(Parametros.class);
		String from = parametros.getEmail();
		
		if (from == null) {
			throw new CascaException(SenhaONExceptionCode.CASCA_MENSAGEM_PARAMETRO_EMAIL_NAO_CADASTRADO, "P_AGHU_EMAIL_ENVIO");
		}

		String corpo = SeamResourceBundle.getBundle().getString("CASCA_MENSAGEM_CORPO_EMAIL_SENHA");
		if(corpo != null) {
			corpo = Interpolator.instance().interpolate(corpo, login, url);
		} else {
			throw new CascaException(SenhaONExceptionCode.CASCA_MENSAGEM_CORPO_EMAIL_NAO_CADASTRADO);
		}
		String titulo = SeamResourceBundle.getBundle().getString("CASCA_MENSAGEM_TITULO_EMAIL_SENHA");
	
		getEmailUtil().enviaEmail(from, email.toLowerCase(), null, titulo,
				corpo);
	}

	public UsuarioON getUsuarioON() {
		return new UsuarioON();
	}
	
	public EmailUtil getEmailUtil() {
		return super.obterDoContexto(EmailUtil.class);
	}
	
// FIXME Buscar de outro lugar que nao seja o AGH_PARAMETROS
//	public AghParametrosON getAghParametrosON() {
//		return super.obterDoContexto(AghParametrosON.class);
//	}
	
	public Identity getIdentity() {
		return super.obterDoContexto(Identity.class);
	}
	
	public TokenIdentity getTokenIdentity() {
		return super.obterDoContexto(TokenIdentity.class);
	}
	
}