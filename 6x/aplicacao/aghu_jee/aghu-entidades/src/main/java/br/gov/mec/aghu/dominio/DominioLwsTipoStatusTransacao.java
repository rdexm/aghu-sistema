package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLwsTipoStatusTransacao implements Dominio {
	
	NAO_PROCESSADA(0),
	PROCESSADA(1);
	
	private int value;

	private DominioLwsTipoStatusTransacao(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO_PROCESSADA:
			return "Não Processada";
		case PROCESSADA:
			return "Processada";
		default:
			return "";
		}
	}
}
