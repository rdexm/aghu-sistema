package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndIsoniazida implements Dominio {
	//
	
	/**
	 * Não
	 */
	NAO(2),
	/**
	 * Sim
	 */
	SIM(1);
	
		
	private int value;

	private DominioNotificacaoTuberculoseIndIsoniazida(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO:
			return "2";
		case SIM:
			return "1";
		default:
			return "";
		}
	}

}