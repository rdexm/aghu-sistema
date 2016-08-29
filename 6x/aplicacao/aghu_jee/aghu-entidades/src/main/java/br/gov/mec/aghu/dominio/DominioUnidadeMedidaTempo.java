package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeMedidaTempo implements Dominio {
	D, // dias
	H, // horas
	M // minutos
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
		case H:
			return "Horas";
		case M:
			return "Minutos";
		default:
			return "";
		}
	}
}
