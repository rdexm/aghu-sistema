package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 
 */
public enum DominioUnidFunc implements Dominio {
	/**
	 * Ativo.
	 */
	A,
	/**
	 * Inativo.
	 */
	I;

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
		default:
			return "";
		}
	}
	
	public static DominioUnidFunc getInstance(boolean valor) {
		if (valor) {
			return DominioUnidFunc.A;
		} else {
			return DominioUnidFunc.I;
		}
	}
	
	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * @return
	 */
	public boolean isAtivo(){
		switch (this) {
		case A:
			return Boolean.TRUE;
		case I:	
			return Boolean.FALSE;
		default:
			return Boolean.FALSE;
		}
	}

}
