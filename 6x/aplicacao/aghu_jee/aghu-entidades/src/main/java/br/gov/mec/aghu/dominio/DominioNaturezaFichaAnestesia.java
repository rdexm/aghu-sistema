package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioNaturezaFichaAnestesia implements Dominio {
	/**
	 * Urgência
	 */
	URG,
	/**
	 * Eletiva
	 */
	ELE,
	/**
	 * Aproveitamento de Sala
	 */
	APR,
	/**
	 * Não
	 */
	ESP,
	/**
	 * Registro Especial
	 */
	/**
	 * Emergência
	 */
	EMG;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case URG:
			return "Urgência";
		case ELE:
			return "Eletiva";
		case APR:
			return "Aproveitamento de Sala";
		case ESP:
			return "Registro Especial";
		case EMG:
			return "Emergência";
		default:
			return "";
		}
	}


}
