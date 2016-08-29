package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdemRelatorioMensalPosicaoFinalEstoque implements Dominio {
	
	C(1),
	N(2),
	V(3);
	
	
	private int value;
	
	private DominioOrdemRelatorioMensalPosicaoFinalEstoque(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Código Material";
		case N:
			return "Nome Material";
		case V:
			return "Valor Estoque";			
		default:
			return "";
		}
	}

}