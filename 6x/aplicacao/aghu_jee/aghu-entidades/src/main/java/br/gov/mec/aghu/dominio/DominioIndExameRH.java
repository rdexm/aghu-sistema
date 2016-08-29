package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndExameRH  implements Dominio {
	
	/**
	 * Admissional
	 */
	A,
	/**
	 * Periódico
	 */
	P,
	/**
	 * Demissional
	 */
	D,
	/**
	 * Periódico/demissional
	 */
	M,
	/**
	 * Nenhum
	 */
	N;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Demissional";
		case M:
			return "Periódico/demissional";
		case P:
			return "Periódico";
		case A:
			return "Admissional";
		case N:
			return "Nenhum";
		default:
			return "";
		}
	}

}
