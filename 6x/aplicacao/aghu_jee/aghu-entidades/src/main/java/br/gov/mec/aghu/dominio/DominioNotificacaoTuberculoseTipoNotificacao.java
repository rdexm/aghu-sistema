package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseTipoNotificacao implements Dominio {
	// Tipo de notificação
	/**
	 * Individual
	 */
	INDIVIDUAL(2);

	private int value;

	private DominioNotificacaoTuberculoseTipoNotificacao(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case INDIVIDUAL:
			return "Individual";
		default:
			return "";
		}
	}

}