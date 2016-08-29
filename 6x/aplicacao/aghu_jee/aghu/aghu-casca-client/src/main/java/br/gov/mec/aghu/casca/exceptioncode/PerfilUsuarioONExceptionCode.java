package br.gov.mec.aghu.casca.exceptioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum PerfilUsuarioONExceptionCode implements BusinessExceptionCode {
	USUARIO_NAO_PODE_DELEGAR_PERFIS,
	PERFIL_NAO_DELEGAVEL,
	USUARIO_JA_POSSUI_PERFIL,
	VOCE_NAO_POSSUI_EMAIL_CADASTRADO,
	CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO,
	DATA_EXPIRACAO_PASSADO,
	DATA_EXPIRACAO_ULTRAPASSOU_LIMITE,
	MOTIVO_DEVE_SER_MAIS_DETALHADO,
	;

}
