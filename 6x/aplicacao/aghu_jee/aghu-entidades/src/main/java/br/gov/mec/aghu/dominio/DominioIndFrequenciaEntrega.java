package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que indica a frequencia de entregas de compras
 * 
 */
public enum DominioIndFrequenciaEntrega implements Dominio {
	/**
	 * Diária
	 */
	D,
	/**
	 * Semanal
	 */
	S,
	/**
	 * Mensal
	 */
	M;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
			case D:
				return "Diária";
			case S:
				return "Semanal";
			case M:
				return "Mensal";
			default:
				return "";
		}
	}
	
	public static DominioIndFrequenciaEntrega getInstance(String valor) {
		if ("D".equals(valor)) {
			return DominioIndFrequenciaEntrega.D;
		} else if ("S".equals(valor)) {
			return DominioIndFrequenciaEntrega.S;
		} else if ("M".equals(valor)) {
			return DominioIndFrequenciaEntrega.M;
		} else {
			return null;
		}
	}
}
