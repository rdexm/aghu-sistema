package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoSolicitacaoTransferencia implements Dominio {
	/**
	 * Pendente.
	 */
	P,
	
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