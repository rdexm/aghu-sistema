package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCidCustoPacienteDiagnostico implements Dominio {

	/**
	 * 
	 */
	Q,
	/**
	 * 
	 */
	T;
	
	private static final long serialVersionUID = 4508319733013317612L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Q:
			return "Qualquer um dos CIDs informados";
		case T:
			return "Quando ocorrer todos os CIDs informados";
		default:
			return "";
		}
	}

}
