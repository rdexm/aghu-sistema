package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio para tipos de dados do sisreg 
 */
public enum DominioTipoSisreg implements Dominio {
	/**
	 * Exame
	 */
	E,
	/**
	 * Consulta
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Exame";
		case C:
			return "Consulta";
		default:
			return "";
		}
	}

}