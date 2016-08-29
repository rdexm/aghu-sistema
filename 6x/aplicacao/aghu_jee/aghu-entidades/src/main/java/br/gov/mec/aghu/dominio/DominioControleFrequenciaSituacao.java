package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioControleFrequenciaSituacao implements Dominio {
	/**
	 * CF IMPRESSO COM APAC
	 */
	CIA,

	/**
	 * CF IMPRESSO SEM APAC
	 */
	CIS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CIA:
			return "CIA";
		case CIS:
			return "CIS";
		default:
			return "";
		}
	}
	
}
