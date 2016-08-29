package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAspectoGeral implements Dominio {
	
	/**
	 * Normal
	 */
	Norm,

	/**
	 * Sindrômico
	 */
	Sind;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Norm:
			return "Normal";
		case Sind:
			return "Sindrômico";
		default:
			return "";
		}
	}
}
