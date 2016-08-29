package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioBaseReposicao implements Dominio{
	HC,
	CP;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case HC:
			return "Histórico de Consumo";
		case CP:
			return "Consumo Projetado";
		default:
			return "";
		}
	}

}
