package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 29/09/2010.
 * 
 * @author gmaciel
 *
 */
public enum DominioIndPendenteItemPrescricao implements Dominio {

	/**
	 * Confirmado
	 */
	N,
	/**
	 * Exclusão não validada
	 */
	E,
	/**
	 * Pendente
	 */
	P,
	/**
	 * Alteração não validada
	 */
	A,
	/**
	 * Modelo basico
	 */
	B,
	/**
	 * Desdobramento
	 */
	D,
	/**
	 * Reeprescrição em exclusão
	 */
	Y,
	/**
	 * Reeprescrição não validada
	 */
	R,
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Validado";
		case E:
			return "Exclusão não validada";
		case P:
			return "Pendente";
		case A:
			return "Alteração não validada";
		case B:
			return "Modelo basico";
		case D:
			return "Desdobramento";
		case Y:
			return "Reeprescrição em exclusão";
		case R:
			return "Represcrição não validada";	
		default:
			return "Estado inválido. Contate o administrador do sistema.";
		}
	}
}
