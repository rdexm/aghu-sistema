package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAltaUTI implements Dominio {
	/**
	 * Alta
	 */
	POSITIVO_1(1),
	/**
	 * Óbito
	 */
	POSITIVO_2(2),
	/**
	 * Transferência
	 */
	POSITIVO_3(3);

	private int value;

	private DominioTipoAltaUTI(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_1:
			return "Alta";
		case POSITIVO_2:
			return "Óbito";
		case POSITIVO_3:
			return "Transferência";
		default:
			return "";
		}
	}
}
