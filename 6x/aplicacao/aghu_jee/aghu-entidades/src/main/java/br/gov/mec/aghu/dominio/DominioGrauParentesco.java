package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o grau de parentesco de uma pessoa
 * 
 * 
 */
public enum DominioGrauParentesco implements Dominio {

	/**
	 * Filho(a)
	 */
	FILHO(1),

	/**
	 * Conjuge (Esposo(a))
	 */
	CONJUGE(2),

	/**
	 * Companheiro(a)
	 */
	COMPANHEIRO(3),

	/**
	 * Outros
	 */
	OUTROS(4);

	private int value;

	private DominioGrauParentesco(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case FILHO:
			return "Filho(a)";
		case CONJUGE:
			return "Conjuge (Esposo(a))";
		case COMPANHEIRO:
			return "Companheiro(a)";
		case OUTROS:
			return "Outros";
		default:
			return "";
		}
	}

}
