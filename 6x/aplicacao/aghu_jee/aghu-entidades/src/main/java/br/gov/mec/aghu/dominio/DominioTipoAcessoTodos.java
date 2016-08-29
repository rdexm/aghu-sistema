package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAcessoTodos implements Dominio {
	ENTRADA, 
	SAIDA,
	TODOS;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ENTRADA:
			return "Entrada";
		case SAIDA:
			return "Saída";
		case TODOS:
			return "Todos";		
		default:
			return "";
		}
	}

}

