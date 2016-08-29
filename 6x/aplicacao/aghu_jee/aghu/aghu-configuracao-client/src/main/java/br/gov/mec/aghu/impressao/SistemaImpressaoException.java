/**
 * 
 */
package br.gov.mec.aghu.impressao;

import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Exceção lançada em caso de erro na impressão de documentos.
 * 
 * @author cvagheti
 * 
 */
@SuppressWarnings("ucd")
public class SistemaImpressaoException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3356505548073808491L;

	public SistemaImpressaoException(BusinessExceptionCode exception,
			Object... params) {
		super(exception, params);
	}

	public SistemaImpressaoException(BusinessExceptionCode exception,
			Throwable cause) {
		super(exception, cause);
	}

}
