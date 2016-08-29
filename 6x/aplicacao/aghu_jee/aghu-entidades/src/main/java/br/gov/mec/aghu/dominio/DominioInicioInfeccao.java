package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioInicioInfeccao implements Dominio {
	/**
	 * Após 72h de internação.
	 */
	A,
	/**
	 * Antes de 72h de internação.
	 */
	T,
	
	/**
	 * Não estabelecido.
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Após 72h de internação";
		case T:
			return "Antes de 72h de internação";
		case N:
			return "Não estabelecido";
		default:
			return "";
		}
	}
}