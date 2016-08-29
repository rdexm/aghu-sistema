
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author rhrosa
 *
 */
public enum DominioTipoImpressaoRelatorio implements Dominio {
	
	/**
	 * Visualizar
	 */
	S,
	/**
	 * Reimpressão
	 */
	R,
	/**
	 * Definitiva
	 */
	N;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Visualizar";
		case R:
			return "Reimpressão";
		case N:
			return "Definitiva";		
		default:
			return "";
		}
	}

}
