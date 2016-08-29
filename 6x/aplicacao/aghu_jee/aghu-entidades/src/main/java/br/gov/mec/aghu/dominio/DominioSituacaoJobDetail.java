package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoJobDetail implements Dominio {
	/**
	 * Agendado
	 */
	A,
	/**
	 * Executando
	 */
	E,
	/**
	 * Concluido
	 */
	C,
	/**
	 * Falha Geral
	 */
	F,
	/**
	 * Falha de Negocio
	 */
	N
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Agendado";
		case E:
			return "Executando";
		case C:
			return "Concluído";
		case F:
			return "Falha Geral";
		case N:
			return "Falha de Negócio";			
		default:
			return "";
		}
	}

	public static DominioSituacaoJobDetail getInstance(String valor) {
		if (valor != null && valor.trim().equalsIgnoreCase("A")) {
			return DominioSituacaoJobDetail.A;
		} else if (valor != null && valor.trim().equalsIgnoreCase("E")) {
			return DominioSituacaoJobDetail.E;
		} else if (valor != null && valor.trim().equalsIgnoreCase("C")) {
			return DominioSituacaoJobDetail.C;
		} else if (valor != null && valor.trim().equalsIgnoreCase("F")) {
			return DominioSituacaoJobDetail.F;
		} else if (valor != null && valor.trim().equalsIgnoreCase("N")) {
			return DominioSituacaoJobDetail.N;
		} else {
			return null;
		}
	}

}
