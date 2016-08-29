package br.gov.mec.aghu.casca.exceptioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum ComponenteONExceptionCode implements BusinessExceptionCode {
	CASCA_MENSAGEM_COMPONENTE_NAO_INFORMADA, CASCA_MENSAGEM_COMPONENTE_JA_CADASTRADA
	, CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_VIOLACAO_FK_COMPONENTE
	, CASCA_VIOLACAO_FK_METODO
	;

}
