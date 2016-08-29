package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoCxtPostalServidor implements Dominio {
	
	N,E,A,L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "";
		case L:
			return "Lida";
		case E:
			return "Excluída";
		case A:
			return "";
		default:
			return "";
		}
	}
}