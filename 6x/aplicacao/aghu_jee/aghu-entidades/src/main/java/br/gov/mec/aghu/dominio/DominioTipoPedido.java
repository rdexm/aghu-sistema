package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoPedido implements Dominio {
	/**
	 * P
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			// Fixme Tocchetto Identificar a descrição para a chave P
			return "P";
		default:
			return "";
		}
	}

}
