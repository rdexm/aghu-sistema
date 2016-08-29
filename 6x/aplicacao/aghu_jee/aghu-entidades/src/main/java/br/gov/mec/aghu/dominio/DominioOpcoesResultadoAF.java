package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOpcoesResultadoAF implements Dominio {
	POR_AF, 
	POR_ITEM_AF,
	POR_AFP,
	POR_PARCELA;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POR_AF:
			return "Por AF";
		case POR_ITEM_AF:
			return "Por Item de AF";
		case POR_AFP:
			return "Por AFP";
		case POR_PARCELA:
			return "Por Parcela";
		default:
			return "";
		}
	}

}

