package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioArea implements Dominio {
	
	/**
	 * Geral
	 */
	G,

	/**
	 * Engenharia
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Geral";
		case E:
			return "Engenharia";
		default:
			return "";
		}
	}

}
