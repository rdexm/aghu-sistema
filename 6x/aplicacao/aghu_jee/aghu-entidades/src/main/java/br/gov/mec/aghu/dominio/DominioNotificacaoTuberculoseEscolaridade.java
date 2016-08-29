package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseEscolaridade implements Dominio {
	//

	/**
	 * Não se aplica
	 */
	NAO_SE_APLICA(10),
	/**
	 * Ignorado
	 */
	IGNORADO(9),
	/**
	 * Educação Superior completa
	 */
	EDUCACAO_SUPERIOR_COMPLETA(8),
	/**
	 * Educação Superior incompleta
	 */
	EDUCACAO_SUPERIOR_INCOMPLETA(7),
	/**
	 * Ensino Médio completo (antigo colegial ou 2º grau)
	 */
	ENSINO_MEDIO_COMPLETO_ANTIGO_COLEGIAL_OU_2GRAU(6),
	/**
	 * Ensino Médio imcompleto(antigo colegial ou 2º grau)
	 */
	ENSINO_MEDIO_IMCOMPLETO_ANTIGO_COLEGIAL_OU_2GRAU(5),
	/**
	 * Ensino Fundamental completo (antigo ginásio ou 1º grau)
	 */
	ENSINO_FUNDAMENTAL_COMPLETO_ANTIGO_GINASIO_OU_1GRAU(4),
	/**
	 * 5ª a 8ª série incompleta do EF(antigo ginásio ou 1º grau)
	 */
	DA_5_A_8_SERIE_INCOMPLETAD_O_EF_ANTIGO_GINASIO_OU_1GRAU(3),
	/**
	 * 4ª série completa do EF (antigo primário ou 1º grau)
	 */
	DA_4_SERIE_COMPLETA_DO_EF_ANTIGO_PRIMARIO_OU_1GRAU(2),
	/**
	 * 1ª a 4ª série incompleta do EF(antigo primário ou 1º grau)
	 */
	DA_1_A_4_SERIE_INCOMPLETA_DO_EF_ANTIGO_PRIMARIO_OU_1GRAU(1),
	/**
	 * Analfabeto
	 */
	ANALFABETO(0);

	private int value;

	private DominioNotificacaoTuberculoseEscolaridade(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO_SE_APLICA:
			return "Não se aplica";
		case IGNORADO:
			return "Ignorado";
		case EDUCACAO_SUPERIOR_COMPLETA:
			return "Educação Superior completa";
		case EDUCACAO_SUPERIOR_INCOMPLETA:
			return "Educação Superior incompleta";
		case ENSINO_MEDIO_COMPLETO_ANTIGO_COLEGIAL_OU_2GRAU:
			return " Ensino Médio completo (antigo colegial ou 2º grau)";
		case ENSINO_MEDIO_IMCOMPLETO_ANTIGO_COLEGIAL_OU_2GRAU:
			return "Ensino Médio imcompleto(antigo colegial ou 2º grau)";
		case ENSINO_FUNDAMENTAL_COMPLETO_ANTIGO_GINASIO_OU_1GRAU:
			return "Ensino Fundamental completo (antigo ginásio ou 1º grau)";
		case DA_5_A_8_SERIE_INCOMPLETAD_O_EF_ANTIGO_GINASIO_OU_1GRAU:
			return "5ª a 8ª série incompleta do EF(antigo ginásio ou 1º grau)";
		case DA_4_SERIE_COMPLETA_DO_EF_ANTIGO_PRIMARIO_OU_1GRAU:
			return "4ª série completa do EF (antigo primário ou 1º grau)";
		case DA_1_A_4_SERIE_INCOMPLETA_DO_EF_ANTIGO_PRIMARIO_OU_1GRAU:
			return "1ª a 4ª série incompleta do EF(antigo primário ou 1º grau)";
		case ANALFABETO:
			return "Analfabeto";
		default:
			return "";
		}
	}

}