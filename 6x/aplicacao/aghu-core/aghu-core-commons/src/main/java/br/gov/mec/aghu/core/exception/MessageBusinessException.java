package br.gov.mec.aghu.core.exception;


/**
 * Excecao checked que serve para retornar uma mensagem ao usuário em um
 * processamento que ocorre na camada de negocio. Não faz rollback da transação.<br>
 * 
 * @author dlaks
 */
public class MessageBusinessException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8959384872819781422L;

	
	public MessageBusinessException(String KeyMessage, Severity s, Object... params) {
		super(KeyMessage, s, params);
	}
	
	public MessageBusinessException(BaseException excecaoOriginal) {
		super(excecaoOriginal);
	}

	public MessageBusinessException(BusinessExceptionCode code,
			Object... params) {
		super(code, params);
	}

	public MessageBusinessException(BusinessExceptionCode code,
			Severity severidade, Object... params) {
		super(code, severidade, params);
	}

	public MessageBusinessException(BusinessExceptionCode code,
			Throwable cause, Object... params) {
		super(code, cause, params);
	}

	public MessageBusinessException(BusinessExceptionCode code,
			Throwable cause) {
		super(code, cause);
	}
	
}
