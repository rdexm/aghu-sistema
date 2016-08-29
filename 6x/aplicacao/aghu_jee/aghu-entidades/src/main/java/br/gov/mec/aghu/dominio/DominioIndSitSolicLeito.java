package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndSitSolicLeito implements Dominio {

	/**
	 * Atendida
	 */
	A,
	/**
	 * Pendente
	 */
	P,
	/**
	 * Cancelada
	 */
	C,
	/**
	 * Efetuada
	 */
	E,
	/**
	 * Liberadas
	 */
	L;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Atendida";
		case P:
			return "Pendente";
		case C:
			return "Cancelada";
		case E:
			return "Efetuada";
		case L:
			return "Liberadas";
		default:
			return "";
		}
	}

}
