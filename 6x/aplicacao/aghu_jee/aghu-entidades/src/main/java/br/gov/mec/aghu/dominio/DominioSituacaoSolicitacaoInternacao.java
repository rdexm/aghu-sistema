package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoSolicitacaoInternacao implements Dominio {
	/**
	 * Pendente.
	 */
	P,
	/**
	 * Liberada.
	 */
	L,
	
	/**
	 * Atendida.
	 */
	A,
	
	/**
	 * Efetuada.
	 */
	E,
	
	/**
	 * Cancelada.
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente";
		case L:
			return "Liberada";
		case A:
			return "Atendida";
		case E:
			return "Efetuada";
		case C:
			return "Cancelada";
		default:
			return "";
		}
	}
}