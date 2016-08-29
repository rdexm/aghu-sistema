package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de vivo ou morto para cadastro de cid por nascimento
 * 
 * @author rafael.silvestre
 */
public enum DominioVivoMorto implements Dominio {
	/**
	 * Zero
	 */
	Z,

	/**
	 * Um
	 */
	U, 

	/**
	 * Dois
	 */
	D,

	/**
	 * Maior de dois
	 */
	M,

	/**
	 * Maior de zero
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Z:
			return "Zero";
		case U:
			return "Um";
		case D:
			return "Dois";
		case M:
			return "Maior de dois";
		case A:
			return "Maior de zero";
		default:
			return "";
		}
	}
}

