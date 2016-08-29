package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAbordagemNeuroeixoNvlPuncionados implements Dominio {
	M, 
	P;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Paramediana";
		case M:
			return "Mediana";
		default:
			return "";
		}
	}

}

