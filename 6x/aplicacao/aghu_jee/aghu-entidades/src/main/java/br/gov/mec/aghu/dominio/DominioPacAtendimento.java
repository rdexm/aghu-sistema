package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o estado do atendimento de um paciente
 * 
 * @author ehgsilva
 */
public enum DominioPacAtendimento implements Dominio {
	
	/**
	 * Em Andamento
	 */
	S,

	/**
	 * Encerrado
	 */
	N,
	
	/**
	 * Cancelaro por Óbito
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Em Andamento";
		case N:
			return "Encerrado";
		case O:
			return "Cancelaro por Óbito";
		default:
			return "";
		}
	}

}
