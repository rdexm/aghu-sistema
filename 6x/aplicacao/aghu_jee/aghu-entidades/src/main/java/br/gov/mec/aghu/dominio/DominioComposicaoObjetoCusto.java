package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioComposicaoObjetoCusto implements Dominio {
	
	/**
	 * Por Recurso
	 */
	R,
	
	/**
	 * Por Atividade
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Por Recurso";
		case A:
			return "Por Atividade";
		default:
			return "";
		}
	}
}
