package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTonoMuscular implements DominioString {
	FLACIDO("0"),
	FLEXAO_EXTREMIDADES("1"),
	BOA_FLEXAO("2");
	
	private String value;
	
	private DominioTonoMuscular(String value) {
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
			case FLACIDO:
				return "Flácido";
			case FLEXAO_EXTREMIDADES:
				return "Flexão de Extremidades";
			case BOA_FLEXAO:
				return "Boa Flexão";
			default:
				return "";
		}
	}
}
