/**
 * 
 */
package br.gov.mec.aghu.core.seguranca;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Date;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.criptografia.CriptografiaUtil;
import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exceptioncode.SecurityPhaseListenerExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * @author geraldo
 * 
 *         Classe usada para validar autorização de acesso as páginas do sistema
 *         AGHU.
 * 
 */
public class SecurityPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -4899374983210765943L;
	private static final Log LOG = LogFactory.getLog(SecurityPhaseListener.class);
	public static final boolean DESABILITA_SEGURANCA=false;

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	

	@Override
	public void afterPhase(PhaseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	@SuppressWarnings("PMD")
	public void beforePhase(PhaseEvent event) {
		if (DESABILITA_SEGURANCA){
			LOG.info("** VERIFICAÇÃO SEGURANÇA DO AGHU DESABILITADA - SecurityPhaseListener **");
			return;
		}		
		FacesContext context = event.getFacesContext();		
		Principal principal = context.getExternalContext().getUserPrincipal();
		if (!context.isPostback() && !context.isValidationFailed() && (!context.getPartialViewContext().isAjaxRequest() || !context.getPartialViewContext().isRenderAll())){
			UIViewRoot view = event.getFacesContext().getViewRoot();
			if (view==null){
				return;
			}
			String usuario = null;
			boolean autenticacaoViatoken = false;
			
			if (principal == null) {
				try {
					usuario = processarToken((HttpServletRequest) event.getFacesContext().getExternalContext().getRequest());
					if (usuario != null) {
						autenticacaoViatoken = true;
					}
				} catch (TokenIdentityException | UnsupportedEncodingException e) {
					LOG.error("Erro na geração do token", e);
				}
			} else {
				usuario = principal.getName();
			}
			
			if (autenticacaoViatoken){
				try {
					ExternalContext contextoExterno = context.getExternalContext();
					HttpServletRequest request = (HttpServletRequest) contextoExterno.getRequest();					
					contextoExterno.dispatch(((String)request.getAttribute("javax.servlet.forward.servlet_path")) + "?" + request.getQueryString());
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
			
			String viewID = view.getViewId();
			if (!StringUtils.startsWith(viewID, "/pages")
					|| StringUtils.endsWith(viewID, "/relatorioSumarioAltaAtendEmergenciaPdf.xhtml")) {
				return; // Não valida permissão para a página de login nem p/ tela
						// de erro de permissão nem p/ tela de requisitar senha
						// (esqueci minha senha) nem p/ tela de limite máximo de
						// tentativas de acesso por minuto.
			}
			if (StringUtils.endsWith(viewID, "/trocarSenha.xhtml")	&& autenticacaoViatoken) {
				return; // não valida permissão para a tela de troca de senha caso a
						// autenticação tenha sido efetuada via (veio do link do
						// email de esqueci minha senha)
			}
			if (StringUtils.endsWith(viewID, "/home.xhtml")) {  
				return; // Páginas do Casca
			}			
			if (usuario == null) {
				throw new AuthorizationException(SecurityPhaseListenerExceptionCode.USUARIO_NAO_LOGADO);
			}
			if (!getPermissionService().usuarioTemPermissao(usuario, StringUtils.removeStart(viewID, "/pages"), "render")) {
				throw new AuthorizationException(SecurityPhaseListenerExceptionCode.ERRO_PERMISSAO, usuario, viewID, "render");
			}
			
			
		}	
	}

	private String processarToken(HttpServletRequest request)
			throws TokenIdentityException, UnsupportedEncodingException {
		String usuario = null;
		String tkn = request.getParameter("tkn");
		if (tkn != null) {
		//	tkn = CoreUtil.decodeURL(tkn);
			if (LOG.isInfoEnabled()) {
				LOG.info(String.format("Realizando autenticacao por token: %s",
						tkn));
			}
			Token token = Token.createToken(tkn);
			if (LOG.isDebugEnabled()) {
				LOG.debug(String
						.format("Token criado em %s com expiracao em %s. Hora local: %s. Timezone local: %s",
								token.getDate().toString(), token
										.getDataExpiracao().toString(),
								new Date().toString(), System
										.getProperty("user.timezone")));
			}
			if (token.isValid()) {
				try {
					request.login(token.getUsername(), CriptografiaUtil
							.descriptografar(token.getEncryptedPassword()));
					usuario = token.getUsername();
				} catch (ServletException e) {
					throw new TokenIdentityException(
							"não foi possível autentucar usuário via token", e);
				}
			} else {
				if (LOG.isWarnEnabled()) {
					LOG.warn(String
							.format("Tentativa de autenticacao utilizando token invalido: %s",
									tkn));
				}
			}
		}
		return usuario;
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}