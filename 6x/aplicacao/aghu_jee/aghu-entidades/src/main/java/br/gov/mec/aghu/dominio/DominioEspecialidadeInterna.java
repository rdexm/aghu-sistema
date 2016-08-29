package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica se a especialidade interna. 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioEspecialidadeInterna implements Dominio {
	
	/**
	 * Não Interna.
	 */
	N,

	/**
	 * Interna Unidade.
	 */
	I,
	
	/**
	 * Interna Urgência.
	 */
	U;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não Interna";
		case I:
			return "Interna Unidade";
		case U:
			return "Interna Urgência";
		default:
			return "";
		}
	}

}
