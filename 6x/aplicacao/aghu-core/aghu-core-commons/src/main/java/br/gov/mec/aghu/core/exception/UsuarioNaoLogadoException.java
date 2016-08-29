package br.gov.mec.aghu.core.exception;


public class UsuarioNaoLogadoException extends BaseRuntimeException {

	private enum UsuarioNaoLogadoExceptionCode implements BusinessExceptionCode{
		USUARIO_NAO_LOGADO;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6722919075121119L;
	
	
	public UsuarioNaoLogadoException(){
		super(UsuarioNaoLogadoExceptionCode.USUARIO_NAO_LOGADO);
	}

}
