package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAfEmpenhada implements Dominio {
	
	/**
	 * Pendente de Empenho
	 */
	P,
	/**
	 * Empenhada
	 */
	S,
	/**
	 * Não Empenhada
	 */
	N,
	/**
	 * Opção desativada
	 */
	T;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente de Empenho";
		case S:
			return "Empenhada";
		case N:
			return "Não Empenhada";
		case T:
			return "Opção desativada";
		default:
			return "";
		}
	}

}
