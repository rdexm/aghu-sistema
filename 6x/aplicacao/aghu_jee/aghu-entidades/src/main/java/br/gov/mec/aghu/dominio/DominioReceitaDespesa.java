package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica se um documento é receita ou despesa
 * 
 * @author agerling
 * 
 */
public enum DominioReceitaDespesa implements Dominio {
	
	/**
	 * Receita
	 */
	R,

	/**
	 * Despesa
	 */
	D;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Receita";
		case D:
			return "Despesa";
		default:
			return "";
		}
	}

}
