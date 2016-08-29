package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseBeneficiario implements Dominio {
	//

	/**
	 * Ignorado
	 */
	IGNORADO(9),
	/**
	 * Não
	 */
	NAO(2),
	/**
	 * Sim
	 */
	SIM(1);

	private int value;

	private DominioNotificacaoTuberculoseBeneficiario(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IGNORADO:
			return "Ignorado";
		case NAO:
			return "Não";
		case SIM:
			return "Sim";
		default:
			return "";
		}
	}

}