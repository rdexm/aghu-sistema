package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Grupo Sanguineo de uma pessoa.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioGrupoSanguineo implements Dominio {
	
	/**
	 * O
	 */
	O,

	/**
	 * A
	 */
	A,
	
	/**
	 * B
	 */
	B,
	
	/**
	 * AB
	 */
	AB;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "O";
		case A:
			return "A";
		case B:
			return "B";
		case AB:
			return "AB";
		default:
			return "";
		}
	}

}
