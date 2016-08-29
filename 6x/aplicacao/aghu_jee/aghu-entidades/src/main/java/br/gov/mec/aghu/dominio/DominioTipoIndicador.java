package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio para manter os tipos de indicadores 
 */
public enum DominioTipoIndicador implements Dominio {
	/**
	 * Geral
	 */
	G,
	/**
	 * Especialidade
	 */
	E,
	/**
	 * Clinica
	 */
	C,
	/**
	 * Área Funcional
	 */
	U;
	/**
	 * Equipe
	 */
//	Q,
	/**
	 * Pagador
	 */
//	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Geral";
		case U:
			return "Área Funcional";
		case C:
			return "Clínica";
		case E:
			return "Especialidade";
//		case Q:
//			return "Equipe";
//		case P:
//			return "Pagador";
		default:
			return "";
		}
	}

}