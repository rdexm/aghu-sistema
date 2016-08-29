package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoObjetoCustoCcts implements Dominio{
	
	//Principal
	P,
	
	//Colaborador
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Principal";
		case C:
			return "Colaborador";
		default:
			return "";
		}
		
	}
	
	
	

}
