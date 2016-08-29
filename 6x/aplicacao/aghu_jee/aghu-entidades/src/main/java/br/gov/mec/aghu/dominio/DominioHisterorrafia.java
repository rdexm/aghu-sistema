package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioHisterorrafia implements Dominio {
	SC,
	PS
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case SC:
			return "Sutura Continua";
		case PS:
			return "Pontos Separados";
		default:
			return "";
		}
	}

}
