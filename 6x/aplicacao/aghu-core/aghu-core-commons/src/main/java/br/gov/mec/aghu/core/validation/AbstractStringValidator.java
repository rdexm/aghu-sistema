package br.gov.mec.aghu.core.validation;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Melhoria nesta classe para tratar mesmo os casos onde o valor não for instância de string e ainda assim funcionar.
 */

@SuppressWarnings({"PMD.UncommentedEmptyMethod"})
public abstract class AbstractStringValidator<A extends Annotation> implements
	ConstraintValidator<A,String> {

	public void initialize(A Annotation) {
	}

	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		try {
			if (isEmpty(value)) {
				return true;
			}
			
			return validate(value);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isEmpty(String value) {
		if (value == null) {
			return true;
		}

		if ("".equals(value.trim())) {
		//if (value.trim().length() == 0) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	protected abstract boolean validate(String value) throws Exception;
}