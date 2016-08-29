package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseSexo implements Dominio {
	
	/**
	 * Ignorado
	 */
	I,
	/**
	 * Masculino
	 */
	M,

	/**
	 * Feminino
	 */
	F;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Ignorado";
		case M:
			return "Masculino";
		case F:
			return "Feminino";
		default:
			return "";
		}
	}

}
