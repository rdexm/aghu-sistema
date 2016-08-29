package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoOrdemDeAdministracao implements Dominio  {
	/**
	 * Sem Situação
	 */
	S,
	/**
	 * Administrar
	 */
	A,
	/**
	 * Pendente
	 */
	P,
	/**
	 * OK
	 */
	O;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sem Situação";
		case A:
			return "Administrar";
		case P:
			return "Pendente";
		case O:
			return "OK";
		default:
			return "";
		}
	}
	
}