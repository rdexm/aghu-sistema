package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio usado na tabela AAC_UNID_FUNCIONAL_SALAS
 * 
 */

public enum DominioTipoUnidadeFuncionalSala implements Dominio {

	/**
	 * Aula
	 */
	A, 
	/**
	 * Consultório
	 */
	C,
	/**
	 * Chefia
	 */
	D,
	/**
	 * Exames - Procedimentos
	 */
	E,
	/**
	 * Grupos
	 */
	G,
	/**
	 * Reunião
	 */
	R,
	/**
	 * Secretaria
	 */
	S,
	/**
	 * Curativos
	 */
	U,
	/**
	 * Vacinas
	 */
	V,
	/**
	 * Odonto
	 */
	O,
	/**
	 * Espera
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
			return "Aula";
		case C:
			return "Consultório";
		case D:
			return "Chefia";
		case E:
			return "Exames - Procedimentos";
		case G:
			return "Grupos";
		case R:
			return "Reunião";
		case S:
			return "Secretaria";
		case U:
			return "Curativos";
		case V:
			return "Vacinas";
		case O:
			return "Odonto";
		case P:
			return "Espera";
		default:
			return "";
		}
	}
	
}
