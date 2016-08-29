package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCustoUnitarioConfirmacaoRecebimento implements Dominio {
	/**
	 * Não há item
	 */
	N,
	/**
	 * Valores não conferem
	 */
	V,
	/**
	 * Não há item e valores não conferem
	 */
	T,
	
	/**
	 * Recebimento permitido
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não há item";
		case V:
			return "Valores não conferem";
		case T:
			return "Não há item e valores não conferem";
		case P:
			return "Recebimento permitido";
		default:
			return "";
		}
	}

}
