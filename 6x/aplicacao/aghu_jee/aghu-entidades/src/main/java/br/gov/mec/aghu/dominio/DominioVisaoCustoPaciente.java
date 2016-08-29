package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioVisaoCustoPaciente implements Dominio{
	PACIENTE,
	
	COMPETENCIA;

	private int value;

	private DominioVisaoCustoPaciente() {}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PACIENTE:
			return "Paciente";
		case COMPETENCIA:
			return "Competência";
		default:
			return "";
		}
	}
}
