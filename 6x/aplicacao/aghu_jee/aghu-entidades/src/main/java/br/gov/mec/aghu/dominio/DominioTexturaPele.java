package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTexturaPele implements DominioString {
	MUITO_FINA("0"),
	FINA_E_LISA("5"),
	GROSSA_E_DISCRETA("10"),
	GROSSA_SUPERFICIAL("15"),
	GROSSA_PROFUNDO("20");
	
	private String value;
	
	private DominioTexturaPele(String value) {
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
			case MUITO_FINA:
				return "0 - Muito Fina";
			case FINA_E_LISA:
				return "5 - Fina e Lisa";
			case GROSSA_E_DISCRETA:
				return "10 - Algo mais grossa e discreta, descamação superficial";
			case GROSSA_SUPERFICIAL:
				return "15 - Grossa - Sulcos superficiais, descamação de pés e mãos";
			case GROSSA_PROFUNDO:
				return "20 - Grossa, apergaminhada com sulcos profundos";
			default:
				return "";
		}
	}
}
