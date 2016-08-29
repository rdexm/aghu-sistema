package br.gov.mec.aghu.exceptionhandler;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Factory de configuração do exceptionhandler específico do aghu.
 * 
 * @author geraldo
 * 
 */
public class AghuExceptionHandlerFactory extends ExceptionHandlerFactory {

	private final javax.faces.context.ExceptionHandlerFactory parent;

	public AghuExceptionHandlerFactory(
			final javax.faces.context.ExceptionHandlerFactory parent) {
		this.parent = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new AghuExceptionHandlerWrapper(
				this.parent.getExceptionHandler());
	}

}
