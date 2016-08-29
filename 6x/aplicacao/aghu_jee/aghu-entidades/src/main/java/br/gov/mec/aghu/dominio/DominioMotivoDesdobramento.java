package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioMotivoDesdobramento implements Dominio {
	CIR,
	OBT,
	UFN,
	HDI,
	UTI,
	NOR,
	SSM,
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CIR:
			return "Cirurgia";
		case OBT:
			return "Obstetrícia";
		case UFN:
			return "Unidade Funcional";
		case HDI:
			return "Hospital Dia";
		case UTI:
			return "UTI";
		case NOR:
			return "Normal";
		case SSM:
			return "SSM";
		default:
			return "";
		}
	}
	
}
