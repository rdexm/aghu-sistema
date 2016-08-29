package br.gov.mec.aghu.core.exception;

/**
 * Excecao NAO checada lançada pela aplicacao. substitui: MECModelException.
 * 
 * Esta exception NUNCA deveria ir para o cliente. Deve ser TRATADA
 * adequadamente na camada de NEGOCIO e ser encapsulada em uma excessao de
 * negocio para entao seguir para o cliente.
 * 
 * @author rcorvalao
 * 
 */
public class BaseRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8518061604555904635L;

	private BusinessExceptionCode code;

	private Severity severity = Severity.ERROR;

	private Object[] parameters;

	public BaseRuntimeException() {
		super();
	}

	protected BaseRuntimeException(String message) {
		super(message);
	}

	
	/**
	 * Construtor padrao que mapeia um ExceptionCode para uma mensagem de um
	 * resource bundle.
	 * 
	 * @param code
	 */
	public BaseRuntimeException(BusinessExceptionCode code, Object... params) {
		super(code.toString());
		this.code = code;
		this.setParameters(params);
	}

	/**
	 * Construtor padrao que mapeia um ExceptionCode para uma mensagem de um
	 * resource bundle.
	 * 
	 * @param code
	 */
	public BaseRuntimeException(BusinessExceptionCode code, Severity s,
			Object... params) {
		this(code, params);
		this.setSeverity(s);
	}

	/**
	 * Construtor criado para preservar a causa de uma excecao com o objetivo de
	 * preservar o seu StackTrace.
	 * 
	 * Isso é uma boa prática e avalida pelo PMD e FindBugs.
	 * 
	 * @param code
	 * @param cause
	 */
	public BaseRuntimeException(BusinessExceptionCode code, Throwable cause,
			Object... params) {
		super(code.toString(), cause);
		this.code = code;
		this.setParameters(params);
	}

	/**
	 * Construtor criado para preservar a causa de uma excecao com o objetivo de
	 * preservar o seu StackTrace.
	 * 
	 * Isso é uma boa prática e avalida pelo PMD e FindBugs.
	 * 
	 * @param code
	 * @param cause
	 */
	public BaseRuntimeException(BusinessExceptionCode code, Throwable cause) {
		super(code.toString(), cause);
		this.code = code;
	}

	/**
	 * Construtor criado para os caso onde uma exceção sem rollback precise
	 * gerar uma com rollback ou vice-versa.
	 * 
	 * 
	 * @param excecaoOriginal
	 */
	public BaseRuntimeException(BaseException excecaoOriginal) {
		super(excecaoOriginal.getMessage(), excecaoOriginal);
		this.setSeverity(excecaoOriginal.getSeverity());
		this.code = excecaoOriginal.getCode();
		this.setParameters(excecaoOriginal.getParameters());
	}

	/**
	 * Retorna o respectivo código desta excecao
	 * 
	 * @return
	 */
	public BusinessExceptionCode getCode() {
		return code;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

}
