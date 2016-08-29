package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoResponsabilidade implements Dominio {
	/**
	 * Conta.
	 */
	C,
	/**
	 * Contato.
	 */
	T,
	
	/**
	 * Referência.
	 */
	R,
	
	/**
	 * Contato e Conta.
	 */
	CT;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Conta";
		case T:
			return "Contato";
		case R:
			return "Referência";
		case CT:
			return "Contato e Conta";
		default:
			return "";
		}
	}
}