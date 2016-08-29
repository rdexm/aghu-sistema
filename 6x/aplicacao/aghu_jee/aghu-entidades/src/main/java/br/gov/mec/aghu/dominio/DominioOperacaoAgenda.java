package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioOperacaoAgenda implements Dominio {
	/**
	 * 
	 */
	I,
	
	/**
	 * 
	 */
	A,
	
	/**
	 * 
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Inclusão";
		case A:
			return "Alteração";
		case E:
			return "Exclusão";

		default:
			return "";
		}
	}

}
