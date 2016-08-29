package br.gov.mec.aghu.impressao.expectioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum SistemaImpressaoExceptionCode implements BusinessExceptionCode {
	ERRO_IMPRESSAO, //
	IMPRESSORA_NAO_ENCONTRADA, //
	MENSAGEM_FALHA_ENVIAR_CUPS,
	SERVIDOR_NAO_ENCONTRADO, //
	IMPRESSORA_PARA_UNIDADE_TIPO_NAO_ENCONTRADA, //
	IMPRESSORA_PARA_CLIENTE_TIPO_NAO_ENCONTRADA, DOCUMENTO_IMPRESSAO_PROTEGIDA;
	
}
