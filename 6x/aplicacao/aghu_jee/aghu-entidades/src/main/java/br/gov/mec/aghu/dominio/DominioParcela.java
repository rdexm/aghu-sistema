package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis formas de parcela
 * 
 * @author israel.haas
 * 
 */
public enum DominioParcela implements Dominio {

	/**
	 * Intervalo entre Parcelas
	 */
	IP, 
	
	/**
	 * Dias Específicos
	 */
	DE, 
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IP: 
			return "Intervalo entre Parcelas";
		case DE: 
			return "Dias Específicos";
		default:
			return "";
		}
	}
}
