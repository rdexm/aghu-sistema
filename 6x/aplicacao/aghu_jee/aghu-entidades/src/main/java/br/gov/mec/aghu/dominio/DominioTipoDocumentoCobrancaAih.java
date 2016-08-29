package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoDocumentoCobrancaAih implements Dominio {

	/**
	 * Normal
	 */
	N,

	/**
	 * FAEC
	 */
	F,

	/**
	 * DCIH com AIHS tipo 5
	 */
	C,

	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Normal";
		case F:
			return "FAEC";
		case C:
			return "DCIH com AIHS tipo 5";
		default:
			return "";
		}
	}

}
