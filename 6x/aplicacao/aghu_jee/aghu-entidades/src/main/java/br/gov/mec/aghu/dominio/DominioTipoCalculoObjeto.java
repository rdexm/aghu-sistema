package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCalculoObjeto implements Dominio{
	/**
	 * Produção
	 */
	PR,
	/**
	 * Dias Produtivos
	 */
	DP,
	/**
	 * Período Processamento
	 */
	PP,
	/**
	 * Produção Manual
	 */
	PM,
	
	/**
	 * Peso
	 */
	PE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PR:
			return "Produção";
		case DP:
			return "Dias Produtivos";
		case PP:
			return "Período Processamento";
		case PM:
			return "Produção Manual";
		case PE:
			return "Peso";
		default:
			return "";
		}
	}

}

