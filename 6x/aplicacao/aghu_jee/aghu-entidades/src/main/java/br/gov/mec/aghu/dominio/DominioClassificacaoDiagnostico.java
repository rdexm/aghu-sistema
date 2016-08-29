package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioClassificacaoDiagnostico implements Dominio {
	/**
	 * Pós-Operatório
	 */
	POS,
	/**
	 * Pré-Operatório
	 */
	PRE
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case POS:
				return "Pós-Operatório";
			case PRE:
				return "Pré-Operatório";
			default:
				return "";
		}
	}

}
