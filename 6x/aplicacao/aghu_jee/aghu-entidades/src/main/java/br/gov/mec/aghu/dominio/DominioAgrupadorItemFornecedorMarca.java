package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAgrupadorItemFornecedorMarca  implements Dominio{
	ITEM,
	FORNECEDOR,
	MARCA;
	
	private int value;
	
	private DominioAgrupadorItemFornecedorMarca(){}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ITEM:
			return "Item";
		case FORNECEDOR:
			return "Fornecedor";
		case MARCA:
			return "Marca";	
		default:
			return "";
		}
	}
	
}
