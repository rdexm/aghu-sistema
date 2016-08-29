package br.gov.mec.aghu.exames.pesquisa.business;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum PesquisaProtocoloExceptionCode implements BusinessExceptionCode {
	
	PROTOCOLO_DIGITADO_NAO_EXISTE, SOLICITACAO_DIGITADA_NAO_EXISTE, PRONTUARIO_DIGITADO_NAO_EXISTE, PREENCHER_CRITERIOS_PARA_SELECAO,
	INFORMAR_DADOS_SERVIDOR, INFORMAR_CONSELHO_NUMERO_REGISTRO;

}
