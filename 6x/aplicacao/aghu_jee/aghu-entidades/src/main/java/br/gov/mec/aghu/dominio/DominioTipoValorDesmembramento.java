package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author rafael.nascimento.
 */
public enum DominioTipoValorDesmembramento implements Dominio {
	/**
	 * Valor.
	 */
	V,
	/**
	 * Percetual.
	 */
	P;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case V:
			return "Valor";
		case P:
			return "Percetual";
		default:
			return "";
		}
	}
}