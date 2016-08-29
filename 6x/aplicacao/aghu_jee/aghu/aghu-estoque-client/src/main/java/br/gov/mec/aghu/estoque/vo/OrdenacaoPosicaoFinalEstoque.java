package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum OrdenacaoPosicaoFinalEstoque implements Dominio {
	
	C, N, V;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	public String getDescricao() {
		switch (this) {
		case C:
			return "Código Material";
		case N:
			return "Nome Material";			
		case V:
			return "Valor";
		default:
			return "Código Material";
		}
	}

}
