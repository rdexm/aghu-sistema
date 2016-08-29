package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCompetencia implements Dominio{
	CUSTOS,
	
	FATURAMENTO;

	private int value;

	private DominioTipoCompetencia() {}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CUSTOS:
			return "Custos";
		case FATURAMENTO:
			return "Faturamento";
		default:
			return "";
		}
	}
}
