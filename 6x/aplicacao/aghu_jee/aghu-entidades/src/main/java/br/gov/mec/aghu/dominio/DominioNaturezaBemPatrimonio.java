package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author rafael.nascimento.
 */

public enum DominioNaturezaBemPatrimonio implements Dominio {
	/**
	 * Patrimônio.
	 */
	PA,
	
	/**
	 * Controle Físico.
	 */
	CF,
	/**
	 * Doação.
	 */
	DO,
	
	/**
	 * Bens fabricação própria.
	 */
	FP,
	/**
	 * Bens de terceiro – Demonstração ou Empréstimo.
	 */
	TD,
	
	/**
	 * Bens de terceiro – Comodato ou Locação.
	 */
	TC;	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PA:
			return "Patrimônio";
		case CF:
			return "Controle Físico";
		case DO:
			return "Doação";
		case FP:
			return "Bens fabricação própria";
		case TD:
			return "Bens de terceiro – Demonstração ou Empréstimo";
		case TC:
			return "Bens de terceiro – Comodato ou Locação";
		default:
			return "";
		}
	}
}