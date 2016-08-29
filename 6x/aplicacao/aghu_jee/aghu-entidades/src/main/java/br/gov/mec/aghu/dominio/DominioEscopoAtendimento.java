package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica os escopo do atendimento de um médico.
 * 
 * @author tfelini
 */

public enum DominioEscopoAtendimento implements Dominio {
	/**
	 * Ambulatorial
	 */
	N1,
	/**
	 * Emergência
	 */
	N2,
	/**
	 * Internação
	 */
	N3;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N1:
			return "Ambulatorial";
		case N2:
			return "Emergência";
		case N3:
			return "Internação";

		default:
			return "";
		}
	}
	
}
