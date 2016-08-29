package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndMovimento implements Dominio {

	SLC,
	AUT,
	MAN,
	SDC,
	COM, //COMPRAS
	CON, //CONSUMO
	FFX; //COMPRA FUNDO FIXO
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SLC:
			return "SLC";
		case AUT:
			return "AUT";
		case MAN:
			return "MAN";
		case SDC:
			return "SDC";
		case COM:
			return "COM";
		case CON:
			return "CON";
		case FFX:
			return "FFX";
		default:
			return "";
	}
	}

}
