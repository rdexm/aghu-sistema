package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de envio do contrato
 * 
 * @author agerling
 * 
 */
public enum DominioFixoDemanda implements Dominio {
	
	/**
	 * Valor Fixo
	 */
	F,

	/**
	 * Valor Sob Demanda
	 */
	D;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Valor Fixo";
		case D:
			return "Valor Sob Demanda";
		default:
			return "";
		}
	}

}
