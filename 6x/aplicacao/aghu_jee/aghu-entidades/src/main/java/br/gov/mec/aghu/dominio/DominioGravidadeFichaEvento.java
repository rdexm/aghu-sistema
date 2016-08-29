package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lsamberg
 *
 */
public enum DominioGravidadeFichaEvento implements Dominio {

	/**
	 * 
	 */
	M, 
	/**
	 * 
	 */
	A,
	/**
	 * 
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Média";
		case A:
			return "Alta";
		case P:
			return "Baixa";
		default:
			return "";
		}
	}
	
}
