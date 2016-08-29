package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioFormaOrelha implements DominioString {
	CHATA("0"),
	PAVILHAO("8"),
	PAVILHAO_PARCIAL("16"),
	PAVILHAO_TOTAL("24");
	
	private String value;
	
	private DominioFormaOrelha(String value) {
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
			case CHATA:
				return "0 - Chata, disforme, pavilhão não encurvado";
			case PAVILHAO:
				return "8 - Pavilhão facilmente encurvado no bordo";
			case PAVILHAO_PARCIAL:
				return "16 - Pavilhão parcial encurvado em toda parte superior";
			case PAVILHAO_TOTAL:
				return "24 - Pavilão totalmente encurvado";
			default:
				return "";
		}
	}
}
