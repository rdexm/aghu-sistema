package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioEsforcoResp implements DominioString {
	AUSENTE("0"),
	CHORO_FRACO("1"),
	CHORO_FORTE("2");
	
	private String value;
	
	private DominioEsforcoResp(String value) {
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
			case CHORO_FRACO:
				return "Choro Fraco - Hipovent";
			case CHORO_FORTE:
				return "Choro Forte";
			default:
				return "";
		}
	}
}
