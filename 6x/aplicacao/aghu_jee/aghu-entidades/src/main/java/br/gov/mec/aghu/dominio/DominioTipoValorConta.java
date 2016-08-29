package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoValorConta implements Dominio{
	/**
	 * Direto Insumos
	 */
	DI,
	/**
	 * Direto Pessoal
	 */
	DP,
	/**
	 * Direto Equipamentos
	 */
	DE,
	/**
	 * Direto Serviços
	 */
	DS,	
	/**
	 * Indiretos Insumos
	 */
	II,
	/**
	 * Indiretos Pessoal 
	 */
	IP,
	/**
	 * Indiretos Equipamento 
	 */
	IE,
	/**
	 * Indiretos Serviço 
	 */
	IS
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DI:
			return "Direto Insumos";
		case DP:
			return "Direto Pessoal";
		case DE:
			return "Direto Equipamentos";
		case DS:
			return "Direto Serviços";
		case II:
			return "Indireto Insumos";
		case IP:
			return "Indireto Pessoal";
		case IE:
			return "Indireto Equipamento";
		case IS:
			return "Indireto Serviço";
		default:
			return "";
		}
	}
}
