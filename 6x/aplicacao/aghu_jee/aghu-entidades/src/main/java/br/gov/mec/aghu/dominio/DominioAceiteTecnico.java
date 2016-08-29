package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author rafael.nascimento.
 */

public enum DominioAceiteTecnico implements Dominio {
	/**
	 * Aceito.
	 */
	A,
	
	/**
	 * Recusado.
	 */
	R;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aceito";
		case R:
			return "Recusado";
		default:
			return "";
		}
	}
}