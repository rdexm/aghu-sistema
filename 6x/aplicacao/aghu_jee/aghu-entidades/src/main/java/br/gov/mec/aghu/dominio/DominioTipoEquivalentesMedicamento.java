package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEquivalentesMedicamento implements Dominio {
	
	/**
	 * Substituto
	 */
	S,
	/**
	 * Equivalente
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Substituto";
		case E:
			return "Equivalente";
		default:
			return "";
		}
	}

}
