package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTipoDadoParametro implements DominioString {
	T,
	N,
	D,
	R;

	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case T:
			return "Texto";
		case N:
			return "Numérico";
		case D:
			return "Data";
		case R:
			return "R";
		default:
			return "";
		}
	}

}
