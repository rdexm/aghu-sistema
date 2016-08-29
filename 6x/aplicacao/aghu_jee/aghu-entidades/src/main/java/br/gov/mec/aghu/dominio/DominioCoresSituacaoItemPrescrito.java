package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCoresSituacaoItemPrescrito implements Dominio {
	VERMELHO, AMARELO, VERDE, LARANJA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case VERMELHO:
			return "vermelho";
		case AMARELO:
			return "amarelo";
		case VERDE:
			return "verde";
		case LARANJA:
			return "cinza";
		default:
			return "";
		}
	}
	
	public String getHexadecimal() {
		switch (this) {
		case VERMELHO:
			return "#ff9999 !important";
		case AMARELO:
			return "#fcf2b8 !important";
		case VERDE:
			return "#E6ED97 !important";
		case LARANJA:
			return "#ffe45c !important";
		default:
			return "";
		}
	}
	
	public String getLegenda(){
		switch (this) {
		case VERMELHO:
			return "Medicamento Excluído";
		case AMARELO:
			return "Medicamento Alterado";
		case VERDE:
			return "Medicamento Incluído";
		case LARANJA:
			return "Medicamento Selecionado";
		default:
			return "";
		}
	}

}
