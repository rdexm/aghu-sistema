package br.gov.mec.aghu.core.dominio;


/**
 * Domínio que indica o Fator Rh de uma pessoa.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioOperacoesJournal implements Dominio {
	
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
