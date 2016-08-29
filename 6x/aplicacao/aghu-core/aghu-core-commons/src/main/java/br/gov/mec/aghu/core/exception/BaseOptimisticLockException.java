package br.gov.mec.aghu.core.exception;


/**
 * Excecao da aplicacao para encapsular um erro do tipo javax.persistence.OptimisticLockException.<br>
 * 
 * Lancada quando a verificacao versao ou timestamp falha, indicando que a Session contem um dado velho 
 * (quando usando transacoes longas com controle de lock). 
 * Também ocorre se nos tentarmos deletar ou atualizar um registro que nao existe.<br>
 * 
 * Eh uma excecao tratada pela infra-estrutura da aplicacao.
 * 
 * @author rcorvalao
 *
 */
public class BaseOptimisticLockException extends BaseRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5725498488311785255L;

	public BaseOptimisticLockException() {
		super();
	}

	public BaseOptimisticLockException(BaseException excecaoOriginal) {
		super(excecaoOriginal);
	}

	public BaseOptimisticLockException(BusinessExceptionCode code, Object... params) {
		super(code, params);
	}

	public BaseOptimisticLockException(BusinessExceptionCode code, Severity s,
			Object... params) {
		super(code, s, params);
	}

	public BaseOptimisticLockException(BusinessExceptionCode code, Throwable cause,
			Object... params) {
		super(code, cause, params);
	}

	public BaseOptimisticLockException(BusinessExceptionCode code, Throwable cause) {
		super(code, cause);
	}
	
	
	
	
	public enum BaseOptimisticLockExceptionCode implements BusinessExceptionCode {
		STALE_OBJECT_STATE
		, OPTIMISTIC_LOCK
		;
	}
	

}
