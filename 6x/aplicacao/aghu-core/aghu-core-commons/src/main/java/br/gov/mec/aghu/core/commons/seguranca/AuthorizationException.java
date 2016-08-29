package br.gov.mec.aghu.core.commons.seguranca;

import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Exceção usada para indicar erro de autorização no AGHU.
 * 
 * @author geraldo
 * 
 */
public class AuthorizationException extends BaseRuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3169587869772211668L;
	
	
	public AuthorizationException(BusinessExceptionCode code, Object... parans) {
		super(code, parans);
	}
	
	public AuthorizationException(String message) {
		super(message);
	}

}
