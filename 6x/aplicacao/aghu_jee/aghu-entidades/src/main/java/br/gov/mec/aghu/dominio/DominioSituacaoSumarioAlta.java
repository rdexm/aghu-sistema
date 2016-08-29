package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoSumarioAlta implements Dominio {
	
	/**
	 * Pendente
	 */
	P,
	/**
	 * Informatizado
	 */
	I,
	/**
	 * Manual
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente";
		case I:
			return "Informatizado";
		case M:
			return "Manual";
		default:
			return "";
		}

	}

}
