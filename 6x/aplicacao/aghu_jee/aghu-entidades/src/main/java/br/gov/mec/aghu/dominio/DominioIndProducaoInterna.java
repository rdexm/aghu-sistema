package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author gandriotti
 * TODO
 */
public enum DominioIndProducaoInterna implements Dominio {
	/**
	 */
	F,
	/**
	 */
	G,
	/**
	 */
	R;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Farmácia Industrial";
		case G:
			return "Gráfica";
		case R:
			return "Rouparia";
		default:
			return "";
		}
	}

}
