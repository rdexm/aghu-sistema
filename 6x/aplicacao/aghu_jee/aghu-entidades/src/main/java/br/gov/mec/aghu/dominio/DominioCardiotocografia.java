package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCardiotocografia implements Dominio {
	BRAD,
	CAT1,
	CAT2,
	CAT3,
	REATIVO
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case BRAD:
			return "Cardiotocografia BRAD";
		case CAT1:
			return "Cardiotocografia CAT1";
		case CAT2:
			return "Cardiotocografia CAT2";
		case CAT3:
			return "Cardiotocografia CAT3";
		case REATIVO:
			return "Cardiotocografia REATIVO";
		default:
			return "";
		}
	}

}
