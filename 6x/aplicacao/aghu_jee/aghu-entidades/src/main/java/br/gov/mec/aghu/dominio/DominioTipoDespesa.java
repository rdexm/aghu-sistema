package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioTipoDespesa implements Dominio {

	/**
	 * Consumo.
	 */
	C,

	/**
	 * Obra.
	 */
	O,

	/**
	 * Serviço.
	 */
	S,

	/**
	 * Temporário.
	 */
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Consumo";
		case O:
			return "Obra";
		case S:
			return "Serviço";
		case T:
			return "Temporário";
		default:
			return "";
		}
	}

}
