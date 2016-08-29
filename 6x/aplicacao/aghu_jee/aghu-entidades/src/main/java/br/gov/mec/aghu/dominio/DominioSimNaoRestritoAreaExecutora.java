package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lsamberg
 *
 */
public enum DominioSimNaoRestritoAreaExecutora implements Dominio {

	/**
	 * SIM
	 */
	S,
	
	/**
	 * NAO
	 */
	N,
	
	/**
	 * Restrito Área Executora
	 */
	R;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case R:
			return "Restrito Área Executora";
		default:
			return "";
		}
	}
	
}
