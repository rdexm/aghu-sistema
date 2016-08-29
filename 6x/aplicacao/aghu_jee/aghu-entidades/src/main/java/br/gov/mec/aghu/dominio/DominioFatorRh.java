package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Fator Rh de uma pessoa.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioFatorRh implements Dominio {
	
	/**
	 * Positivo
	 */
	P,

	/**
	 * Negativo
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "+";
		case N:
			return "-";
		default:
			return "";
		}
	}

}
