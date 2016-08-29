package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo do centro de produção.
 * 
 * @author dansantos
 * 
 */
public enum DominioTipoCentroProducao implements Dominio {
	
	/**
	 * Local Paciente
	 */
	L,

	/**
	 * Nutrição
	 */
	N,
	
	/**
	 * Serv Assistencial
	 */
	S,
	
	/**
	 * Unid Executora
	 */
	U;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case L:
			return "Local Paciente";
		case N:
			return "Nutrição";
		case S:
			return "Serv Assistencial";
		case U:
			return "Unid Executora";
		default:
			return "";
		}
	}

}