package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de uma conta
 * hospitalar sem internacao
 */
public enum DominioAtivoCancelado implements Dominio {
	
	/**
	 * Ativo
	 */
	A,
	/**
	 * Cancelado
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ativo";
		case C:
			return "Cancelado";
		default:
			return "";
		}
	}
}