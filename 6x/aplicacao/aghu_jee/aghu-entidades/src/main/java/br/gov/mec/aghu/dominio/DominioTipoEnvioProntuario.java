package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que representa os possíveis tipos de envio de prontuário.
 * @author gmneto
 *
 */
public enum DominioTipoEnvioProntuario implements Dominio {
	/**
	 * Total
	 */
	T,
	/**
	 * Único
	 */
	U,
	/**
	 * Provisório
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Total";
		case U:
			return "Único";
		case P:
			return "Provisório";	
		default:
			return "";
		}

	}

}
