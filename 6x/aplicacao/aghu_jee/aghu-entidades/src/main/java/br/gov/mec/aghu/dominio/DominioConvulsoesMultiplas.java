package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioConvulsoesMultiplas implements Dominio {
	SIM(0),
	NAO(19);
	
	private int value;
	
	private DominioConvulsoesMultiplas(int value) {
		this.value = value;
	}
	
	public int getCodigo() {
		return this.value;
	}
	@Override
	public String getDescricao() {
		switch (this) {
			case SIM:
				return "Sim";
			case NAO:
				return "Não";
			default:
				return "";
		}
	}
}
