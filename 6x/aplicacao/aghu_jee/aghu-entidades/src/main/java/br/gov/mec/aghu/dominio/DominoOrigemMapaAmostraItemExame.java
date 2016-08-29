package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Representa os valores do atributo origemMapa da classe AelAmostraItemExames.
 * 
 * @author rcorvalao
 *
 */
public enum DominoOrigemMapaAmostraItemExame implements Dominio {
	/**
	 * Ambulatório
	 */
	A,
	/**
	 * Internação
	 **/
	I,
	/**
	 * Todas as origens
	 */
	T
	;

	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambulatório";
		case I:
			return "Internação";
		case T:
			return "Todas as origens";
		default:
			return "";
		}
	}	
	
}
