package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndNaoExtraPulmonar implements Dominio {
	//
	
	/**
	 * Não se Aplica
	 */
	POSITIVO_9(9),
	
	/**
	 * 
	 */
	ZERO(0);
	
		
	private int value;

	private DominioNotificacaoTuberculoseIndNaoExtraPulmonar(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_9:
			return "9";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}