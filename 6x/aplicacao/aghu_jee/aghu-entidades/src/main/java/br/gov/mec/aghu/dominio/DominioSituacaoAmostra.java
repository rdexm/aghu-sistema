package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de uma amostra
 * 
 * @author lsamberg
 * 
 */
public enum DominioSituacaoAmostra implements Dominio {
	
	/**
	 * Gerada
	 */
	G,

	/**
	 * Recebida
	 */
	R,
	
	/**
	 * Executada
	 */
	E,

	/**
	 * Cancelada
	 */
	A,
	
	/**
	 * Coletada
	 */
	C,
	
	/**
	 * Recebida Unidade de Coleta
	 */
	U,
	
	/**
	 * Em Coleta
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Gerada";
		case R:
			return "Recebida";
		case E:
			return "Executada";
		case A:
			return "Cancelada";
		case C:
			return "Coletada";
		case U:
			return "Recebida Unidade de Coleta";
		case M:
			return "Em Coleta";
		default:
			return "";
		}
	}

}
