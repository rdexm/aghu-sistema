package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOpcaoEncerramentoAmbulatorio implements Dominio {

	/**
	 * Apac Quimio
	 */
	APAC, 
	/**
	 * Apac Nefro
	 */
	APAN,
	/**
	 * Apac de Acomp. Pós-transplante
	 */
	APAP,
	/**
	 * Apac de Exames
	 */
	APEX,
	/**
	 * Apac de Radioterapia
	 */
	APAR,
	/**
	 * Apa de Otorrino
	 */
	APAT,
	/**
	 * Apac de Fotocoagulação
	 */
	APAF,
	/**
	 * Apac de Pré-Transplante
	 */
	APRE,
	/**
	 * Programa SISCOLO
	 */
	SISCOLO,
	/**
	 * Programa SISMAMA
	 */
	SISMAMA,
	/**
	 * Ambulatório (BPA/BPI) 
	 */
	AMB,
	/**
	 * Todos 
	 */
	TODOS
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case APAC:
			return "Apac Quimio";
		case APAN:
			return "Apac Nefro";
		case APAP:
			return "Apac de Acomp. Pós-transplante";
		case APEX:
			return "Apac de Exames";
		case APAR:
			return "Apac de Radioterapia";
		case APAT:
			return "Apa de Otorrino";
		case APAF:
			return "Apac de Fotocoagulação";
		case APRE:
			return "Apac de Pré-Transplante";
		case SISCOLO:
			return "Programa SISCOLO";
		case SISMAMA:
			return "Programa SISMAMA";
		case AMB:
			return "Ambulatório (BPA/BPI)";
		case TODOS:
			return "Todos";
		default:
			return "";
		}
	}
	
}
