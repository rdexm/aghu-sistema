package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;


public enum DominioDinamicaUterina implements DominioString {
	ZERO, UM_DECIMO, DOIS_DECIMOS, TRES_DECIMOS, QUATRO_DECIMOS, MAIOR_QUE_QUATRO_DECIMOS;

	@Override
	public String getCodigo() {
		return descricao();
	}
	
	@Override
	public String getDescricao() {
		return descricao();
	}

	private String descricao() {
		switch (this) {
		case ZERO:
			return "0/10";
		case UM_DECIMO:
			return "1/10";
		case DOIS_DECIMOS:
			return "2/10";
		case TRES_DECIMOS:
			return "3/10";
		case QUATRO_DECIMOS:
			return "4/10";
		case MAIOR_QUE_QUATRO_DECIMOS:
			return "> 4/10";
		default:
			return null;	
		}
	}
}
