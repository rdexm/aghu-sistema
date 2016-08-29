package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLocalAgendaCirurgica implements Dominio {

	UBC,
	CCA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case UBC:
			return "UBC";
		case CCA:
			return "CCA";
		default:
			return "";
		}
	}

}
