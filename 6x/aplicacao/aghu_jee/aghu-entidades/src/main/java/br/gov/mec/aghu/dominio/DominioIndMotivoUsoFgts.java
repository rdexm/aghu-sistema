package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;
/*
 * 
 */
		
public enum DominioIndMotivoUsoFgts implements Dominio {
	PH, 
	
	NM,
	
	DT;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PH:
			return "Portador de HIV";
		case NM:
			return "Portador de Neoplasia Maligna";
		case DT:
			return "Portador de Doença Terminal";
		default:
			return "";
		}
	}

}
