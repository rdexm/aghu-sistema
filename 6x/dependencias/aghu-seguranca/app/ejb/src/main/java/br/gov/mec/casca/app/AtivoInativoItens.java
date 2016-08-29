package br.gov.mec.casca.app;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * Exporta a enumeracao de situações para ser usado nas telas do JSF
 * 
 * ao invés de factory usamos o unwrap neste caso.
 * 
 */
@Name("ativoInativoItens")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@AutoCreate
@SuppressWarnings("serial")
public class AtivoInativoItens implements Serializable {
	
	@Unwrap
	public DominioSituacao[] getAtivoInativo() {
		return DominioSituacao.values();
	}

}
