package br.gov.mec.casca.security;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.management.IdentityStore;

import br.gov.mec.casca.service.CascaService;
import br.gov.mec.seam.security.MECBaseIdentityStore;

@Name("cascaRoleIdentityStore")
public class CascaRoleIdentityStore extends MECBaseIdentityStore {

	@Logger
	private transient Log log;

	@In(create = true)
	private CascaService cascaService;

	/**
	 * @see IdentityStore#getImpliedRoles(String)
	 */
	@Override
	public List<String> getImpliedRoles(String username) {
		log.info("getImpliedRoles({0})", username);

		return cascaService.obterNomePerfisPorUsuario(username);
	}
}
