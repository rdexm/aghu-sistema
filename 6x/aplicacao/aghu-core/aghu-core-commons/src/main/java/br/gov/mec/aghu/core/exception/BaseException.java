package br.gov.mec.aghu.core.exception;

import javax.ejb.ApplicationException;



/**
 * Classe base para excecoes checadas com mensagens localizadas<br>
 * substitui: MECBaseException
 *  
 * @author rcorvalao
 *
 */

@ApplicationException(rollback=true)
public class BaseException extends Exception {
	
	private static final long serialVersionUID = -1585696782457773077L;
	

	protected BusinessExceptionCode code;

	private Severity severity = Severity.ERROR;
	
	private Object[] parameters;
	
	
	/**
	 * Construtor para execption atraves de uma string para uma mensagem de um
	 * resource bundle.
	 * 
	 * Especifico para algumas sub-classes apenas.
	 * 
	 * @param code
	 */
	protected BaseException(String KeyMessage, Severity s, Object... params) {
		super(KeyMessage);
		this.setParameters(params);
		this.setSeverity(s);
	}
	
	/**
	 * Construtor padrao que mapeia um ExceptionCode para uma mensagem de um
	 * resource bundle.
	 * 
	 * @param code
	 */
	public BaseException(BusinessExceptionCode code, Object... params) {
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
	public BaseException(BusinessExceptionCode code, Severity s, Object... params) {
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
	public BaseException(BusinessExceptionCode code, Throwable cause, Object... params) {
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
	public BaseException(BusinessExceptionCode code, Throwable cause) {
		super(code.toString(), cause);
		this.code = code;
	}
	
	/**
	 * Construtor criado para os caso onde uma exceção sem rollback precise gerar uma com rollback ou vice-versa.
	 * 
	 * 
	 * @param excecaoOriginal
	 */
	public BaseException(BaseException excecaoOriginal) {
		super(excecaoOriginal.getMessage(), excecaoOriginal);
		this.setSeverity(excecaoOriginal.getSeverity());
		this.code = excecaoOriginal.getCode();
		this.setParameters(excecaoOriginal.getParameters());
	}
	
	/*
	public static String initLocalizedMessage(BusinessExceptionCode code, Object... params) {
		
		try {
			ResourceBundle bundle = super.getResourceBundle();
			String msg = getResourceBundleValue(code.toString());

			// Se for null ou vazio, mostra a propria chave
			if (msg == null || msg.length() == 0) {
				msg = code.toString();
			}

			// Faz a interpolacao de parametros na mensagem
			msg = java.text.MessageFormat.format(msg, params);

			return msg;
		} catch (Exception e) {
			// Esse erro ocorre nos testes unitários ou se não houver messagem
			// correspondente em algum message bundle do servidor
			// Neste caso lança a string do exception code mesmo.
			return code.toString();
		}
		
	}
	*/

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
