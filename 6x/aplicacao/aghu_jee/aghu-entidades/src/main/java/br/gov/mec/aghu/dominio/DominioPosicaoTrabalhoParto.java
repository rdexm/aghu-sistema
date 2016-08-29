package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPosicaoTrabalhoParto implements Dominio {
	 SENTADA, DEITADA, DEAMBULANDO,OUTRA;

	@Override
	public int getCodigo() {
		return ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SENTADA:
			return "SENTADA";
		case DEITADA:
			return "DEITADA";
		case DEAMBULANDO:
			return "DEAMBULANDO";
		case OUTRA:
			return "OUTRA";
		default:
			return null;
		}
	}

}
