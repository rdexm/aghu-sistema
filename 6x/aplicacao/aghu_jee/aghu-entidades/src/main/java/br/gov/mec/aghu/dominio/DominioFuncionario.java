package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFuncionario implements Dominio {

	/**
	 * Funcionário
	 */
	F,
	/**
	 * Dependente
	 */
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Funcionário";
		case D:
			return "Dependente";

		default:
			return "";
		}
	}
}
