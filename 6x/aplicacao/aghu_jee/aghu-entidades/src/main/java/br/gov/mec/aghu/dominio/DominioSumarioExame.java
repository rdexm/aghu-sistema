package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lalegre
 *
 */
public enum DominioSumarioExame implements Dominio {

	/**
	 * Bioquímica
	 */
	B, 
	/**
	 * EQU
	 */
	E,
	/**
	 * Gasometria
	 */
	G,
	/**
	 * Hematologia
	 */
	H,
	/**
	 * Não
	 */
	N,
	/**
	 * Sim (na máscara)
	 */
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case B:
			return "Bioquímica";
		case E:
			return "EQU";
		case G:
			return "Gasometria";
		case H:
			return "Hematologia";
		case N:
			return "Não";
		case S:
			return "Sim (na máscara)";
		default:
			return "";
		}
	}
	
}
