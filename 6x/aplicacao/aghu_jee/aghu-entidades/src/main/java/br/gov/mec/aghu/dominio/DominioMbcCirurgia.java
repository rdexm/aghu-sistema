package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioMbcCirurgia implements Dominio {
	
	A,
	E,
	EI,
	I,
	S,
	N,
	
	;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return null;
	}
}
