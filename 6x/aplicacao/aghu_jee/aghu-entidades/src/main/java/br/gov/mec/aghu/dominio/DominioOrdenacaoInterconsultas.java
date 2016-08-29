package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author bsoliveira
 * 
 */
public enum DominioOrdenacaoInterconsultas implements Dominio {

	/**
	 * Data Solicitação
	 */
	D, 
	/**
	 * Paciente
	 */
	P,
	/**
	 * Prontuário
	 */
	T,
	/**
	 * Especialidade
	 */
	E,
	/**
	 * Consulta
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Data Solicitação";
		case P:
			return "Paciente";
		case T:
			return "Prontuário";
		case E:
			return "Especialidade";
		case C:
			return "Consulta";		
		default:
			return "";
		}
	}
	
}
