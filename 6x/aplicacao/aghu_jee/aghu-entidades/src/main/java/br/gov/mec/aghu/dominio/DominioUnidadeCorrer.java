package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeCorrer implements Dominio {

	/**
	 * Hora
	 */
	H,
	/**
	 * Minuto
	 **/
	M,
	/**
	 * Segundo
	 **/
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case H:
			return "Hora";
		case M:
			return "Minuto";
		case S:
			return "Segundo";
		default:
			return "";
		}
	}

}
