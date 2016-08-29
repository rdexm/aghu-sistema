package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Estado Civil de uma pessoa 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioDuracaoCalculo implements Dominio {
	
	/**
	 * Horas
	 */
	H,

	/**
	 * Minutos
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case H:
			return "Horas";
		case M:
			return "Minutos";
		default:
			return "";
		}
	}

}
