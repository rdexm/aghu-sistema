package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author tfelini
 *
 */

public enum DominioAnamneseEvolucao implements Dominio {
	
	/**
	 * Anamnese
	 */
	A, 
	/**
	 * Evolucao
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Anamnese";
		case E:
			return "Evolução";
		default:
			return "";
		}
	}
	
}
