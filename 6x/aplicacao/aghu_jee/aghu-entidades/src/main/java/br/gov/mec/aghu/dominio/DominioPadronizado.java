package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPadronizado implements Dominio{
	/**
	 * Padronizados
	 */
	S,
	/**
	 * Não Padronizados 
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Padronizados";
		case N:
			return "Não Padronizados";

		default:
			return "";
		}
	}
	
	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * 
	 * @return
	 */
	public boolean isSim() {
		switch (this) {
		case S:
			return Boolean.TRUE;
		case N:
		default:
			return Boolean.FALSE;
		}
	}
}
