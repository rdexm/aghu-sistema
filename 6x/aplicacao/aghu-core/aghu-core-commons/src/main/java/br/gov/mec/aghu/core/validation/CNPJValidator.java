package br.gov.mec.aghu.core.validation;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class CNPJValidator extends AbstractStringValidator<CNPJ> {

	@Override
	public boolean validate(String value) {
		return CoreUtil.validarCNPJ(value);
	}
	
}
