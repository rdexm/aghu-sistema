package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo do orçamento
 * 
 * @author heliz
 * 
 */
public enum DominioOrcamentoPrevio implements Dominio {
	
	/**
	 * Orçamento prévio liberado
	 */
	L,

	/**
	 * Normal
	 */
	N,
	/**
	 * Orçamento prévio rejeitado
	 */
	R;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case L:
			return "Orçamento prévio liberado";
		case N:
			return "Normal";
		case R:
			return "Orçamento prévio rejeitado";
		default:
			return "";
		}
	}

}
