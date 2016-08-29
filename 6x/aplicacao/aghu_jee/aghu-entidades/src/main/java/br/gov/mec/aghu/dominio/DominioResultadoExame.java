package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author bsoliveira
 *
 */
public enum DominioResultadoExame implements Dominio {

	/**
	 * Positivo
	 */
	P, 
	/**
	 * Negativo
	 */
	N,
	/**
	 * Em andamento
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Positivo";
		case N:
			return "Negativo";
		case E:
			return "Em andamento";
		default:
			return "";
		}
	}
	
}
