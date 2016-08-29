package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;


/**
 * Domínio que indica a caracteristica da emergencia
 * 
 * 
 */
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public enum DominioCaracteristicaEmergencia implements DominioString {
	/** Atendido **/
	ATENDIDO,
	/** Atualiza Data Fim **/
	ATUALIZA_DATA_FIM,
	/** Atualiza Pac Atend **/
	ATUALIZA_PAC_ATEND,
	/** Atualiza Pac Emerg **/
	ATUALIZA_PAC_EMERG,
	/** Check Out **/
	CHECK_OUT,
	/** Em Atend **/
	EM_ATEND,
	/** Em Triagem **/
	EM_TRIAGEM,
	/** Enc Ambulatorio **/
	ENC_AMBULATORIO,
	/** Enc Externo **/
	ENC_EXTERNO,
	/** Enc Interno **/
	ENC_INTERNO,
	/** Enc Triagem **/
	ENC_TRIAGEM,
	/** Lista Aguardando **/
	LISTA_AGUARDANDO,
	/** Lista Atendido **/
	LISTA_ATENDIDO,
	/** Lista Emergencia **/
	LISTA_EMERGENCIA,
	/** Lista Triagem **/
	LISTA_TRIAGEM,
	/** Lista em Atend **/
	LISTA_EM_ATEND,
	/** No Consultorio **/
	NO_CONSULTORIO,
	/** Permanece Triagem **/
	PERMANECE_TRIAGEM,
	/** Recepcao **/
	RECEPCAO,
	/** Voltar Triagem **/
	VOLTAR_TRIAGEM;

	@Override
	public String getCodigo() {
		switch (this) {
		case ATENDIDO:
			return "Atendido";
		case ATUALIZA_DATA_FIM:
			return "Atualiza Data Fim";
		case ATUALIZA_PAC_ATEND:
			return "Atualiza Pac Atend";
		case ATUALIZA_PAC_EMERG:
			return "Atualiza Pac Emerg";
		case CHECK_OUT:
			return "Check Out";
		case EM_ATEND:
			return "Em Atend";
		case EM_TRIAGEM:
			return "Em Triagem";
		case ENC_AMBULATORIO:
			return "Enc Ambulatorio";
		case ENC_EXTERNO:
			return "Enc Externo";
		case ENC_INTERNO:
			return "Enc Interno";
		case ENC_TRIAGEM:
			return "Enc Triagem";
		case LISTA_AGUARDANDO:
			return "Lista Aguardando";
		case LISTA_ATENDIDO:
			return "Lista Atendido";
		case LISTA_EMERGENCIA:
			return "Lista Emergencia";
		case LISTA_TRIAGEM:
			return "Lista Triagem";
		case LISTA_EM_ATEND:
			return "Lista em Atend";
		case NO_CONSULTORIO:
			return "No Consultorio";
		case PERMANECE_TRIAGEM:
			return "Permanece Triagem";
		case RECEPCAO:
			return "Recepcao";
		case VOLTAR_TRIAGEM:
			return "Voltar Triagem";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ATENDIDO:
			return "Atendido";
		case ATUALIZA_DATA_FIM:
			return "Atualiza Data Fim";
		case ATUALIZA_PAC_ATEND:
			return "Atualiza Pac Atend";
		case ATUALIZA_PAC_EMERG:
			return "Atualiza Pac Emerg";
		case CHECK_OUT:
			return "Check Out";
		case EM_ATEND:
			return "Em Atend";
		case EM_TRIAGEM:
			return "Em Triagem";
		case ENC_AMBULATORIO:
			return "Enc Ambulatorio";
		case ENC_EXTERNO:
			return "Enc Externo";
		case ENC_INTERNO:
			return "Enc Interno";
		case ENC_TRIAGEM:
			return "Enc Triagem";
		case LISTA_AGUARDANDO:
			return "Lista Aguardando";
		case LISTA_ATENDIDO:
			return "Lista Atendido";
		case LISTA_EMERGENCIA:
			return "Lista Emergencia";
		case LISTA_TRIAGEM:
			return "Lista Triagem";
		case LISTA_EM_ATEND:
			return "Lista em Atend";
		case NO_CONSULTORIO:
			return "No Consultorio";
		case PERMANECE_TRIAGEM:
			return "Permanece Triagem";
		case RECEPCAO:
			return "Recepcao";
		case VOLTAR_TRIAGEM:
			return "Voltar Triagem";
		default:
			return "";
		}
	}

}
