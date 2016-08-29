package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;
/*
 * 
 */
		
public enum DominioIndTerminoNotificacoes implements Dominio {
	ST, 
	
	TE,
	
	TA;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ST:
			return "Sem data término";
		case TE:
			return "Termina em";
		case TA:
			return "Termina após";
		default:
			return "";
		}
	}

}
