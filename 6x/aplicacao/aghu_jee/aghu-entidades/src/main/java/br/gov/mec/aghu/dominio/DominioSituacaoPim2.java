package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoPim2 implements Dominio {
	E,
	A,
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Efetivada";
		case A:
			return "Isenta";
		case P:
			return "Pendente";
		default:
			return "";
		}
	}

}
