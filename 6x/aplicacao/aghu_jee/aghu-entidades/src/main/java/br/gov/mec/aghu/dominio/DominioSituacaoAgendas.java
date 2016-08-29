package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * IndSituacao da MbcAgendas
 * 
 * @author cristiane barbado
 *
 */
public enum DominioSituacaoAgendas implements  Dominio {
	 AG	//Cirurgia agendada	
	,CA	//Cirurgia cancelada	
	,ES	//Escala cirurgica
	,LE	//Lista de espera
	;
	
	 
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AG:
			return "Cirurgia agendada";
		case CA:
			return "Cirurgia cancelada";	
		case ES:
			return "Escala cirurgica";
		case LE:
			return "Lista de espera";	
		default: 
			return "";
			
		}
	}	
}
