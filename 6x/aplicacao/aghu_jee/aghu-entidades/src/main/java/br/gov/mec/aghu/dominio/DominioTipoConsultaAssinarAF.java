package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoConsultaAssinarAF implements Dominio {
	/**
	 * Autorizações de Fornecimentos
	 */
	AF,
	/**
	 * Pedidos
	 */
	AFP,
	/**
	 * Todos
	 */
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AF:
			return "Autorizações de Fornecimentos";
		case AFP:
			return "Pedidos";
		case T:
			return "Todos";
		default:
			return "";
		}
	}
}
