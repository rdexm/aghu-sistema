package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioModoCobranca implements Dominio {
	/**
	 * Apresentar para pontuação
	 */
	P,
	/**
	 * Apresentar para cobrança de valor
	 */
	V;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Apresentar para pontuação";
		case V:
			return "Apresentar para cobrança de valor";
		default:
			return "";
		}
	}

}
