package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de uma conta
 * hospitalar
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSituacaoContaApac implements Dominio {
	
	/**
	 * Aberta
	 */
	A,
	/**
	 * Encerrada
	 */
	E,
	/**
	 * Cancelada
	 */
	C,
	/**
	 * Apresentada
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aberta";
		case E:
			return "Encerrada";
		case C:
			return "Cancelada";
		case P:
			return "Apresentada";
		default:
			return "";
		}

	}
	
}
