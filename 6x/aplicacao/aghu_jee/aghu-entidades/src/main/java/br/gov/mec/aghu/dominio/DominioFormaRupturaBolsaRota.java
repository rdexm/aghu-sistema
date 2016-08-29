package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFormaRupturaBolsaRota implements Dominio {
	
	Integra, 
	Amniorrexis, 
	Ignorado, 
	Amniotomia;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Integra:
			return "Integra";
		case Amniorrexis:
			return "Amniorrexis";
		case Ignorado:
			return "Ignorado";
		case Amniotomia:
			return "Amniotomia";			
		default:
			return "";
		}
	}
	

	
	
}
