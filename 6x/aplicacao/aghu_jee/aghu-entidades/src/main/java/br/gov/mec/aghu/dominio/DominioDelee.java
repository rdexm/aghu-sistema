package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioDelee implements Dominio{
	MENOS_QUATRO(-4),MENOS_TRES(-3),MENOS_DOIS(-2),MENOS_UM(-1),ZERO(0),UM(1), DOIS(2),TRES(3),QUATRO(4);
	
	private int value;

	private DominioDelee(int value){
		this.value = value;
	}
	
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		return String.valueOf(this.value);
	}

}
