package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioBossa implements DominioString {
	NAO, UM_MAIS, DOIS_MAIS,TRES_MAIS;
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
		case NAO:
			return "Não";
		case UM_MAIS:
			return "+";
		case DOIS_MAIS:
			return "++";
		case TRES_MAIS:
			return "+++";
		default:
			return null;	
		}
	}
}
