package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio a característica associada a grade de agendamento.
 * 
 * @author dansantos
 * 
 */
public enum DominioCaracteristicaGrade implements DominioString {
	/**
	 * Acompanhamento Tomografias
	 */
	ACOMPANHAMENTO_TOMOGRAFIAS,

	/**
	 * Adaptação Prótese
	 */
	ADAPTACAO_PROTESE,

	/**
	 * Agenda Prescrição Quimio
	 */
	AGENDA_PRESCRICAO_QUIMIO,

	/**
	 * Atendimento Familiar
	 */
	ATENDIMENTO_FAMILIAR,

	/**
	 * Atualizar Acomp Tomografias
	 */
	ATUALIZAR_ACOMP_TOMOGRAFIAS,

	/**
	 * Exames Periódicos
	 */
	EXAMES_PERIODICOS,

	/**
	 * Hemodiálise Amb HIV
	 */
	HEMODIALISE_AMB_HIV,

	/**
	 * Hemodiálise Amb Normal
	 */
	HEMODIALISE_AMB_NORMAL,

	/**
	 * Hemodiálise Peritoneal
	 */
	HEMODIALISE_PERITONEAL,
	
	/**
	 * Não Altera Reconsultas
	 */
	NAO_ALTERA_RECONSULTAS,
	
	/**
	 * Não Consiste Proc Definidos
	 */
	NAO_CONSISTE_PROCS_DEFINIDOS,
	
	/**
	 * Obter CBO Profissional
	 */
	OBTER_CBO_RPOFISSIONAL;

	@Override
	public String getCodigo() {
		
		switch (this) {
		case ACOMPANHAMENTO_TOMOGRAFIAS:
			return "Acompanhamento tomografias";
		case ADAPTACAO_PROTESE:
			return "Adaptação Protese";
		case AGENDA_PRESCRICAO_QUIMIO:
			return "Agenda Prescrição Quimio";
		case ATENDIMENTO_FAMILIAR:
			return "Atendimento Familiar";
		case ATUALIZAR_ACOMP_TOMOGRAFIAS:
			return "Atualizar acomp tomografias";
		case EXAMES_PERIODICOS:
			return "Exames Periodicos";
		case HEMODIALISE_AMB_HIV:
			return "Hemodiálise Amb HIV";
		case HEMODIALISE_AMB_NORMAL:
			return "Hemodiálise Amb Normal";
		case HEMODIALISE_PERITONEAL:
			return "Hemodiálise Peritoneal";
		case NAO_ALTERA_RECONSULTAS:
			return "Nao Altera Reconsultas";
		case NAO_CONSISTE_PROCS_DEFINIDOS:
			return "Nao consiste procs definidos";
		case OBTER_CBO_RPOFISSIONAL:
			return "Obter CBO profissional";
		default:
			return "";
		}
	}

		
	

	@Override
	public String getDescricao() {
		switch (this) {
		case ACOMPANHAMENTO_TOMOGRAFIAS:
			return "Acompanhamento tomografias";
		case ADAPTACAO_PROTESE:
			return "Adaptação Protese";
		case AGENDA_PRESCRICAO_QUIMIO:
			return "Agenda Prescrição Quimio";
		case ATENDIMENTO_FAMILIAR:
			return "Atendimento Familiar";
		case ATUALIZAR_ACOMP_TOMOGRAFIAS:
			return "Atualizar acomp tomografias";
		case EXAMES_PERIODICOS:
			return "Exames Periodicos";
		case HEMODIALISE_AMB_HIV:
			return "Hemodiálise Amb HIV";
		case HEMODIALISE_AMB_NORMAL:
			return "Hemodiálise Amb Normal";
		case HEMODIALISE_PERITONEAL:
			return "Hemodiálise Peritoneal";
		case NAO_ALTERA_RECONSULTAS:
			return "Nao Altera Reconsultas";
		case NAO_CONSISTE_PROCS_DEFINIDOS:
			return "Nao consiste procs definidos";
		case OBTER_CBO_RPOFISSIONAL:
			return "Obter CBO profissional";
		default:
			return "";
		}
	}

}
