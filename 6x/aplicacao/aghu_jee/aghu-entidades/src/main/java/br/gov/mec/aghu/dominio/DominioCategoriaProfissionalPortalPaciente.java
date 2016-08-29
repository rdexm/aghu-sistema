package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCategoriaProfissionalPortalPaciente implements Dominio {

	/**
	 * Médico
	 */
	MEDICO,

	/**
	 * Enfermeiro
	 */
	ENFERMEIRO,

	/**
	 * Nutricionista
	 */
	NUTRICIONISTA,

	/**
	 * A. Social, Psico, Biol, Fono, Fisio
	 */
	ASSIST_SOCIAL,

	/**
	 * Farmacêutico, Físico
	 */
	FARMACEUTICO,

	/**
	 * Outros Profissionais
	 */
	OUTROS_PROFISSIONAIS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MEDICO:
			return "Médico";
		case ENFERMEIRO:
			return "Enfermeiro";
		case NUTRICIONISTA:
			return "Nutricionista";
		case ASSIST_SOCIAL:
			return "A. Social, Psico, Biol, Fono, Fisio";
		case FARMACEUTICO:
			return "Farmacêutico, Físico";
		case OUTROS_PROFISSIONAIS:
			return "Outros Profissionais";
		default:
			return "";
		}
	}
	
	public String obterNomeParam(){
		switch (this) {
		case MEDICO:
			return "P_CATEG_PROF_MEDICO";
		case ENFERMEIRO:
			return "P_CATEG_PROF_ENF";
		case NUTRICIONISTA:
			return "P_CATEG_PROF_NUT";
		case ASSIST_SOCIAL:
			return "P_CATEG_PROF_FIS";
		case FARMACEUTICO:
			return "P_CATEG_PROF_FAR";
		case OUTROS_PROFISSIONAIS:
			return "P_CATEG_PROF_OUTROS";
		default:
			return "";
		}
	}
}
