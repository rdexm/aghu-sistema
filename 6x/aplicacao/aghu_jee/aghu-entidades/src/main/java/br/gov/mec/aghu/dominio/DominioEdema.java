package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEdema implements Dominio{
	NAO,
	MSI,
	ANS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO:
			return "Não";
		case MSI:
			return "Ms Inferiores";
		case ANS:
			return "Anasarca";
		default:
			return "";
		}
	}

}
