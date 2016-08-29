package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioConsultaGenerica implements Dominio {
	/**
	 * Ignora
	 */
	I,
	/**
	 * Mesma
	 */
	M,
	/**
	 * Diferente
	 */
	D;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Ignora";
		case M:
			return "Mesma";
		case D:
			return "Diferente";
		default:
			return "";
		}
	}
}
