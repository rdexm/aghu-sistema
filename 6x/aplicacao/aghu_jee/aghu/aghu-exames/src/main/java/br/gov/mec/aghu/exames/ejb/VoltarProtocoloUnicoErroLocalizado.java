package br.gov.mec.aghu.exames.ejb;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

class VoltarProtocoloUnicoErroLocalizado extends ApplicationBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 784604091105157240L;

	private String observacao;

	public VoltarProtocoloUnicoErroLocalizado(String observacao, BusinessExceptionCode code) {
		this(code, null);
		this.observacao = observacao;
	}

	public VoltarProtocoloUnicoErroLocalizado(BusinessExceptionCode code, Object[] params) {
		super(code, params);
	}

	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
