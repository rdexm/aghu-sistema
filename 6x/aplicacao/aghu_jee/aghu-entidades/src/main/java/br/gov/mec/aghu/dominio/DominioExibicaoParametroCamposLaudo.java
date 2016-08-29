package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioExibicaoParametroCamposLaudo implements Dominio {

	/**
	 * Somente Tela
	 */
	T, 

	/**
	 * Somente Relatório
	 */
	R,

	/**
	 * Não exibir
	 */
	N,
	
	/**
	 * Ambos(tela e relatório)
	 */
	A;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Somente Tela";
		case R:
			return "Somente Relatório";
		case N:
			return "Não exibir";
		case A:
			return "Ambos(tela e relatório)";
		default:
			return "";
		}
	}

}
