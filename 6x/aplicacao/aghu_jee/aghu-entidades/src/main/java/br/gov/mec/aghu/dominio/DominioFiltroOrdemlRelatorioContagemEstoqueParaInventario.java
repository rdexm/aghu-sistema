package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario implements Dominio {
	
	G(1),
	C(2),
	N(3),
	E(4);
	
	
	private int value;
	
	private DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Grupo";
		case C:
			return "Codigo Material";
		case N:
			return "Nome Material";	
		case E:
			return "Endereço";	
		default:
			return "";
		}
	}

}