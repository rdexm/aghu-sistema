package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioPeriodicidadeAdm implements Dominio {
	/**
	 * Dias
	 */
	D, 
	/**
	 * Semanas
	 */
	S, 
	/**
	 * Meses
	 */
	M, 
	/**
	 * Anos
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case D: return "Dias";
			case S: return "Semanas";
			case M: return "Meses";
			case A: return "Anos";
			default: return "";
		}
	}

}
