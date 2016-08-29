package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndGestante implements Dominio {
	//

	/**
	 * Ignorado
	 */
	IGNORADO(9),
	/**
	 * Não se aplica
	 */
	NAO_SE_APLICA(6),
	/**
	 * Não
	 */
	NAO(5),
	/**
	 * Idade Gestacional Ignorada
	 */
	IDADE_GESTACIONAL_IGNORADA(4),
	/**
	 * 3º Trimestre
	 */
	TERCEIRO_TRIMESTRE(3),
	/**
	 * 2º Trimestre
	 */
	SEGUNDO_TRIMESTRE(2),
	/**
	 * 1º Trimestre
	 */
	PRIMEIRO_TRIMESTRE(1);

	private int value;

	private DominioNotificacaoTuberculoseIndGestante(int value) {
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
		case NAO_SE_APLICA:
			return "Não se aplica";
		case NAO:
			return "Não";
		case IDADE_GESTACIONAL_IGNORADA:
			return "Idade Gestacional Ignorada";
		case TERCEIRO_TRIMESTRE:
			return "3º Trimestre";
		case SEGUNDO_TRIMESTRE:
			return "2º Trimestre";
		case PRIMEIRO_TRIMESTRE:
			return "1º Trimestre";
		default:
			return "";
		}
	}

}