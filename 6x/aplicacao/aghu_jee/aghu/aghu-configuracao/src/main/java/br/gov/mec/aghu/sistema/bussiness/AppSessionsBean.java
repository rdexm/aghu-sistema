package br.gov.mec.aghu.sistema.bussiness;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import br.gov.mec.aghu.sistema.bussiness.UserSessions.UserSession;

@Singleton
public class AppSessionsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 168654357746542513L;

	private static UserSessions instance = new UserSessions();

	@Produces
	@ApplicationScoped
	public UserSessions getInstance() {
		return instance;
	}

	@Inject
	private UserSessions sessions;

	public Map<String, UserSession> listarUsuariosLogados() {
		return sessions.getUserSessions();
	}

}
