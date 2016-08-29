package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.utils.StringUtil;


/**
 * Dominio que indica as situacoes para interconsultas.
 * 
 */
public enum DominioSituacaoInterconsultas implements Dominio {
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
	 * Atendida On-line
	 */
	O,
	/**
	 * Avaliação pendente
	 */
	N,
	/**
	 * Sem indicação para agenda
	 */
	S,
	/**
	 * Solicitação de mais informações
	 */
	I,
	/**
	 * Consultoria sem agendamento
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Interconsulta pendente";
		case M:
			return "Interconsulta marcada";
		case A:
			return "Paciente avisado";
		case O:
			return "Atendida On-line";
		default:
			return "";
		}
	}

	/**
	 * Descrição dos domínios para exibição de consultorias ambulatoriais
	 * segundo o POST_QUERY anexo à história #11732.
	 * @author Cristiano Alexandre Moretti
	 * @since 10/04/2012
	 */
	public String getDescricaoConsultoriasAmbulatoriais() {
		switch (this) {
		case M:
			return "Consulta marcada"; 
		case A:
			return "Consulta marcada"; 
		case N:
			return "Avaliação pendente"; 
		case S:
			return "Sem indicação para agenda"; 
		case I:
			return "Solicitação de mais informações"; 
		case C:
			return "Consultoria sem agendamento"; 
		default:
			return "Lista de espera"; 
		}
	}

	public String getDescricaoConsultoriasAmbulatoriaisTrunc(Long size){
		return StringUtil.trunc(this.getDescricaoConsultoriasAmbulatoriais(), Boolean.TRUE, size);
	}
	
}
