package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGrupoProcedimento implements Dominio {
	/**
	 * Cirurgias
	 */
	C,

	/**
	 * Proced Esp Diverso
	 */
	D,

	/**
	 * Exames
	 */
	E,

	/**
	 * Proced Hemoterapicos
	 */
	H,
	
	/**
	 * Materiais 
	 */
	M,
	
	/**
	 * Componente Sanguineo
	 */
	S

	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Cirurgias";
		case D:
			return "Proced Esp Diverso";
		case E:
			return "Exames";
		case H:
			return "Proced Hemoterapicos";
		case M:
			return "Materiais";
		case S:
			return "Componente Sanguineo";
		default:
			return "";
		}
	}
}