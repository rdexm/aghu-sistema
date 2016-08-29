package br.gov.mec.aghu.core.commons.seguranca;

import java.io.Serializable;

public interface IPermissionService extends Serializable {
	
	 boolean usuarioTemPermissao(String login, String componente, String metodo); 

}