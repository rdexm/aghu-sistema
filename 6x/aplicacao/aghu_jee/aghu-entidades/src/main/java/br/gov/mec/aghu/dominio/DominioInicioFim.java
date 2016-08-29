package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para indicar Início ou Fim 
 * 
 * @author rpetter
 * 
 */
public enum DominioInicioFim implements Dominio {
	
	/**
	 * Inicio
	 */
	I,

	/**
	 * Fim
	 */
	F;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Início";
		case F:
			return "Fim";
		default:
			return "";
		}
	}

}
