package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoVersaoDocumento implements Dominio {

	/**
	 * Pendente
	 */
	P,
	/**
	 * Assinado
	 */
	A,
	/**
	 * Inativo
	 */
	I,
	/**
	 * Eliminado
	 */
	E;

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
			return "Assinado";
		case I:
			return "Inativo";
		case E:
			return "Eliminado";
		default:
			return "";
		}
	}
	
	public boolean isPendente() {
		switch (this) {
		case P:
			return true;
		default:
			return false;
		}
	}

	public boolean isAssinado() {
		switch (this) {
		case A:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isInativo() {
		switch (this) {
		case I:
			return true;
		default:
			return false;
		}
	}

	public boolean isEliminado() {
		switch (this) {
		case E:
			return true;
		default:
			return false;
		}
	}
	
	
}
