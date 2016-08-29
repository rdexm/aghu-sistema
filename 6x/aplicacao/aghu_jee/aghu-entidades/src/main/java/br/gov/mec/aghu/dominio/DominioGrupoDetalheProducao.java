package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGrupoDetalheProducao implements Dominio{
	/**
	 * PHI
	 */
	PHI,
	/**
	 * Origem
	 */
	ORIG,
	/**
	 * Paciente
	 */
	PAC, 
	/**
	 * Produção Apoio Manual
	 */
	PAM;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PHI:
			return "PHI";
		case ORIG:
			return "Origem";
		case PAC:
			return "Paciente";
		case PAM:
			return "Produção Apoio Manual";
		default:
			return "";
		}
	}
}
