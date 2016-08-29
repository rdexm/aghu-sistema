package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioComparacaoDataCompetencia implements DominioString {

	IGUAL,
	MAIOR;

	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IGUAL:
			return "Igual";
		case MAIOR:
			return "Maior";
		default:
			return "";
		}
	}
}
