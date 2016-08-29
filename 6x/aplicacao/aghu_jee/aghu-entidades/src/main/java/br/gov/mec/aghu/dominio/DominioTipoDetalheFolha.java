package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoDetalheFolha implements Dominio{
	THC,
	THD,
	THE,
	TSB,
	TSP,
	TSE,
	P13,
	PFE,
	TPE,
	NRF;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case THC:
			return "Total de horas de contrato";
		case THD:
			return "Total de horas descontadas em folha";
		case THE:
			return "Total de  horas excedentes ao contratado";
		case TSB:
			return "Total de salário base";
		case TSP:
			return "Total de salário pago ao funcionário";
		case TSE:
			return "Total de encargos incidentes sobre o salário pagos pelo hospital";
		case P13:
			return "Total de valores provisionados para o 13º";
		case PFE:
			return "Total de valores provisionados relativos a férias";
		case TPE:
			return "Total de encargos sobre valores provisionados";
		case NRF:
			return "Número de funcionário no grupo de ocupação ou cargo";
		default:
			return "";
		}
	}

}
