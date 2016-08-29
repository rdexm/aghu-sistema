package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioLinhaTratamento implements DominioString {
	/**
	 * Item pendente
	 */
	PRIMEIRA,
	/**
	 * Alteração não validada
	 */
	SEGUNDA,
	/**
	 * Exclusão não validada
	 */
	TERCEIRA,
	/**
	 * Item válido
	 */
	NAO_SE_APLICA;
	
	
	
	
	@Override
	public String getCodigo() {
		switch (this) {
		case PRIMEIRA:
			return "1";
		case SEGUNDA:
			return "2";
		case TERCEIRA:
			return "3";
		case NAO_SE_APLICA:
			return "N";
		default:
			return "";
		}

	}

	@Override
	public String getDescricao() {

		switch (this) {
		case PRIMEIRA:
			return "1ª";
		case SEGUNDA:
			return "2ª";
		case TERCEIRA:
			return "3ª";
		case NAO_SE_APLICA:
			return "Não se aplica";
		default:
			return "";
		}
	}
}
