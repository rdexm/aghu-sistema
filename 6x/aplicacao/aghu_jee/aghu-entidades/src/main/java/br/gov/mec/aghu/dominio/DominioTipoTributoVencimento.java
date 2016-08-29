package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoTributoVencimento implements Dominio {
	

	/**
	 * Federal
	 */
	F,
	
	/**
	 * Municipal
	 */
	M,
	
	/**
	 * Previdenciário
	 */
	P,
	
	/**
	 * Contribuições
	 */
	C,
	
	/**
	 * Retenções Folha
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Federal";
		case M:
			return "Municipal";
		case P:
			return "Previdenciário";
		case C:
			return "Contribuições";
		case R:
			return "Retenções Folha";
		default:
			return "";
		}
	}
}