package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Domínio referente ao momento do agendamento
 */
public enum DominioMomentoAgendamento implements Dominio {
	
	 /**
	 * Agenda Médico
	 */
	AGD,
	 /**
	 * Escala Prévia
	 **/
	PRV,
	/**
	 * Escala Definitiva
	 **/
	DEF,
	/**
	 * Escala Pós Definitiva
	 **/
	POS;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	public String getDescricaoBanco(){
		switch (this) {
		case AGD:
			return "Agenda Médico";
		case PRV:
			return "Escala Prévia";
		case DEF:
			return "Escala Definitiva";
		case POS:
			return "Escala Pós Definitiva";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AGD:
			return "Agenda Médico";
		case PRV:
			return "Escala Prévia";
		case DEF:
			return "Escala Definitiva";
		case POS:
			return "Escala Pós Definitiva";
		default:
			return "";
		}
	}

}
