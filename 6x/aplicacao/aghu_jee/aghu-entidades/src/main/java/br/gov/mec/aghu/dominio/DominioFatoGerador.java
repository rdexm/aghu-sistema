package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFatoGerador implements Dominio {
	

	/**
	 * Pagamento do Título
	 */
	PGT,
	
	/**
	 * Emissão do Documento Fiscal
	 */
	EDF;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PGT:
			return "Pagamento do Título";
		case EDF:
			return "Emissão do Documento Fiscal";
		default:
			return "";
		}
	}
}