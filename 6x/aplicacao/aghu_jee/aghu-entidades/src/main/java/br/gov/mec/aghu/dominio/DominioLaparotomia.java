package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLaparotomia implements Dominio{
	P,
	M
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case P:
			return "Pfannenstiel";
		case M:
			return "Mediana";
		default:
			return "";
		}
	}

}
