package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioBloqueioNeuroeixoNvlPuncionados implements Dominio {
	P, 
	S, 
	C;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Peridural";
		case S:
			return "Subaracnóide";
		case C:
			return "Caudal";
		default:
			return "";
		}
	}

}
