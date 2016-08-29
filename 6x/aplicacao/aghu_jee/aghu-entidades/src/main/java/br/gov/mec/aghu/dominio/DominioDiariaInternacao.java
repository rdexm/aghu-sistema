package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiariaInternacao implements Dominio {
	/**
	 * UTI - 3 dias.
	 */
	U,
	/**
	 * UTI - + 3 dias
	 */
	M,
	
	/**
	 * Acompanhante
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U:
			return "UTI - 3 dias";
		case M:
			return "UTI - + 3 dias";
		case A:
			return "Acompanhante";
		default:
			return "";
		}
	}
}