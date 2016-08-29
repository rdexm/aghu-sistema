package br.gov.mec.aghu.exames.ejb;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

class VoltarProtocoloUnicoErroVerificado extends ApplicationBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3848935744876455094L;

	private Exception exceptionOriginal;
	private String observacao;
	
	public VoltarProtocoloUnicoErroVerificado(String observacao, Exception exceptionOriginal, BusinessExceptionCode code) {
		this(code, null);
		this.observacao = observacao;
		this.exceptionOriginal = exceptionOriginal;
	}

	public VoltarProtocoloUnicoErroVerificado(Exception exceptionOriginal, BusinessExceptionCode code) {
		this(code, null);
		this.exceptionOriginal = exceptionOriginal;
	}

	public VoltarProtocoloUnicoErroVerificado(BusinessExceptionCode code, Object[] params) {
		super(code, params);
	}

	public Exception getExceptionOriginal() {
		return exceptionOriginal;
	}

	public void setExceptionOriginal(Exception exceptionOriginal) {
		this.exceptionOriginal = exceptionOriginal;
	}
	
	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	


}
