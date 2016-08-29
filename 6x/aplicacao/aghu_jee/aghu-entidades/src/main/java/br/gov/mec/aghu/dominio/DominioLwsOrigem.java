package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lalegre
 *
 */
public enum DominioLwsOrigem implements Dominio {
	
	AGHU,
	INTERFACEAMENTO;
	
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AGHU:
			return "AGHU - Interfaceamento";		
		case INTERFACEAMENTO:
			return "Interfaceamento - AGHU";
		default:
			return "";
		}
	}
}
