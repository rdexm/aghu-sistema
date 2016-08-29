package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCondicaoPsicoSensorial implements Dominio {
	AD,
	RE,
	AM;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case AD:
			return "";
		case RE:
			return "";
		case AM:
			return "";
		default:
			return "";
		}
	}

}
