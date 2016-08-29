package br.gov.mec.aghu.certificacaodigital.exceptioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum DocumentosPacienteONExceptionCode implements BusinessExceptionCode {
	
	MENSAGEM_DATA_FINAL_MENOR_DATA_INICIAL,
	MENSAGEM_PARAMETRO_NRO_MAXIMO_PESQUISA_NAO_DEFINIDO,
	MENSAGEM_PERIODO_MAXIMO_PESQUISA_EXCEDIDO,
	MENSAGEM_PERIODO_MAXIMO_PARAMETRO_EXCEDIDO,
	MENSAGEM_NECESSARIO_INFORMAR_FILTRO,
	MENSAGEM_HU_NAO_POSSUI_CERTIFICACAO_DIGITAL
	;

}
