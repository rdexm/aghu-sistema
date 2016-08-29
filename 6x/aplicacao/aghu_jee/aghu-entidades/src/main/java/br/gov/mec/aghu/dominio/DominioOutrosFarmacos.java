package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOutrosFarmacos implements Dominio {
	
	CADASTRADO,
	NAO_CADASTRADO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CADASTRADO:
			return "Cadastrado";
		case NAO_CADASTRADO:
			return "Não Cadastrado";
		default:
			return "";
		}
	}

}
