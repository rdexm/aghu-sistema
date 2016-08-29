package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioItemConsultoriaSumarios implements Dominio {
	/**
	 * Item Conta Hospitalar
	 */
	ICH,

	/**
	 * Adiantamento Conta Hospitalar
	 */
	ACH;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ICH:
			return "Item Conta Hospitalar";
		case ACH:
			return "Adiantamento Conta Hospitalar";
		default:
			return "";
		}
	}

}
