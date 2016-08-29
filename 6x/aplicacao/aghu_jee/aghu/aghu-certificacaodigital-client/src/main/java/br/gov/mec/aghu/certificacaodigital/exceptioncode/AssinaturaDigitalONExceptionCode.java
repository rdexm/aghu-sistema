package br.gov.mec.aghu.certificacaodigital.exceptioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum AssinaturaDigitalONExceptionCode implements BusinessExceptionCode {
	
	USUARIO_SEM_PERMISSAO_ASSINAR, CPF_INCOMPATIVEL_USUARIO_LOGADO
	, ERRO_VALIDACAO_ENVELOPE_CRIPTOGRAFICO, ERRO_VALIDACAO_CADEIA
	, ERRO_CERTIFICADO_EXPIRADO, 
	ERRO_CERTIFICADO_INVALIDO, ERRO_PENDENCIA_ASSINATURA
	;


}
