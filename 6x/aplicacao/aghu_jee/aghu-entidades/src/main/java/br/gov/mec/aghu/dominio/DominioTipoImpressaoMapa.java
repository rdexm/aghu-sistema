package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoImpressaoMapa implements Dominio {
	
	/**
	 * Impressão
	 */
	I, 
	/**
	 * Reimpressão
	 */
	R,
	/**
	 * Visualização (Scren)
	 */
	S;

	@Override 
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case I: return "Impressão";
			case R: return "Reimpressão";
			case S: return "Visualização (Scren)";
		default:
			return "";
		}
	}
}
