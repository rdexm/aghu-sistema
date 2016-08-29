package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoItemPrescricaoMedicamento implements Dominio {
	
	/**
	 * 
	 */
	P,
	/**
	 * 
	 */
	A,
	/**
	 * 
	 */
	E,
	/**
	 * 
	 */
	V,
	/**
	 * 
	 */
	X;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "P";
		case A:
			return "A";
		case E:
			return "E";
		case V:
			return "V";
		case X:
			return "X";
		default:
			return "";
		}
	}

}
