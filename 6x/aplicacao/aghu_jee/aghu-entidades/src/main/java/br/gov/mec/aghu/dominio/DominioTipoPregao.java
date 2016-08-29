package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica se o Pregã é Presencial ou Eletrônico
 * 
 * @author agerling
 * 
 */
public enum DominioTipoPregao implements DominioString{
	
	/**
	 * Presencial
	 */
	P,

	/**
	 * Eletrônico
	 */
	E;
	
	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Presencial";
		case E:
			return "Eletrônico";
		default:
			return "";
		}
	}

}
