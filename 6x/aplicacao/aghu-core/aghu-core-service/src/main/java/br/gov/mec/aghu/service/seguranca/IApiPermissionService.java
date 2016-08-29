package br.gov.mec.aghu.service.seguranca;

import java.io.Serializable;
import java.util.Set;

public interface IApiPermissionService extends Serializable {
	
	Boolean verificarTokenAtivo(String token); 

	Boolean verificarPerfilToken(String token, Set<String> rolesSet);
}