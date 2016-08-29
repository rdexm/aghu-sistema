package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque implements Dominio {
	
	S(1),
	N(2),
	T(3);
	
	
	private int value;
	
	private DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case T:
			return "Todos";			
		default:
			return "";
		}
	}

}