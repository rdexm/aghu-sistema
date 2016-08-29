package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndLaringea implements Dominio {
	//
	
	
	/**
	 * Laríngea
	 */
	LARINGEA(9),
	
	/**
	 * 
	 */
	ZERO(0);
	
		
	private int value;

	private DominioNotificacaoTuberculoseIndLaringea(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LARINGEA:
			return "Laríngea";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}