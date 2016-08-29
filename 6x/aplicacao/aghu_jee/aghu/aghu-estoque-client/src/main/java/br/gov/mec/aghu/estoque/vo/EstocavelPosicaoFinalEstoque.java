package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum EstocavelPosicaoFinalEstoque implements Dominio {

	T, S, N;		

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case T: return "Todos";
			case S: return "Estocável";
			case N: return "Não Estocável";			
			default: return "Todos";
		}
	}

}
