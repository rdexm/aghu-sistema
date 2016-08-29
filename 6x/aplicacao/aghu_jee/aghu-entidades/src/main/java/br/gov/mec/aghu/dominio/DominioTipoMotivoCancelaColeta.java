package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMotivoCancelaColeta implements Dominio {

	/**
	 * Solicitante
	 */
	S, 
	
	/**
	 * Banco de Sangue
	 */
	B,
	
	/**
	 * Alta
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Solicitante";
		case B:
			return "Banco de Sangue";
		case A:
			return "Alta";
		default:
			return "";
		}
	}

}
