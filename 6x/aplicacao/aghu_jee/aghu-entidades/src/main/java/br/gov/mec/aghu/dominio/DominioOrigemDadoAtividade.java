package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemDadoAtividade implements Dominio {
    /** 
     * Manual
    */
	MN,
	/** 
	 * Nota de Sala
	 */
	NS,
	/**
	 * Cuidados de Enfermagem
	 */
	CE
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MN:
			return "Manual";
		case NS:
			return "Nota de Sala";
		case CE:
			return "Cuidados de Enfermagem";
		default:
			return "";
		}
	}

}
