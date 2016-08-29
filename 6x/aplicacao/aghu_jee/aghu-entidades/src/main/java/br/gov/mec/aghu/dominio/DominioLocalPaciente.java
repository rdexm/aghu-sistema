package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioLocalPaciente implements Dominio {
	
	/**
	 * Leito.
	 */
	L,

	/**
	 * Unidade Funcional.
	 */
	U,
	
	/**
	 * Quarto.
	 */
	Q;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case L:
			return "Leito";
		case U:
			return "Unidade Funcional";
		case Q:
			return "Quarto";
		default:
			return "";
		}
	}

}
