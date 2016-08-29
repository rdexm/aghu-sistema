package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a operação do banco.
 * 
 * @author dansantos
 * 
 */
public enum DominioOperacaoBanco implements Dominio {
	
	/**
	 * Update
	 */
	UPD,
	
	/**
	 * Delete
	 */
	DEL,
	
	/**
	 * Insert
	 */
	INS;

	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case UPD:
			return "Update";
		case DEL:
			return "Delete";
		case INS:
			return "Insert";
		default:
			return "";
		}
	}

}
