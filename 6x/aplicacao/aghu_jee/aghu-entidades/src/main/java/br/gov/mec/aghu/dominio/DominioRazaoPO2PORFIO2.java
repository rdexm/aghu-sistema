package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioRazaoPO2PORFIO2 implements Dominio {
	MAIOR_QUE_DOIS_VIRGULA_QUARENTA_NOVE(0),
	ENTRE_UM_E_DOIS_VIRGULA_QUARENTA_NOVE(5),
	ENTRE_ZERO__VIRGULA_TRES_E_ZERO_NOVENTA_NOVE(16),
	MENOR_QUE_ZERO_VIRGULA_TRINTA(28);
	
	private int value;
	
	private DominioRazaoPO2PORFIO2(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case MAIOR_QUE_DOIS_VIRGULA_QUARENTA_NOVE:
				return "> 2,49";
			case ENTRE_UM_E_DOIS_VIRGULA_QUARENTA_NOVE:
				return "1 - 2,49";
			case ENTRE_ZERO__VIRGULA_TRES_E_ZERO_NOVENTA_NOVE:
				return "0,3 - 0,99”";
			case MENOR_QUE_ZERO_VIRGULA_TRINTA:
				return "< 0,3";
			default:
				return "";
		}
	}
}
