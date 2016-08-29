/**
 * 
 */
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Tipo Patologia Obito
 * <p>
 * B - doenca base; C - doenca que contribuiu
 * 
 * @author rcorvalao
 */
public enum DominioPatologiaObito implements Dominio {
	/**
	 * Doença Base
	 */
	B,
	/**
	 * Doença que Contribuiu
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case B:
			return "Doença Base";
		case C:
			return "Doença que Contribuiu";
		default:
			return "";
		}
	}

	public static DominioPatologiaObito getInstance(final String valor) {
		if (valor.equalsIgnoreCase("B")) {
			return DominioPatologiaObito.B;
		} else if (valor.equalsIgnoreCase("C")) {
			return DominioPatologiaObito.C;
		} else {
			return null;
		}
	}

}
