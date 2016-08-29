package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioInternacaoPrevia implements Dominio {
	/**
	 * Não.
	 */
	N,
	/**
	 * Sim alta + 14 dias.
	 */
	M,
	
	/**
	 * Sim alta - 14 dias.
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não";
		case M:
			return "Sim, com alta há mais de 14 dias";
		case E:
			return "Sim, com alta há menos de 14 dias";
		default:
			return "";
		}
	}
}