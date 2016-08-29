package br.gov.mec.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@RequestScoped
public class LoginAction extends ActionController {
	
	
	private static final Log LOG = LogFactory.getLog(LoginAction.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1430160036849757596L;
	 
	private ICascaFacade cascaFacade = ServiceLocator.getBean(
			ICascaFacade.class, "aghu-casca");
	
	private IParametroFacade parametroFacade = ServiceLocator.getBean(
			IParametroFacade.class, "aghu-configuracao");
	
	@Inject
	private HostRemotoCache hostRemoto;
	
	@Inject
    private BeanManager beanManager;
	
	private boolean rememberMe; 
	
	private String username;
	
	private String password;
	
	@PostConstruct
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void iniciar()  {
		try {
			if (isLoggedIn()) {
				FacesContext.getCurrentInstance().getExternalContext().redirect("pages/casca/casca.jsf?faces-redirect=true");
			} else if (cascaFacade.verificarMaximoTentativasAcessoUltimoMinuto(hostRemoto.getEnderecoIPv4HostRemoto().getHostAddress())){
				FacesContext.getCurrentInstance().getExternalContext().redirect("limiteAcessos.jsf?faces-redirect=true");
			}
		} catch (ApplicationBusinessException | IOException e) {
			LOG.error("Erro no @PostConstruct LoginAction", e);
		}

	}

	public String login() {
		String retorno = null;	
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();	
		try {
			request.login(username, password);
			beanManager.fireEvent(new EventoUsuarioLogado(username, password));
			retorno = "sucessoLogin";
		} catch (ServletException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(
							"Erro de autenticação. Favor verificar o Nome de Usuário e a Senha "
									+ "antes de tentar novamente."));
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

}
