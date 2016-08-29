package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioFreqCardicaca implements DominioString {
	AUSENTE("0"),
	MENOS_DE_100("1"),
	MAIS_DE_100("2");
	
	private String value;
	
	private DominioFreqCardicaca(String value) {
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
			case AUSENTE:
				return "Ausente";
			case MENOS_DE_100:
				return "Menos de 100";
			case MAIS_DE_100:
				return "Mais de 100";
			default:
				return "";
		}
	}
}
