package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioMoro implements Dominio {
	
	/**
	 * Presente
	 */
	Pre,

	/**
	 * Ausente
	 */
	Aus,
	
	/**
	 * Diminuido
	 */
	Dim,
	
	/**
	 * Não Realizado
	 */
	Nr;
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Pre:
			return "Presente";
		case Aus:
			return "Ausente";
		case Dim:
			return "Diminuido";
		case Nr:
			return "Não Realizado";
		default:
			return "";
		}
	}
}