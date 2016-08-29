package br.gov.mec.casca.app.parametros;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("parametros")
@Scope(ScopeType.APPLICATION)
public class Parametros {

	private String email;

	public String getEmail() {
		return email;
	}

}
