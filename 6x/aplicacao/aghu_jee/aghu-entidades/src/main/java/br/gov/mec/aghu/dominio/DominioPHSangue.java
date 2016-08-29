package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPHSangue implements Dominio {
	MAIOR_QUE_SETE_VIRGULA_DEZENOVE(0),
	ENTRE_SETE_VIRGULA_DEZ_E_SETE_VIRGULA_DEZENOVE(7),
	MENOR_SETE_VIRGULA_DEZ(16);
	
	private int value;
	
	private DominioPHSangue(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case MAIOR_QUE_SETE_VIRGULA_DEZENOVE:
				return "> 7,19";
			case ENTRE_SETE_VIRGULA_DEZ_E_SETE_VIRGULA_DEZENOVE:
				return "7,10 - 7,19";
			case MENOR_SETE_VIRGULA_DEZ:
				return "< 7,10”";
			default:
				return "";
		}
	}
}
