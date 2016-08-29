package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

@SuppressWarnings({"PMD.CyclomaticComplexity"})
public enum DominioTipoDocumento implements Dominio {
	/**
	 * Prescrição Diálise
	 */
	DLS,
	/**
	 * Prescrição NPT
	 */
	NPT,
	/**
	 * Admissão Centro Obstétrico
	 */
	ACO,
	/**
	 * Solicitação de Internação
	 */
	AIH,
	/**
	 * Anamnse
	 */
	ANA,
	/**
	 * Assistência ao Parto
	 */
	AP,
	/**
	 * Atendimento Recem Nascido
	 */
	ARN,
	/**
	 * Consulta Emergência Obstétrica
	 */
	CEO,
	/**
	 * Descrição Cirúrgica
	 */
	DC,
	/**
	 * Exame Físico
	 */
	EF,
	/**
	 * Evolução
	 */
	EV,
	/**
	 * Solicitação de Exames
	 */
	EX,
	/**
	 * Ficha Anestésica
	 */
	FA,
	/**
	 * Ficha Apache
	 */
	FAP,
	/**
	 * Laudo de Exame
	 */
	LE,
	/**
	 * Descrição de PDT
	 */
	PDT,
	/**
	 * Prescrição Médica
	 */
	PM,
	/**
	 * Sumário de Alta 
	 */
	SA,
	/**
	 * Sumário de Óbtio
	 */
	SO,
	/**
	 * Prescrição de Quimioterapia
	 */
	QUI,
	/**
	 * Prescrição de Fisiatria
	 */
	FIS,
	/**
	 * Nota Adicional Ananmnese
	 */
	NAN,
	/**
	 * Nota Adicional Evolução
	 */
	NEV,
	/**
	 * Nota Adicional Consulta CO
	 */
	NCO,
	/**
	 * Nota Adicional Admissão CO
	 */
	NAD,
	/**
	 * Nota Adicional Assistência Parto
	 */
	NAP,
	/**
	 * Nota Adicional Atendimento RN
	 */
	NRN,
	/**
	 * Nota Adicional Exame Físico RN
	 */
	NEF,
	/**
	 * Nota Adicional POL
	 */
	NPO,
	/**
	 * PIM2
	 */
	PIM,
	/**
	 * Planejamento Cirurgico
	 */
	PC,
	/**
	 * Laudo Preliminar
	 */
	PRE,
	/**
	 * Laudo Definitivo
	 */
	DEF;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DLS:
			return "Prescrição de Diálise";
		case NPT:
			return "Prescrição de NPT";
		case ACO:
			return "Admissão Centro Obstétrico";
		case ANA:
			return "Anamnese";
		case AP:
			return "Assistência ao Parto";
		case ARN:
			return "Atendimento Recem Nascido";
		case CEO:
			return "Consulta Emergência Obstétrica";
		case DC:
			return "Descrição Cirúrgica";
		case EF:
			return "Exame Físico";
		case EV:
			return "Evolução";
		case EX:
			return "Solicitação de Exames";
		case FA:
			return "Ficha Anestésica";
		case FAP:
			return "Ficha Apache";
		case LE:
			return "Laudo de Exame";
		case PDT:
			return "Descrição de PDT";
		case PM:
			return "Prescrição Médica";
		case SA:
			return "Sumário de Alta";
		case SO:
			return "Sumário de Óbito";
		case QUI:
			return "Prescrição de Quimioterapia";
		case FIS:
			return "Prescrição de Fisiatria";
		case NAN:
			return "Nota Adicional Ananmnese";
		case NEV:
			return "Nota Adicional Evolução";
		case NCO:
			return "Nota Adicional Consulta CO";
		case NAD:
			return "Nota Adicional Admissão CO";
		case NAP:
			return "Nota Adicional Assistência Parto";
		case NRN:
			return "Nota Adicional Atendimento RN";
		case NEF:
			return "Nota Adicional Exame Físico RN";
		case NPO:
			return "Nota Adicional POL";
		case PIM :
			return "PIM2";
		case PC :
			return "Planejamento Cirúrgico";
		case PRE :
			return "Laudo Preliminar";
		case DEF : 
			return "Laudo Definitivo";
		case AIH :
			return "Solicitação de Internação";			
		default:
			return "";
		}
	}	
}
