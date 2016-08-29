package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioGlandulaMamaria implements DominioString {
	NAO_PALPAVEL("0"),
	PALPAVEL_MENOR_5("5"),
	PALPAVEL_ENTRE_5_10("10"),
	PALPAVEL_MAIOR_10("15");
	
	private String value;
	
	private DominioGlandulaMamaria(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		return this.value;
	}

	public String getDescricaoCompleta() {

		switch (this) {
			case NAO_PALPAVEL:
				return "0 - Não palpável";
			case PALPAVEL_MENOR_5:
				return "5 - Palpável menor do que 5 mm";
			case PALPAVEL_ENTRE_5_10:
				return "10 - Entre 5 e 10 mm";
			case PALPAVEL_MAIOR_10:
				return "15 - Maior do que 10 mm";
			default:
				return "";
		}
	}
}
