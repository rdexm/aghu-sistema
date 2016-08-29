package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author twickert
 *
 */
public enum DominioSimNaoNaoAplicavel implements Dominio {
	/**
	 * Sim
	 */
	S,
	/**
	 * Não
	 */
	N,
	/**
	 * Não Aplicável
	 */
	A;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case A:
			return "Não Aplicável";
		default:
			return "";
		}
	}


}
