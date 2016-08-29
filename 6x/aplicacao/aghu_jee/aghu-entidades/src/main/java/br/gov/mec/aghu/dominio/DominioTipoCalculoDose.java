package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Estado Civil de uma pessoa 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioTipoCalculoDose implements Dominio {
	
	/**
	 * Dose 
	 */
	U,

	/**
	 * Dia
	 */
	D,
	
	/**
	 * Minuto
	 */
	M,

	/**
	 * Hora
	 */
	H;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U:
			return "Dose";
		case D:
			return "Dia";
		case M:
			return "Minuto";
		case H:
			return "Hora";
		default:
			return "";
		}
	}

}
