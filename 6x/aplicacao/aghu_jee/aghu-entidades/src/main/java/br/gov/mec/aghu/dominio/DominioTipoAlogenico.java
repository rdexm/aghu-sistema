package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAlogenico implements Dominio {

	A,
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aparentado";
		case N:
			return "Não Aparentado";
		default:
			return "";
		}
	}
}
