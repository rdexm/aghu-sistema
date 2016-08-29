package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica as situacoes para interconsultas.
 * 
 */
public enum DominioSituacaoInterconsultasPesquisa implements Dominio {
	/**
	 * Interconsulta pendente
	 */
	P,
	/**
	 * Interconsulta marcada
	 */
	M,
	/**
	 * Paciente avisado
	 */
	A,
	/**
	 * Não Avaliada
	 */
	N,
	/**
	 * Atendida On-line
	 */
	O;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente";
		case M:
			return "Marcada";
		case A:
			return "Avisada";
		case N:
			return "Não Avaliada";
		case O:
			return "Atendida On-line";		
		default:
			return "";
		}
	}	
}
