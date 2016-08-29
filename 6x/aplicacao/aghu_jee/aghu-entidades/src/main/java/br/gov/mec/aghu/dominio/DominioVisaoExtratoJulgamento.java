package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioVisaoExtratoJulgamento implements Dominio{
	TODOS,
	
	SELECIONADOS;

	private int value;

	private DominioVisaoExtratoJulgamento() {}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case TODOS:
			return "Extrato Todos Itens do Fornecedor";
		case SELECIONADOS:
			return "Extrato Itens Selecionados";
		default:
			return "";
		}
	}
}
