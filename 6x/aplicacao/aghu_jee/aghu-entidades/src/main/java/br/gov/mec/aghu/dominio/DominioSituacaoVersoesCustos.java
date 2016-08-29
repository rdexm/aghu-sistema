package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



/**
 * Domínio que indica a situação de uma entidade, se ativa ou inativa.
 * 
 * @author gmneto
 * 
 */
public enum DominioSituacaoVersoesCustos implements Dominio {

	/**
	 * Ativa
	 */
	A, 
	/**
	 * Elaboração
	 */
	E, 
	/**
	 * Inativa
	 */
	I, 
	/**
	 * Programada
	 */
	P ;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Inativa";
		case A:
			return "Ativa";
		case E:
			return "Elaboração";
		case P:
			return "Programada";
		default:
			return "";
		}
	}
	
	

}
