package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTipoDocumentoEntrada implements DominioString {

	/**
	 */
	NF,
	/**
	 */
	VL,
	/**
	 */
	DO;

	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NF:
			return "Nota Fiscal";
		case VL:
			return "Vale";
		case DO:
			return "Doação";
		default:
			return "";
		}
	}

}
