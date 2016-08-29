package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoFormularioAlta implements Dominio {
	/**
	 * Simples
	 */
	S,
	/**
	 * Completo
	 */
	C,
	/**
	 * Internação
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}
}
