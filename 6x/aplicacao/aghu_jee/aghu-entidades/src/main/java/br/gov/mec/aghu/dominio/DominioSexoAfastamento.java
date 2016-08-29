package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSexoAfastamento implements Dominio {

	/**
	 * Masculino
	 */
	M,

	/**
	 * Feminino
	 */
	F,
	/**
	 * Ambos
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Masculino";
		case F:
			return "Feminino";
		case A:
			return "Ambos";
		default:
			return "";
		}
	}

}
