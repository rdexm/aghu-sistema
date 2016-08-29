package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIntensidadeDinamicaUterina implements Dominio{
	FRA,
	MED,
	FOR,
	IRR;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch(this){
		case FRA:
			return "Fraca";
		case MED:
			return "Média";
		case FOR:
			return "Forte";
		case IRR:
			return "Irregular";
		default:
			return null;
		}
	}
}
