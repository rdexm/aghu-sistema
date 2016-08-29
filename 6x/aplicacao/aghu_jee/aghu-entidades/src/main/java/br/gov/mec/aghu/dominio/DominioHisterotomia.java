package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioHisterotomia implements Dominio {
	SE,
	SC,
	CO
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case SE:
			return "Segmentar";
		case SC:
			return "Segmento Corporal";
		case CO:
			return "Corporal";
		default:
			return "";
		}
	}

}
