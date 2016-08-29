package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioOrdenacaoPesquisaPacComAlta implements Dominio {
	/**
	 * Unidade
	 */
	U, 
	/**
	 * Paciente
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U:
			return "Unidade";
		case P:
			return "Paciente";
		default:
			return "";
		}
	}

}