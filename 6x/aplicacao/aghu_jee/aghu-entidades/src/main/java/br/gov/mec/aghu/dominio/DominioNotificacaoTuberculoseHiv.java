package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseHiv implements Dominio {
	//

	/**
	 * Não Realizada
	 */
	NAO_REALIZADA(4),
	/**
	 * Em Andamento
	 */
	EM_ANDAMENTO(3),
	/**
	 * Negativa
	 */
	NEGATIVA(2),
	/**
	 * Positiva
	 */
	POSITIVA(1);

	private int value;

	private DominioNotificacaoTuberculoseHiv(int value) {
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
		case EM_ANDAMENTO:
			return "Em Andamento";
		case NEGATIVA:
			return "Negativa";
		case POSITIVA:
			return "Positiva";
		default:
			return "";
		}
	}

}