package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica uma categoria profissional para profissional que realiza
 * Procedimento Diagnostico Terapeutico (PDT).
 * 
 * @author dpacheco
 * 
 */
public enum DominioCategoriaProfissionalEquipePdt implements Dominio {
	
	/**
	 * Auxiliar
	 */
	AUX, 
	
	/**
	 * Anestesista
	 */
	ANES, 
	
	/**
	 * Executor Sedação
	 */
	ESE,	
	
	/**
	 * Enfermagem
	 */
	ENF, 
	
	/**
	 * Professor
	 */
	PROF, 
	
	/**
	 * Executor
	 */
	CIRG,
	
	/**
	 * Perfusionista
	 */
	PERF;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AUX:
			return "Auxiliar";
		case ANES:
			return "Anestesista";
		case ENF:
			return "Enfermagem";
		case PROF:
			return "Professor";
		case CIRG:
			return "Executor";
		case PERF:
			return "Perfusionista";
		case ESE:
			return "Executor Sedação";
		default:
			return "";
		}
	}
}
