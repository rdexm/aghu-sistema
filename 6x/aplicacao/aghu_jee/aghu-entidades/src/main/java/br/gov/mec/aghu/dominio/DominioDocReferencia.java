package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Doc de referência de um paciente
 * 
 * @author ehgsilva
 * 
 */
public enum DominioDocReferencia implements Dominio {
	
	
	/**
	 * APAC
	 */
	APAC(1),
	
	/**
	 * AIH
	 */
	AIH(2);


	private int value;
	
	private DominioDocReferencia(int value) {
		this.value = value;
	}
	
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case APAC:
			return "APAC";
		case AIH:
			return "AIH";
		default:
			return "";
		}
	}

}
