package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioFormacaoMamilo implements DominioString {
	SEM_AREOLA("0"),
	AREOLA_LISA("5"),
	AREOLA_PONTILHADA_MENOR_7_5("10"),
	AREOLA_PONTILHADA_MAIOR_7_5("15");
	
	private String value;
	
	private DominioFormacaoMamilo(String value) {
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
			case SEM_AREOLA:
				return "0 - Apenas visível sem aréola";
			case AREOLA_LISA:
				return "5 - Mamilo bem definido, aréola lisa e diâmetro menor de 7,5 mm";
			case AREOLA_PONTILHADA_MENOR_7_5:
				return "10 - Mamilo bem definido, aréola pontilhada, bordo levantado, diâmetro < 7,5 mm";
			case AREOLA_PONTILHADA_MAIOR_7_5:
				return "15 - Mamilo bem definido, aréola pontilhada, bordo levantado, diâmetro > 7,5 mm";
			default:
				return "";
		}
	}
}
