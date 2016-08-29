package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a origem dos pacientes de cirurgias
 */
public enum DominioOrigemPacienteCirurgia implements Dominio {

	/**
	 * Ambulatório
	 */
	A,
	/**
	 * Internação
	 **/
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public String getDescricaoBanco() {
		switch (this) {
		case A:
			return "Ambulatório";
		case I:
			return "Internação";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambulatório";
		case I:
			return "Internação";
		default:
			return "";
		}
	}
	
	public String getDescricaoAbreviada() {
		switch (this) {
		case A:
			return "AMB";
		case I:
			return "INT";
		default:
			return "";
		}
	}

}
