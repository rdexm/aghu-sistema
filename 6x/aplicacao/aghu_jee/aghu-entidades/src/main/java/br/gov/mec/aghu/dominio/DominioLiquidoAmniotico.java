package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLiquidoAmniotico implements Dominio {
	CL,
	MT,
	ME,
	SL,
	PL,
	XC
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case CL:
			return "Claro";
		case MT:
			return "Meconial Tinto";
		case ME:
			return "Meconial Espesso";
		case SL:
			return "Sanguinolento";
		case PL:
			return "Purulento";
		case XC:
			return "Xantocrômico";
		default:
			return "";
		}
	}

}
