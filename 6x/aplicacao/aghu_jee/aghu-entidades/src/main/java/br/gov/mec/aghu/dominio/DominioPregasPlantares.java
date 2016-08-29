package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioPregasPlantares implements DominioString {
	SEM_PREGAS("0"),
	MARCAS_MAL_DEFINIDAS("5"),
	MARCAS_BEM_DEFINIDAS("10"),
	SULCOS_METADE("15"),
	SULCOS_MAIS_METADE("20");
	
	private String value;
	
	private DominioPregasPlantares(String value) {
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
			case SEM_PREGAS:
				return "0 - Sem Pregas";
			case MARCAS_MAL_DEFINIDAS:
				return "5 - Marcas mal definidas na parte anterior da planta";
			case MARCAS_BEM_DEFINIDAS:
				return "10 - Marcas bem definidas sobre a metade anterior e sulcos no ter";
			case SULCOS_METADE:
				return "15 - Sulcos na metade anterior da planta";
			case SULCOS_MAIS_METADE:
				return "20 - Sulcos mais da metade anterior da planta";
			default:
				return "";
		}
	}
}
