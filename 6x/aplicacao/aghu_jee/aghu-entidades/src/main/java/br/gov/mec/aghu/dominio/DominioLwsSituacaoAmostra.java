package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLwsSituacaoAmostra implements Dominio{

	L, // Liberado
	N, // Não Liberado
	C; // Cancelado
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case L:
			return "Liberado";
		case N:
			return "Não Liberado";
		case C:
			return "Cancelado";
		default:
			return "";
		}
	}
}
