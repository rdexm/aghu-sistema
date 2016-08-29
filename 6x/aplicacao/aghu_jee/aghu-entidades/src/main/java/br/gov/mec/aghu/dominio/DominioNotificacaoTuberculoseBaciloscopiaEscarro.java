package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseBaciloscopiaEscarro implements Dominio {
	//

	/**
	 * Não Realizada
	 */
	NAO_REALIZADA(3),
	/**
	 * Negativa
	 */
	NEGATIVA(2),
	/**
	 * Positiva
	 */
	POSITIVA(1);

	private int value;

	private DominioNotificacaoTuberculoseBaciloscopiaEscarro(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO_REALIZADA:
			return "Não Realizada";
		case NEGATIVA:
			return "Negativa";
		case POSITIVA:
			return "Positiva";
		default:
			return "";
		}
	}

}