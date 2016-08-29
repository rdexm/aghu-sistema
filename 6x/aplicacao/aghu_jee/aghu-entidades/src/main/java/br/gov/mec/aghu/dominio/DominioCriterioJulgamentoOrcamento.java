package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o criterio de julgamento de orçamento
 * 
 * @author agerling
 * 
 */
public enum DominioCriterioJulgamentoOrcamento implements Dominio {
	
	/**
	 * Individual
	 */
	I,

	/**
	 * Global
	 */
	G;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Individual";
		case G:
			return "Global";
		default:
			return "";
		}
	}

}
