package br.gov.mec.aghu.core.exception;

/**
 * Excecao utilizada para garantir que quando houver um relatório com dados vazios ele não será impresso
 * @author ghluz
 *
 */
public class EmptyReportException extends BaseRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2617732216340745575L;

	public enum EmptyReportExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RELATORIO_VAZIO;
	}

	public EmptyReportException() {
		super();
	}

	public EmptyReportException(BaseException excecaoOriginal) {
		super(excecaoOriginal);
	}

	public EmptyReportException(BusinessExceptionCode code, Object... params) {
		super(code, params);
	}

	public EmptyReportException(BusinessExceptionCode code, Severity s,
			Object... params) {
		super(code, s, params);
	}

	public EmptyReportException(BusinessExceptionCode code, Throwable cause,
			Object... params) {
		super(code, cause, params);
	}

	public EmptyReportException(BusinessExceptionCode code, Throwable cause) {
		super(code, cause);
	}
	
	
}
