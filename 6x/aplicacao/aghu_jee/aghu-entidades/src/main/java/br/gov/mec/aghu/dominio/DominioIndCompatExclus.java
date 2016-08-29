package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndCompatExclus implements Dominio {
	/**
	 * Procedimento realizado cobra esse item
	 */
	PCI,
	/**
	 * Item cobrado pelo procedimento
	 */
	ICP,
	/**
	 * Procedimento realizado não cobra esse item
	 */
	PNI, 
	/**
	 * Item não cobrado pelo procedimento
	 */
	INP,
	;

	@Override
	public int getCodigo() {

		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case PCI:
				return "Procedimento realizado cobra esse item";
			case ICP:
				return "Item cobrado pelo procedimento";
			case PNI:
				return "Procedimento realizado não cobra esse item";
			case INP:
				return "Item não cobrado pelo procedimento";
			default:
				return "";
		}
	}

}
