package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIconesSituacaoCirurgias implements Dominio {
	CIR_CANCELADA, CIR_REALIZADA, PAC_PREPARO, PAC_CHAMADO, CIR_AGENDADA, PAC_TRANSOPERATORIO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CIR_CANCELADA:
			return "Cirurgia/PDT Cancelada";
		case CIR_REALIZADA:
			return "Cirurgia/PDT Realizada";
		case PAC_PREPARO:
			return "Paciente em Preparo";
		case PAC_CHAMADO:
			return "Paciente Chamado";
		case CIR_AGENDADA:
			return "Cirurgia/PDT Agendada";
		case PAC_TRANSOPERATORIO:
			return "Paciente em Transoperatório";				
		default:
			return "";
		}
	}
	
	public String getEndImagem(){
		switch (this) {
		case CIR_CANCELADA:
			return "silk-cancel";
		case CIR_REALIZADA:
			return "silk-tick";
		case PAC_PREPARO:
			return "silk-cadastro3";
		case PAC_CHAMADO:
			return "silk-telephone";	
		case CIR_AGENDADA:
			return "silk-table-row-insert";
		case PAC_TRANSOPERATORIO:
			return "silk-pacientes-ambulatorio-em-consulta";	
		default:
			return "";
		}
	}

}
