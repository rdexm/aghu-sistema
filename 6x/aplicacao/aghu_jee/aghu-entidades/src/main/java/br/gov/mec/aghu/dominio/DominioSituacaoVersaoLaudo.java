package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoVersaoLaudo implements Dominio {


	/**
	 * Ativo
	 */
	A,
	/**
	 * Inativo
	 */
	I,
	/**
	 * Em construção
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ativo";
		case I:
			return "Inativo";
		case E:
			return "Em construção";
		default:
			return "";
		}
	}
	
	public boolean isAtivo(){
		return this.equals(DominioSituacaoVersaoLaudo.A);
	}
	
	public boolean isInativo(){
		return this.equals(DominioSituacaoVersaoLaudo.I);
	}
	
	public boolean isConstrucao(){
		return this.equals(DominioSituacaoVersaoLaudo.E);
	}
	
}
