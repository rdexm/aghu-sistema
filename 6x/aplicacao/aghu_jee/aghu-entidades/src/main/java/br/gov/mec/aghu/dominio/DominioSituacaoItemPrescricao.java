package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoItemPrescricao implements Dominio {
	/**
	 * Item pendente
	 */
	P,
	/**
	 * Alteração não validada
	 */
	A,
	/**
	 * Exclusão não validada
	 */
	E,
	/**
	 * Item válido
	 */
	V,
	/**
	 * Protocolo não validado
	 */
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case P:
			return "Item pendente";
		case A:
			return "Alteração não validada";
		case E:
			return "Exclusão não validada";
		case V:
			return "Item válido";
		case X:
			return "Protocolo não validado";
		default:
			return "";
		}
	}
}
