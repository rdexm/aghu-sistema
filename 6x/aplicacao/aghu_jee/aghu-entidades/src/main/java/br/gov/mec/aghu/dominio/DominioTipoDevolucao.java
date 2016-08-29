package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoDevolucao implements Dominio{
	/**
	 *	DF - Devolução ao Fornecedor
	 */
	DF;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DF:
			return "Devolução ao Fornecedor";
		default:
			return "";
		}
	}
}
