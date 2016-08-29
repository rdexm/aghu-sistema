package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a forma de cálculo do Aprazamento - prescrição
 * 
 * @author rpetter
 * 
 */
public enum DominioFormaCalculoAprazamento implements Dominio {

	/**
	 * Positivo
	 */
	I,

	/**
	 * Negativo
	 */
	V,

	/**
	 * Contínuo
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Intervalo";
		case V:
			return "Vezes dia";
		case C:
			return "Contínuo";
		default:
			return "";
		}
	}
}
