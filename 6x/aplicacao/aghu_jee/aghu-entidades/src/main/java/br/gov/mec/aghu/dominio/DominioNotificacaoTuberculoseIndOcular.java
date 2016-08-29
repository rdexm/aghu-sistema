package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndOcular implements Dominio {
	//

	/**
	 * Ocular
	 */
	OCULAR(5),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndOcular(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case OCULAR:
			return "Ocular";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}