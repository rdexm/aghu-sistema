package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioParecer implements Dominio{
	/**
	 * PF -PARECER FAVORAVEL
	 * PD-PARECER DESFAVORAVEL
	 * EA-EM AVALIACAO
	 */
	PF,
	PD,
	EA,
	DC,
	DP;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PF:
			return "Favorável";		
		case PD:
			return "Desfavorável";		
		case EA:
			return "Em avaliação";	
		case DC:
			return "Desacordo";		
		case DP:
			return "Dispensa Parecer";			
		default:
			return "";
		}
	}

}
