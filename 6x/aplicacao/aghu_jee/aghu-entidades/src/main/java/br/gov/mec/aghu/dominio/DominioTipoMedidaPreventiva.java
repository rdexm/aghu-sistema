package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio com tipos de medida preventiva.
 * 
 * @author evschneider
 * 
 */
public enum DominioTipoMedidaPreventiva implements Dominio {

	/**
	 * Transmitível
	 */
	T,
	/**
	 * Proteção
	 */
	P,
	/**
	 * Referência
	 */
	R;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Transmitível";
		case P:
			return "Proteção";
		case R:
			return "Referência";
		default:
			return "";
		}
	}
	
}
