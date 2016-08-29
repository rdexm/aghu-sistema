package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDestinoRecemNascido implements Dominio{
	O, I, A;

	public int getCodigo() {
		return this.ordinal();
	}

	public String getDescricao() {
		switch (this) {
		case O:
			return "Óbito no CO";
		case I:
			return "Internação na UTI Neo";
		case A:
			return "Observação na Admissão";
		default:
			return "";
		}
	}
}