package br.gov.mec.aghu.prescricaoenfermagem.util;

public enum TipoPrescricaoCuidadoEnfermagem {

	CUIDADOS_MEDICOS,
	CUIDADOS_MEDICOS_ROTINA;
	
	public String getSufixoIcone() {
	
		switch (this) {
			case CUIDADOS_MEDICOS:
				return "icon-" + this.toString().toLowerCase();
			case CUIDADOS_MEDICOS_ROTINA:
				//TODO: alterar quando tiver o icone certo
				//return this.toString().toLowerCase();
				return "silk-cuidado-cifrao";
			default:
				return "";
		}
		
	}
	
	public String getTitulo() {

		switch (this) {
			case CUIDADOS_MEDICOS:
				return "Cuidado";
			case CUIDADOS_MEDICOS_ROTINA:
				return "Cuidado de Rotina";
			default:
				return "";
		}
		
	}	
}