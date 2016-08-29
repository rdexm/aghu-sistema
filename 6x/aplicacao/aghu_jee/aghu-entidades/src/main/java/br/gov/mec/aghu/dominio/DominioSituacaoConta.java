package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de uma conta
 * hospitalar
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSituacaoConta implements Dominio {
	
	/**
	 * Aberta
	 */
	A,
	/**
	 * Fechada
	 */
	F,
	/**
	 * Encerrada
	 */
	E,
	/**
	 * Cobrada
	 */
	O,
	/**
	 * Não Paga
	 */
	N,
	/**
	 * Cancelada
	 */
	C,
	/**
	 * Rejeitado
	 */
	R,
	/**
	 * Pendente
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
		case F:
			return "Fechada";
		case E:
			return "Encerrada";
		case O:
			return "Cobrada";
		case N:
			return "Não Paga";
		case C:
			return "Cancelada";
		case R:
			return "Rejeitado";
		case P:
			return "Pendente";
		default:
			return "";
		}

	}
	
}
