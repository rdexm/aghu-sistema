package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoDadosAltaPaciente implements Dominio {
	/**
	 * Manual
	 */
	M, 
	/**
	 * Prescrição Médica
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Manual";
		case P:
			return "Prescrição Médica";
		default:
			return "";
		}
	}

}