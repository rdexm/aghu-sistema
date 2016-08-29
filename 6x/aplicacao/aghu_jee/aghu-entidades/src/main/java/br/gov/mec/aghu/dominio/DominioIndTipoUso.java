package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author gandriotti
 * TODO
 */
public enum DominioIndTipoUso implements Dominio {
	/**
	 */
	A,
	/**
	 */
	B,
	/**
	 */
	C;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambos";
		case B:
			return "Bomba";
		case C:
			return "Comum";
		default:
			return "";
		}
	}

}
