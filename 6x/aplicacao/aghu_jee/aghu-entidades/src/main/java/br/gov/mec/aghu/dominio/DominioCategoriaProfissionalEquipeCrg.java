package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica uma categoria profissional
 * 
 * @author lcmoura
 *
 */
public enum DominioCategoriaProfissionalEquipeCrg implements Dominio {
	/**
	 * Auxiliar
	 */
	AUX, 
	
	/**
	 * Anestesista
	 */
	ANES, 
	
	/**
	 * Enfermagem
	 */
	ENF, 
	
	/**
	 * Professor
	 */
	PROF, 
	
	/**
	 * Cirurgião
	 */
	CIRG,
	
	/**
	 * Perfusionista
	 */
	PERF,
	
	/**
	 * Substituto
	 */
	SUBS;

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
			return "Cirurgião";
		case PERF:
			return "Perfusionista";
		case SUBS:
			return "Substituto";
		default:
			return "";
		}
	}
}
