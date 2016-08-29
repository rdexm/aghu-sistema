package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoCribPesoNascimento implements Dominio {
	/**
	 * <= 700 gramas
	 */
	POSITIVO_7(7),
	/**
	 * 701 - 850 gramas
	 */
	POSITIVO_4(4),
	/**
	 * 851 - 1350 gramas
	 */
	POSITIVO_1(1),
	/**
	 * > 1350 gramas
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoCribPesoNascimento(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_7:
			return "7";
		case POSITIVO_4:
			return "4";
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}