package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPig implements Dominio {
	NAO(0),
	SIM(12);
	
	private int value;
	
	private DominioPig(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case NAO:
				return "Não";
			case SIM:
				return "Sim";
			default:
				return "";
		}
	}
}
