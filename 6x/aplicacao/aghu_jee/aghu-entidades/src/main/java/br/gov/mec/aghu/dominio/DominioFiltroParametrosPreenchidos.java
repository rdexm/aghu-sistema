package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFiltroParametrosPreenchidos implements Dominio {
	TODOS,
	PREENCHIDOS,
	NAO_PREENCHIDOS;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case TODOS:
			return "Todos";
		case PREENCHIDOS:
			return "Preenchidos";
		case NAO_PREENCHIDOS:
			return "Não Preenchidos";
		default:
			return "";
		}
	}

}
