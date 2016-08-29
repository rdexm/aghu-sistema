package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author tfelini
 *
 */

public enum DominioGrupoProfissionalAnamnese implements Dominio {
	
	/**
	 * Médica
	 * */
	P_GRUPO_PROFISSIONAL_ANAMNESE_MED,
	/**
	 * Enfermagem
	 * */
	P_GRUPO_PROFISSIONAL_ANAMNESE_ENF,
	/**
	 * Nutricional
	 * */
	P_GRUPO_PROFISSIONAL_ANAMNESE_NUT,
	/**
	 * Outros Profissionais de Saúde
	 */
	P_GRUPO_PROFISSIONAL_ANAMNESE_OPS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P_GRUPO_PROFISSIONAL_ANAMNESE_MED:
			return "Médica";
		case P_GRUPO_PROFISSIONAL_ANAMNESE_ENF:
			return "Enfermagem";
		case P_GRUPO_PROFISSIONAL_ANAMNESE_NUT:
			return "Nutricional";
		case P_GRUPO_PROFISSIONAL_ANAMNESE_OPS:
			return "Outros Profissionais de Saúde";			
		default:
			return "Outros Profissionais de Saúde";
		}
	}
	
}
