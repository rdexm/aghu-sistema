package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAcesso implements Dominio {
	ENTRADA, 
	SAIDA;
	
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
		default:
			return "";
		}
	}

}

