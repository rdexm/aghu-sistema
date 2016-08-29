package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoRegistro implements Dominio {
	/**
	 * Em elaboração
	 */
	EE, 
	/**
	 * Pendente
	 */
	PE, 
	/**
	 * Validado
	 */
	VA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}
}
