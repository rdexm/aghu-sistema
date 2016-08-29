package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Estado Civil de uma pessoa 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioParamCalculo implements Dominio {
	
	/**
	 * Kg
	 */
	K,

	/**
	 * m2
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case K:
			return "Kg";
		case M:
			return "m2";
		default:
			return "";
		}
	}

}
