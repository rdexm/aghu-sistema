package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseAgravoAssociado implements Dominio {
	//

	/**
	 * Ignorado
	 */
	IGNORADO(9),
	/**
	 * Outros
	 */
	OUTROS(5),
	/**
	 * Doença Mental
	 */
	DOENCA_MENTAL(4),
	/**
	 * Diabetes
	 */
	DIABETES(3),
	/**
	 * Alcoolismo
	 */
	ALCOOLISMO(2),
	/**
	 * Aids
	 */
	AIDS(1);

	private int value;

	private DominioNotificacaoTuberculoseAgravoAssociado(int value) {
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
		case OUTROS:
			return "Outros";
		case DOENCA_MENTAL:
			return "Doença Mental";
		case DIABETES:
			return "Diabetes";
		case ALCOOLISMO:
			return "Alcoolismo";
		case AIDS:
			return "Aids";
		default:
			return "";
		}
	}

}