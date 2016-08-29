package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum EstoqueFacadeExceptionCode implements
		BusinessExceptionCode {
	ERRO_AO_CONFIRMAR_TRANSACAO;

	public void throwException(Object... params)
			throws ApplicationBusinessException {
		throw new ApplicationBusinessException(this, params);
	}
}