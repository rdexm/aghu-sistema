package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeMedidaIdade implements Dominio {
	D, // dias
	M, // meses
	A // anos
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Dias";
		case M:
			return "Meses";
		case A:
			return "Anos";
		default:
			return "";
		}
	}
}
