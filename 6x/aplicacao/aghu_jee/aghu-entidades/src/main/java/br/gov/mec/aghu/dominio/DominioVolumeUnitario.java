package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioVolumeUnitario implements Dominio {
	ZERO(0),
	CINCO(5),
	DEZOITO(18);
	
	private int value;
	
	private DominioVolumeUnitario(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case ZERO:
				return "> 0,9 ml / kg / h";
			case CINCO:
				return "0,1 - 0,9 ml / kg / h";
			case DEZOITO:
				return "< 0,1 ml / kg / h";
			default:
				return "";
		}
	}
}
