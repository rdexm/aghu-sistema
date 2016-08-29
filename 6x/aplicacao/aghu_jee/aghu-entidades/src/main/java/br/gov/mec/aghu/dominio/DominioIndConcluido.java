package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioIndConcluido implements Dominio {
	/**
	 * Concluída
	 */
	S,
	/**
	 * Pendente
	 */
	N,
	/**
	 * 
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Concluída";
		case N:
			return "Pendente";
		case R:
			return "R";
		default:
			return "";
		}
	}

}
