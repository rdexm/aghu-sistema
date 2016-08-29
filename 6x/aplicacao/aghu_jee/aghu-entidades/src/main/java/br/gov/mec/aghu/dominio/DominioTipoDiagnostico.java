package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Doimínio tipo de diagnóstico.
 * 
 * @author gmneto
 * 
 */
public enum DominioTipoDiagnostico implements Dominio {
	/**
	 * Cid
	 */
	C,
	/**
	 * Descritivo
	 */
	D,
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
			return "Cid";
		case D:
			return "Descritivo";
		case A:
			return "Ambos";
		default:
			return "";
		}
	}

}