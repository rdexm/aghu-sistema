package br.gov.mec.aghu.casca.exceptioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum PerfilONExceptionCode implements BusinessExceptionCode {
	
	CASCA_MENSAGEM_PERFIL_NAO_INFORMADO, CASCA_MENSAGEM_PERFIL_EXISTENTE
	, CASCA_MENSAGEM_PERFIL_NAO_ENCONTRADO, CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO
	, CASCA_VIOLACAO_FK_PERFIL
	;


}
