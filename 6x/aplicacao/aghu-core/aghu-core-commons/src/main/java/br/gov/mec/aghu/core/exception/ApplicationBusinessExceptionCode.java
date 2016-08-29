package br.gov.mec.aghu.core.exception;


/**
 * 
 * substitui: AGHUNegocioExceptionCode
 * @author rcorvalao
 *
 */
public enum ApplicationBusinessExceptionCode implements BusinessExceptionCode {
	
	
	/**
	 * Não existe CID cadastrado com este código
	 */
	AGH_00162,

	/**
	 * Não é possível excluir {0} enquanto existir dependentes em {1}.
	 */
	OFG_00005,
	
	/**
	 * Erro fatal na QMS_GET_ROWID. Contate seu  DBA.
	 */
	QMS_00024,
	
	/**
	 * Erro fatal na  QMS_CLEAR_ROWID. Contate seu  DBA.
	 */
	QMS_00027,
	
	/**
	 * Erro ao tentar clonar bean.
	 */
	NAO_FOI_POSSIVEL_CLONAR_BEAN,
	/**
	 * Não existe AghMicroComputador cadastrado para o usuário logado.
	 */
	AGH_MICROCOMPUTADOR_NAO_CADASTRADO,
	/**
	 * Impossível efetuar a operação, microcomputador não cadastrado ou não identificado corretamente.
	 */
	MICROCOMPUTADOR_NAO_CADASTRADO_IDENTIFICADO,
	/**
	 * Não conseguiu recuperar o nome do computador do user logado.
	 */
	NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR,
	
	PROBLEMA_NAO_RESOLVIDO_NA_MIGRACAO,	
	/**
	 * Ocorreu o seguinte erro ao realizar a operação:
	 */
	OCORREU_O_SEGUINTE_ERRO,
	
	REGISTRO_NULO_EXCLUSAO,
	
	REGISTRO_NULO_BUSCA
	;


	public void throwException(Object... params) throws ApplicationBusinessException {
		throw new ApplicationBusinessException(this, params);
	}

	public void throwException(Throwable cause, Object... params)
			throws ApplicationBusinessException {
		// Tratamento adicional para não esconder a excecao de negocio
		// original
		if (cause instanceof ApplicationBusinessException) {
			throw (ApplicationBusinessException) cause;
		}
		throw new ApplicationBusinessException(this, cause, params);
	}

}
