package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPrioridadeFonteRecurso implements Dominio{
	
	PRIMARIO(1),
	SECUNDARIO(2),
	TERCEARIO(3),
	QUATERNARIO(4),
	QUINARIO(5),
	SENARIO(6);

	private int value;
	
	private DominioPrioridadeFonteRecurso(int value){
		this.value = value;
	}
	
	@Override
	public int getCodigo() {		
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PRIMARIO:
			return "1";
		case SECUNDARIO:
			return "2";
		case TERCEARIO:
			return "3";
		case QUATERNARIO:
			return "4";
		case QUINARIO:
			return "5";
		case SENARIO:
			return "6";
		default:
			return "";
		}
	}

}
