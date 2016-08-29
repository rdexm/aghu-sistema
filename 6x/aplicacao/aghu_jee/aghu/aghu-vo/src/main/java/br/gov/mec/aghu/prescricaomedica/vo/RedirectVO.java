package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class RedirectVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4187244179656538700L;

	private String pagina;
	private Object[] parametros;
	
	
	public RedirectVO(String pagina, Object[] parametros) {
		super();
		this.pagina = pagina;
		this.parametros = parametros;
	}

	public String getPagina() {
		return pagina;
	}
	
	public void setPagina(String pagina) {
		this.pagina = pagina;
	}

	public Object[] getParametros() {
		return parametros;
	}
	
	public void setParametros(Object[] parametros) {
		this.parametros = parametros;
	}
}
