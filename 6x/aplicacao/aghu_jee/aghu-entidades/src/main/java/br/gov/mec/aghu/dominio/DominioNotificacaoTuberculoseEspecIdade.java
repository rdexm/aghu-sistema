package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseEspecIdade implements Dominio {
	//

	/**
	 * Ano
	 */
	ANO(4),
	/**
	 * Mês
	 */
	MÊS(3),
	/**
	 * Dia
	 */
	DIA(2),
	/**
	 * Hora
	 */
	HORA(1);

	private int value;

	private DominioNotificacaoTuberculoseEspecIdade(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ANO:
			return "Ano";
		case MÊS:
			return "Mês";
		case DIA:
			return "Dia";
		case HORA:
			return "Hora";
		default:
			return "";
		}
	}

}