package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 07/02/2011.
 * 
 * @author tfelini
 *
 */
public enum DominioIndPendentePrescricoesCuidados implements Dominio {

	/**
	 * Válido
	 */
	N,
	/**
	 * Exclusão pendente
	 */
	E,
	/**
	 * Pendente
	 */
	P,
	/**
	 * Alteração pendente
	 */
	A,
	/**
	 * Rascunho
	 */
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Valido";
		case E:
			return "Exclusão Pendente";
		case P:
			return "Pendente";
		case A:
			return "Alteração Pendente";
		case X:
			return "Rascunho";	
		default:
			return "Estado inválido. Contate o administrador do sistema.";
		}
	}
}
