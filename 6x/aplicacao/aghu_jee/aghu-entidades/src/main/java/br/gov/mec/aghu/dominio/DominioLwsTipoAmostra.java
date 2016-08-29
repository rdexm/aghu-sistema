package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLwsTipoAmostra implements Dominio{

	N, // Normal
	Q, // Controle de Qualidade
	B, // Branco
	C; // Soro Controle
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Normal";
		case Q:
			return "Controle de Qualidade";
		case B:
			return "Branco";
		case C:
			return "Soro Controle";
		default:
			return "";
		}
	}
}
