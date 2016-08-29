package br.gov.mec.controller;

import java.io.Serializable;
import java.net.UnknownHostException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.HostRemotoUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.sistema.bussiness.UserSessions;

@RequestScoped
@Named
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class LoginController implements Serializable {
	private final String CASCA_INICIO="casca-inicio";	
	
	private static final Log LOG = LogFactory.getLog(LoginController.class);
	private static final long serialVersionUID = 1430160036849757596L;
		 
	private IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	@Inject
    private BeanManager beanManager;
	
	@Inject
	private UserSessions sessions;
	
	private boolean rememberMe; 	
	private String username;	
	private String password;

	public String login() {
		String retorno = null;	
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();	
		try {
			//  #45983 evita erro caso ja exista login ativo
			if (request.getUserPrincipal() != null){
				this.logout();
			}
			
			request.login(username, password);
			beanManager.fireEvent(new EventoUsuarioLogado(username, password));
			retorno = "casca-inicio";
			LOG.info("Autenticou com sucesso usuário " + username);
			String hostName="";
			try {
				hostName = HostRemotoUtil.getHostRemoto(request).getHostName();
			} catch (UnknownHostException e) {
				LOG.debug("Erro ao resolver host name"+e.getMessage());
			}
			sessions.addSession(request.getSession().getId(), request.getRemoteUser(),request.getSession().getCreationTime(),request.getRemoteAddr(),hostName);
				
		} catch (ServletException e) {
			LOG.warn("Erro no login " + username + " " + e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Erro de autenticação. Favor verificar o nome de usuário e a senha antes de tentar novamente."));
			beanManager.fireEvent(new EventoLoginMalSucedido(username));
		}
		return retorno;
	}
	
	
	public Boolean getPossivelAlterarSenhaAghu() {
		AghParametros param = null;
		boolean possivelAlterarSenhaAghu = Boolean.TRUE;
		try {
			param = parametroFacade.obterAghParametro(AghuParametrosEnum.P_POSSIVEL_ALTERAR_SENHA_AGHU);
			if(param != null && "N".equalsIgnoreCase(param.getVlrTexto())){
				possivelAlterarSenhaAghu = Boolean.FALSE;
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada: ", e);
		}
		return possivelAlterarSenhaAghu;
	}
	
	
	public void logout() {
        FacesContext context = FacesContext.getCurrentInstance();  
        try {
        	HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            request.logout();
            context.getExternalContext().invalidateSession();
            sessions.removeSession(request.getSession().getId());
        } catch (ServletException e) {  
            context.addMessage(null, new FacesMessage("Logout failed."));  
        }         
	}
	
	public String logoutAndRedirect() {
		logout();
		return CASCA_INICIO;        
	}		
	
	public String esqueceuSenha(){
		return "esqueceuSenha";
	}
	
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getImagemFundo() {
		try {
			if(this.parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.P_AGHU_IMAGEM_FUNDO_LOGIN)) {
				return this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_IMAGEM_FUNDO_LOGIN);			
			} 			
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada: ", e);
		}
		return "";
	}
	
	public String getImagemLogo() {
		try {
			if(this.parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.P_AGHU_LOGO_LOGIN)) {
				return this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_LOGIN);			
			} 			
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada: ", e);
		}
		return "";
	}

}
