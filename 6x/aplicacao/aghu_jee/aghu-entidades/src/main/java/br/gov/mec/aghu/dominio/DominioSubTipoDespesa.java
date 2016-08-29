package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o subtipo de uma despesa.
 * 
 * @author dansantos
 * 
 */
public enum DominioSubTipoDespesa implements Dominio {
	
	/**
	 * Construção
	 */
	C,

	/**
	 * Ampliação
	 */
	A,
	
	/**
	 * Reforma
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Construção";
		case A:
			return "Ampliação";
		case R:
			return "Reforma";
		default:
			return "";
		}
	}

}