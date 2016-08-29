package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author Rafael Nascimento
 * 
 */
public enum DominioUnidadeIdade implements Dominio {

	/**
	 * Anos.
	 */
	A,
	/**
	 * Meses.
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Anos";
		case M:
			return "Meses";
		default:
			return "";
		}
	}

}
