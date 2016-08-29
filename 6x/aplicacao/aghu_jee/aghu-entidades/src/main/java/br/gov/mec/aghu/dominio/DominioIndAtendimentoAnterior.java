package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndAtendimentoAnterior implements Dominio {
	
	/**
	 * Consulta
	 */
	C,
	/**
	 * Despreza
	 */
	D,
	/**
	 * Internação
	 */
	I,
	/**
	 * Ambos
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Consulta";
		case D:
			return "Despreza";
		case I:
			return "Internação";
		case A:
			return "Ambos";
		default:
			return "";
		}
	}
}
