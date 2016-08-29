package br.gov.mec.casca.app.security;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.permission.Permission;

import br.gov.mec.casca.app.business.CascaFacade;
import br.gov.mec.casca.security.vo.PermissaoVO;
import br.gov.mec.seam.cache.Cached;
import br.gov.mec.seam.cache.CachedClass;
import br.gov.mec.seam.security.MECBasePermissionStore;

@Name("cascaLocalPermissionStore")
@CachedClass
/**
 * TODO Esta classe poderia ou ser apagada ou movida para o projeto
 */
public class CascaLocalPermissionStore extends MECBasePermissionStore {

	@In(required=true, create=true, value="cascaFacade")
	private CascaFacade cascaFacade;
	
	private String defaultApplication;

	@Cached(expiration = 15)
	public List<Permission> listPermissions(Object target, String action) {
		
		String[] args = getTargetArgs(target.toString(), defaultApplication);

		List<Permission> permissions = new ArrayList<Permission>();
		
		for (PermissaoVO permissao : cascaFacade.obterPermissoes(args[0], args[1], action)) {
			permissions.add(new Permission(permissao.getTarget(), permissao.getAction(), new SimplePrincipal(permissao.getLogin())));
		}
		
		return permissions;
	}

	public String getDefaultApplication() {
		return defaultApplication;
	}

	public void setDefaultApplication(String defaultApplication) {
		this.defaultApplication = defaultApplication;
	}
}
