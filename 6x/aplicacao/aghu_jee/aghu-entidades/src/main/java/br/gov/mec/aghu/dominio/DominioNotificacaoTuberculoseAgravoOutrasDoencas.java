package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseAgravoOutrasDoencas implements Dominio {
	
	IGNORADO(9),
	
	SIM(2),
	
	NAO(1)	;

	private int value;

	private DominioNotificacaoTuberculoseAgravoOutrasDoencas(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IGNORADO:
			return "Ignorado";
		case SIM:
			return "Sim";
		case NAO:
			return "Não";
		default:
			return "";
		}
	}

}