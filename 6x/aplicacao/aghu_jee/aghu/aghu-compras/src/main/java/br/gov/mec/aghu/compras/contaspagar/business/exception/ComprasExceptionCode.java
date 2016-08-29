package br.gov.mec.aghu.compras.contaspagar.business.exception;


import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Auto generated
 * @author Gustavo Kuhn Andriotti
 * Created on: 2011.03.03-17:01:16
 */
@SuppressWarnings("ucd")
public enum ComprasExceptionCode implements BusinessExceptionCode {


	
	/**
	 * Ocorreu algum problema na geração física dos arquivos. Por favor contate o responsável de TI.
	 */
	ARQ_ERRO_FISICO,
	ERRO_GERAR_ARQUIVO
	
	;
	
	public void throwException(Object... params) throws ApplicationBusinessException {

		throw new ApplicationBusinessException(this, params);
	}

	public void throwException(Throwable cause, Object... params) throws ApplicationBusinessException {

		// Tratamento adicional para não esconder a excecao de negocio
		// original
		if (cause instanceof ApplicationBusinessException) {
			throw (ApplicationBusinessException) cause;
		}
		throw new ApplicationBusinessException(this, cause, params);
	}
}

