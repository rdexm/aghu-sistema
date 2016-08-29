package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEscala implements Dominio {
	P,//Previa
	D;//Definitiva

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case P:
			return "Prévia";
		case D:
			return "Definitiva";
		default:
			return "";
		}
	}

}
