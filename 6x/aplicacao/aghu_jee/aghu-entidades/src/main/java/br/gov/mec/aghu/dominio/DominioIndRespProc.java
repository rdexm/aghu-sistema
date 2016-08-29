package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica os status da responsabilidade de um procedimento cirurgico.
 * 
 * @author tfelini
 */
public enum DominioIndRespProc implements Dominio {
	
	/**
	 * Agendamento
	 */
	AGND,
	/**
	 * Descrição Cirúrgica
	 */
	DESC,
	/**
	 * Digitação nota de sala
	 */
	NOTA;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public Integer getCodigoCirurgiasCanceladas() {
		switch (this) {
		case AGND:
			return 3;
		case DESC:
			return 1;
		case NOTA:
			return 2;
		default:
			return 0;
		}
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case AGND:
			return "Agendamento";
		case DESC:
			return "Descrição Cirúrgica";
		case NOTA:
			return "Digitação nota de sala";
		default:
			return "";
		}
	}

}

