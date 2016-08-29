package br.gov.mec.aghu.casca.business;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("ucd")
public enum MessagesPendenciaEnum implements BusinessExceptionCode {

	/**
	 * Esta prioridade {0} j\u00E1 consta com a seguinte URL {1}
	 */
	PRIORIDADE_REPETIDA,
	/**
	 * A aplica\u00E7\u00E3o de nome {0} consta mais de uma vez\: {1}
	 */
	APLICACAO_REPETIDA, ;
}
