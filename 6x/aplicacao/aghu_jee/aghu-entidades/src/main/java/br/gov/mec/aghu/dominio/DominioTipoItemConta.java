package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoItemConta implements Dominio {
	/**
	 * Obrigado
	 */
	O,
	/**
	 * Realizado
	 */
	R,
	/**
	 * Demais
	 */
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "Obrigado";
		case R:
			return "Realizado";
		case D:
			return "Demais";
		default:
			return "";
		}
	}
}
