package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioOrigem implements Dominio {
	/**
	 * 
	 */
	S,
	
	/**
	 * 
	 */
	J,
	
	/**
	 * 
	 */
	O,
	
	/**
	 * 
	 */
	A,
	
	/**
	 * 
	 */
	P,
	
	/**
	 * 
	 */
	H,
	
	/**
	 * 
	 */
	N,
	
	/**
	 * 
	 */
	D;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Solicitações Especiais";
		case J:
			return "Justificativa";
		case O:
			return "Órtese/Prótese";
		case A:
			return "Agenda";
		case P:
			return "Procedimento";
		case H:
			return "Hemoterapia";
		case N:
			return "Anestesia";
		case D:
			return "Diagnóstico";

		default:
			return "";
		}
	}
}
