package br.gov.mec.aghu.sistema.bussiness;

import java.io.Serializable;
import java.util.Map;

import br.gov.mec.aghu.sistema.bussiness.UserSessions.UserSession;

public interface ISistemaFacade extends Serializable {
	@SuppressWarnings("rawtypes")
	public void indexar(Class clazz) throws InterruptedException;
	
	public Map<String, UserSession> listarUsuariosLogados();

}