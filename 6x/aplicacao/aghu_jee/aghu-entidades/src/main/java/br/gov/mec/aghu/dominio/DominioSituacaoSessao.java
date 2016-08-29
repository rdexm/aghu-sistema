package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoSessao implements Dominio {
	
	/**
	 * SOLICITADA
	 */
	SSO, 
	/**
	 * AGENDADA
	 */
	SAG, 
	/**
	 * EM ACOLHIMENTO
	 */
	SEA, 
	/**
	 * SCH
	 */
	SCH, 
	/**
	 * FALTA
	 */
	SFA, 
	/**
	 * ATENDIMENTO CONCLUIDO
	 */
	SAC, 
	/**
	 * SCA
	 */
	SCA, 
	/**
	 * SAU
	 */
	SAU, 
	/**
	 * SIN
	 */
	SIN, 
	/**
	 * INTERROMPIDA
	 */
	SIT, 
	/**
	 * SCO
	 */
	SCO,
	
	
	/**
	 * SEX
	 */
	SEX,
	
	/**
	 *NÃO ADMINISTRADA
	 */
	ANA,
	/**
	 *CONCLUÍDA PARCIAL
	 */
	APA,
	/**
	 *CONCLUÍDA TOTAL
	 */
	ATO,
	/**
	 *INTERCORRÊNCIA
	 */
	INT,
	/**
	 *AGUARDANDO AUTORIZAÇÃO
	 */
	MAA,
	/**
	 *AGUARDANDO MANIPULAÇÃO
	 */
	MAM,
	/**
	 *APTA PARA PREPARO
	 */
	MAP,
	/**
	 *DEVOLVIDA
	 */
	MDE,
	/**
	 *NÃO REVISADA
	 */
	MNR,
	/**
	 *PREPARO CONCLUÍDO
	 */
	MPC,
	/**
	 *ENTREGUE
	 */
	MPE,
	/**
	 *REJEITADA FARMÁCIA
	 */
	MRE,
	/**
	 *RESERVANDO INSUMOS
	 */
	MRI,
	/**
	 *SEM MANIPULAÇÃO
	 */
	MSE,
	/**
	 *AGUARDA AVALIAÇÃO MÉDICA
	 */
	PAA,
	/**
	 *AGUARDA EXAME
	 */
	PAE,
	/**
	 *LIBERADO POR EXAME
	 */
	PLE,
	/**
	 *LIBERADO
	 */
	PLI,
	/**
	 *LIBERADA PELO MÉDICO
	 */
	PLM,
	/**
	 *LIBERADO OUTRO
	 */
	PLO,
	/**
	 *SUSPENSA POR EXAME 
	 */
	PSE,
	
	/**
	 * SESSAO SUSPENSA
	 */
	SSU,
	
	/**
	 *SUSPENSA PELO MÉDICO
	 */
	PSM,
	/**
	 * SAA
	 */
	SAA,

	/**
	 * SAT
	 */
	SAT,

	/**
	 * AAC
	 */
	AAC,
	
	/**
	 * SNC
	 */
	SNC;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case SSO:
			return "Solicitada";
		case SAG:
			return "Agendada";
		case SEA:
			return "Em Acolhimento";
		case SCH:
			return "SCH";
		case SFA:
			return "Falta";
		case SAC:
			return "Realizada";
		case SCA:
			return "SCA";
		case SAU:
			return "SAU";
		case SIN:
			return "SIN";
		case SIT:
			return "Interrompida";
		case SCO:
			return "SCO";
		case SEX:
			return "SEX";
		case SAT:
			return "SAT";
		case AAC:
			return "Aviso enviado a CMIV";
		default:
			return excessoRegistroSessaoUm();
		}
		
	}
	
	private String excessoRegistroSessaoUm() {
		switch (this) {
		case ANA:
			return "Não Administrada";
		case APA:
			return "Concluída Parcial";
		case ATO:
			return "Concluída Total";
		case INT:
			return "Intercorrência";
		case MAA:
			return "Aguardando Autorização";
		case MAM:
			return "Aguardando Manipulação";
		case MAP:
			return "Apta Para Preparo";
		case MDE:
			return "Devolvida";
		case MNR:
			return "Não Revisada";
		case MPC:
			return "Preparo Concluído";
		case MPE:
			return "Entregue";
		case MRE:
			return "Rejeitada Farmácia";
		default:
			return excessoRegistroSessaoDois();
				
		}
		
	}
	
	
	private String excessoRegistroSessaoDois() {
		switch (this) {

		case MRI:
			return "Reservado Insumos";		
		case MSE:
			return "Sem Manipulação";
		case PAA:
			return "Aguarda Avaliação Médica";
		case PAE:
			return "Aguarda Exame";
		case PLE:
			return "Liberado Por Exame";
		case PLI:
			return "Liberado";
		case PLM:
			return "Liberado Pelo Médico";
		case PLO:
			return "Liberado Outro";
		case PSE:
			return "Suspensa Por Exame";
		case PSM:
			return "Suspensa Pelo Médico";	
		case SAA:
			return "Aguardando Atendimento";
		case SSU:
			return "Suspensa";	
		case SNC:
			return "Não Compareceu";	
		default:
			return "";
		
		}
	}
}
