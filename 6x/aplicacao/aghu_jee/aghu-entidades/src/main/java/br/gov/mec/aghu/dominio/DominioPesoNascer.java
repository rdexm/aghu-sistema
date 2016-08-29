package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPesoNascer implements Dominio {
	PESO_MAIOR(0),
	PESO_MEDIO(10),
	PESO_MENOR(17);
	
	private int value;
	
	private DominioPesoNascer(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case PESO_MAIOR:
				return "> 999 g";
			case PESO_MEDIO:
				return "750 - 999 g";
			case PESO_MENOR:
				return "< 750 g";
			default:
				return "";
		}
	}
}
