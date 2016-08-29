package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTipoCups implements DominioString {
	/**
	 * PDF
	 */
	PDF("PDF"),
	/**
	 * RAW
	 */
	RAW("RAW");

	private String value;

	private DominioTipoCups(String value) {
		this.value = value;
	}

	@Override
	public String getCodigo() {
		switch (this) {
		case PDF:
			return "PDF";
		case RAW:
			return "RAW";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		return this.value;
	}

}
