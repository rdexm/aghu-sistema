package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lsamberg
 *
 */
public enum DominioApAnterior implements Dominio {

	/**
	 * Não Usa
	 */
	N, 
	/**
	 * Obrigatório
	 */
	O,
	/**
	 * Opcional
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não Usa";
		case O:
			return "Obrigatório";
		case P:
			return "Opcional";
		default:
			return "";
		}
	}
	
}
