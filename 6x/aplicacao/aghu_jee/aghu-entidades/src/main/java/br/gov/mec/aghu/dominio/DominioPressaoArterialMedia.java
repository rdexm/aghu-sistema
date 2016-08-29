package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPressaoArterialMedia implements Dominio {
	MAIOR_VINTE_NOVE(0),
	ENTRE_VINTE_VINTE_NOVE(9),
	MENOR_VINTE(19);
	
	private int value;
	
	private DominioPressaoArterialMedia(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
			case MAIOR_VINTE_NOVE:
				return "> 29 mmHg";
			case ENTRE_VINTE_VINTE_NOVE:
				return "20 - 29 mmHg";
			case MENOR_VINTE:
				return "< 20mmHg";
			default:
				return "";
		}
	}
}
