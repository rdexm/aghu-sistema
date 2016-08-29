package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para reforçar retrições de tipo em campos com comportamento boleano.
 * 
 * @author gmneto
 * 
 */
public enum DominioOrderBy implements Dominio {
	/**
	 * Código
	 */
	C("Código"),
	/**
	 * Nome
	 */
	N("Nome Material"),
	/**
	 * Endereço
	 */
	E("Endereço");
	DominioOrderBy(String orderBy){
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	public String getDescricao() {
		switch (this) {
		case C:
			return "Código";
		case N:
			return "Nome Material";
		case E:
			return "Endereço";
		default:
			return "";
		}
	}
}