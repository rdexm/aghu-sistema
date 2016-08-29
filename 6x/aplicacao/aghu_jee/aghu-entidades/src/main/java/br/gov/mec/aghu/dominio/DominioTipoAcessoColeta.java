package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de acesso para a coleta da amostra do exame.
 * 
 * @author diego.pacheco
 *
 */
public enum DominioTipoAcessoColeta implements Dominio {
	
	/**
	 * Arterial
	 */
	A,
	
	/**
	 * Capilar
	 */
	C,
	
	/**
	 * Cateter	
	 */
	T,
	
	/**
	 * Venoso	
	 */
	V;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Arterial";
		case C:
			return "Capilar";
		case T:
			return "Cateter";
		case V:
			return "Venoso";
		default:
			return "";
		}
	}

}
