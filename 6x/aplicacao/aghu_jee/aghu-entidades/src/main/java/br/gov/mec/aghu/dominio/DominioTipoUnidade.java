package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para reforçar retrições de tipo em campos com comportamento boleano.
 * 
 * @author gmneto
 * 
 */
public enum DominioTipoUnidade implements Dominio {

	/**
	 * Unidade Centro Obstétrico ou Emergência
	 */
	A,
	/**
	 * Unidade em Clínica Área Satélite
	 */
	B,
	/**
	 * Convênio
	 */
	C,
	/**
	 * Leitos Privativos
	 */
	P,
	/**
	 * Outras
	 */
	U;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Unidade Centro Obstétrico ou Emergência";
		case B:
			return "Unidade em Clínica Área Satélite";
		case C:
			return "Convênio";
		case P:
			return "Leitos Privativos";
		case U:
			return "Outras";
		default:
			return "";
		}
	}

	public static final DominioTipoUnidade[] UNIDADES_INDICADOR_GERAL = {
			DominioTipoUnidade.values()[0], DominioTipoUnidade.values()[4] };

}
