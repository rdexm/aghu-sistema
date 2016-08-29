package br.gov.mec.aghu.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class CPFValidator implements ConstraintValidator<CPF,Long>   {

	@Override
	@SuppressWarnings("PMD.UncommentedEmptyMethod")
	public void initialize(CPF constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		boolean retorno = false;		
		if (value == null){
			retorno = true;
		}else{
			retorno = CoreUtil.validarCPF(value.toString());
		}
		return retorno;
	}    
}