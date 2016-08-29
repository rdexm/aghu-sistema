package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSimNaoTodos implements Dominio {
	SIM, 
	NAO,
	TODOS;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SIM:
			return "Sim";
		case NAO:
			return "Não";
		case TODOS:
			return "Todos";		
		default:
			return "";
		}
	}

}

