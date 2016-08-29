package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseHistopatologia implements Dominio {
	//

	/**
	 * Não Realizado
	 */
	NAO_REALIZADO(5),
	/**
	 * Em Andamento
	 */
	EM_ANDAMENTO(4),
	/**
	 * Não Sugestivo de TB
	 */
	NAO_SUGESTIVO_DE_TB(3),
	/**
	 * Sugestivo de TB
	 */
	SUGESTIVO_DE_TB(2),
	/**
	 * Baar Positivo
	 */
	BAAR_POSITIVO(1);

	private int value;

	private DominioNotificacaoTuberculoseHistopatologia(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO_REALIZADO:
			return "Não Realizado";
		case EM_ANDAMENTO:
			return "Em Andamento";
		case NAO_SUGESTIVO_DE_TB:
			return "Não Sugestivo de TB";
		case SUGESTIVO_DE_TB:
			return "Sugestivo de TB";
		case BAAR_POSITIVO:
			return "Baar Positivo";
		default:
			return "";
		}
	}

}