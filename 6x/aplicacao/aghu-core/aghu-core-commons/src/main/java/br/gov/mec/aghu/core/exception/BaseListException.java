package br.gov.mec.aghu.core.exception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Excecao para controlar uma lista de erros durante a execucao das validacoes.<br>
 * Não faz rollback.<br>
 * substitui: MECNegocioListaException
 * 
 * @author rcorvalao
 */
public class BaseListException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 417935294061642890L;

	public enum BaseListExceptionCode implements BusinessExceptionCode {
		MSG_LISTA_ERROS;
	}

	private List<BaseException> negocioExceptions;
	
	public BaseListException() {
		super(BaseListExceptionCode.MSG_LISTA_ERROS);
	}

	public BaseListException(BaseException excecaoOriginal) {
		super(excecaoOriginal);
	}

	public BaseListException(BusinessExceptionCode code, Object... params) {
		super(code, params);
	}

	public BaseListException(BusinessExceptionCode code, Severity severidade,
			Object... params) {
		super(code, severidade, params);
	}

	public BaseListException(BusinessExceptionCode code, Throwable cause,
			Object... params) {
		super(code, cause, params);
	}

	public BaseListException(BusinessExceptionCode code, Throwable cause) {
		super(code, cause);
	}
	
	

	private List<BaseException> getNegocioExceptions() {
		if (this.negocioExceptions == null) {
			this.setNegocioExceptions(new ArrayList<BaseException>());
		}
		return negocioExceptions;
	}

	private void setNegocioExceptions(List<BaseException> n) {
		this.negocioExceptions = n;
	}
	
	/**
	 * Método para adicionar exceções na lista de erros.
	 * 
	 * @param e
	 */
	public void add(BaseException e) {
		if (e != null && e.getCode() != null) {
			this.getNegocioExceptions().add(e);
		}
	}
	
	/**
	 * Verifica se existem exceções.
	 * 
	 * @return
	 */
	public boolean hasException() {
		return !this.getNegocioExceptions().isEmpty();
	}
	
	/**
	 * Retorna um iterator com as exceções.
	 * 
	 * @return
	 */
	public Iterator<BaseException> iterator() {
		return this.getNegocioExceptions().iterator();
	}
	

}
