package br.gov.mec.aghu.core.action;


import java.io.Serializable;
import java.security.Principal;
import java.util.WeakHashMap;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.UsuarioNaoLogadoException;
import br.gov.mec.aghu.core.exceptioncode.AghuSecureInterceptorExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.seguranca.SecurityPhaseListener;

/**
 * Classe base para as Actions controllers de interface do AGHU.
 * substitui: AGHUController
 * 
 * 
 * @author Cristiano Quadros
 *
 */
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
@SessionScoped
@Named
public class SecurityController implements Serializable {

	private static final long serialVersionUID = 7199696252483597955L;
	private static final Log LOG = LogFactory.getLog(SecurityController.class);	

	
	private static final int MAX_CACHE_VALUE=1000;
	private static final boolean DISABLE_MANUAL_CACHE=false;

	private IPermissionService permissionService = ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	
	private static final WeakHashMap<String, Boolean> mapCacheSecurity = new WeakHashMap<>();	

	/**
	 * Retorna o login do usuário logado.
	 * 
	 */
	protected String obterLoginUsuarioLogado() {
		if (SecurityPhaseListener.DESABILITA_SEGURANCA){
			return "SEGURANÇA DESABILITADA";
		}
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if (principal == null){
			throw new UsuarioNaoLogadoException();
		}
		return principal.getName();
	}
	
	protected boolean isLoggedIn(){
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		return principal != null;
	}
	
	/**
	 * Este método verifica se o usuário logado tem a permissão passada por parâmetro.
	 * 
	 * Deve ser chamado nos xhtml em subistituição ao #{s:hasPermission(x,y)}
	 * @param componente
	 * @param metodo
	 * @return
	 */
	public boolean usuarioTemPermissao(String componente, String metodo){
		return usuarioTemPermissaoCache(obterLoginUsuarioLogado(), componente, metodo);
		
	}

	/**
	 * Verifica se o usuário logado possui permissão para o componente e método
	 * fornecido.
	 * 
	 * @throws AuthorizationException
	 *             quando sem permissão
	 * @param componente
	 * @param metodo
	 */
	public void verificaPermissaoUsuario(String componente, String metodo) {
		if (this.usuarioTemPermissao(componente, metodo)) {
			throw new AuthorizationException(
					AghuSecureInterceptorExceptionCode.ERRO_PERMISSAO,
					obterLoginUsuarioLogado(), componente, metodo);
		}
	}

	public void clearSecurityCache() {
		mapCacheSecurity.clear();
	}
	
	public boolean usuarioTemPermissao(String componente, String metodo, boolean cache){
		if (cache){
			return usuarioTemPermissaoCache(obterLoginUsuarioLogado(), componente, metodo);
		}else{
			return permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), componente, metodo);
		}
	}	
	
	public boolean usuarioTemPermissao(String componentemetodo){
		if (StringUtils.isEmpty(componentemetodo) || SecurityPhaseListener.DESABILITA_SEGURANCA){
			return true;
		}
		String [] cm = componentemetodo.split(",");
		return usuarioTemPermissaoCache(obterLoginUsuarioLogado(), cm[0].trim(), cm[1].trim());
	}
	
	
	private boolean usuarioTemPermissaoCache(String usuario, String componente, String metodo){
		if (SecurityPhaseListener.DESABILITA_SEGURANCA){
			return true;
		}		
		
		if (DISABLE_MANUAL_CACHE){
			return permissionService.usuarioTemPermissao(usuario, componente, metodo);
		}else{
		
			String keyCache=usuario.concat(";").concat(componente).concat(";").concat(metodo);
			if (mapCacheSecurity.containsKey(keyCache)){
				LOG.debug("Buscando Cache de Segurança...(".concat(String.valueOf(mapCacheSecurity.size())).concat(")"));
				return mapCacheSecurity.get(keyCache);
			}else{
				Boolean retorno=permissionService.usuarioTemPermissao(usuario, componente, metodo);
				addCache(keyCache, retorno);
				return retorno;
			}
		}	
	}
	
	private void addCache(String key, Boolean value){
		if (mapCacheSecurity.size()<MAX_CACHE_VALUE){
			mapCacheSecurity.put(key, value);
		}	
	}
}