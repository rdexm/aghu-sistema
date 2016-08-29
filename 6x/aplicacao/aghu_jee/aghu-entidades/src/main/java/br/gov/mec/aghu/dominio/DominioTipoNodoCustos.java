package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoNodoCustos implements Dominio {
	/**
	 * Internações
	 */
	INTERNACAO,
	
	
	/**
	 * Categorias
	 */
	CATEGORIA,
	
	/**
	 * Data/hora atendimento
	 */
	DTHR_ATENDIMENTO,
	
	/**
	 * Centro de Custo
	 */
	CENTRO_CUSTO;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case INTERNACAO:
			return "Internação";
		case CATEGORIA:
			return "Categoria";
		case DTHR_ATENDIMENTO:
			return "Data/hora atendimento";
		case CENTRO_CUSTO:
			return "Centro de Custo";
		default:
			return "";
		}
	}
}
