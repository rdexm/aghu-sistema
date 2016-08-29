package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioValidacaoNF implements DominioString {
	S, R, N;

	@Override
	public String getCodigo() {
		return this.name();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "CONFERIDO";
		case R:
			return "RECUSADO";
		case N:
			return "EM_BRANCO";
		default:
			return "";
		}
	}

}