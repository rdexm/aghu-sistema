package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a tipo de garantia que será utilizada do contrato
 * 
 * @author agerling
 * 
 */
public enum DominioTipoGarantia implements Dominio {
	
	/**
	 * Caução
	 */
	C,

	/**
	 * Fiança Bancária
	 */
	F,
	
	/**
	 * Inexistente
	 */
	I,
	
	/**
	 * Seguro Garantia
	 */
	S;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Caução";
		case F:
			return "Fiança Bancária";
		case S:
			return "Seguro Garantia";
		case I:
			return "Inexistente";
		default:
			return "";
		}
	}

}
