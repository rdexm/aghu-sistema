package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoPrescricaoSessao implements Dominio {
	
	/**
	 * PLI
	 */
	PLI, 
	/**
	 * PAE
	 */
	PAE, 
	/**
	 * PAA
	 */
	PAA, 
	/**
	 * PLM
	 */
	PLM, 
	/**
	 * PSM
	 */
	PSM, 
	/**
	 * PLE
	 */
	PLE, 
	/**
	 * PSE
	 */
	PSE, 
	/**
	 * PLO
	 */
	PLO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case PLI:
			return "PLI";
		case PAE:
			return "PAE";
		case PAA:
			return "PAA";
		case PLM:
			return "PLM";
		case PSM:
			return "PSM";
		case PLE:
			return "PLE";
		case PSE:
			return "PSE";
		case PLO:
			return "PLO";		
		default:
			return "";
		}
	}
}
