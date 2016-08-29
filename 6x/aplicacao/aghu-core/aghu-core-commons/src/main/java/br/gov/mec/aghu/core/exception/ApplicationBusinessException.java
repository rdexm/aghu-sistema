package br.gov.mec.aghu.core.exception;


/**
 * Excecao checked que obriga as interfaces de clientes tratarem os erros que ocorrerem na camada de negocio.<br>
 * substitui: AGHUNegocioExceptionSemRollback
 * substitui: AGHUNegocioException
 * substitui: CascaException
 * 
 * Tanto nas classes RN quanto ON.
 * @author rcorvalao
 *
 */
public class ApplicationBusinessException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8959384872819781422L;

	
	public ApplicationBusinessException(String KeyMessage, Severity s, Object... params) {
		super(KeyMessage, s, params);
	}
	
	public ApplicationBusinessException(BaseException excecaoOriginal) {
		super(excecaoOriginal);
	}

	public ApplicationBusinessException(BusinessExceptionCode code,
			Object... params) {
		super(code, params);
	}

	public ApplicationBusinessException(BusinessExceptionCode code,
			Severity severidade, Object... params) {
		super(code, severidade, params);
	}

	public ApplicationBusinessException(BusinessExceptionCode code,
			Throwable cause, Object... params) {
		super(code, cause, params);
	}

	public ApplicationBusinessException(BusinessExceptionCode code,
			Throwable cause) {
		super(code, cause);
	}

	
}
