package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioApgar5 implements Dominio {
	MAIOR_SETE(0),
	MENOR_SETE(18);
	
	private int value;
	
	private DominioApgar5(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case MAIOR_SETE:
				return "> 7";
			case MENOR_SETE:
				return "< 7";
			default:
				return "";
		}
	}
}
