package br.gov.mec.aghu.core.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target( { METHOD, FIELD })
@Constraint(validatedBy=TelefoneValidator.class)
public @interface Telefone {
	String message() default "O telefone deve estar no formato (XX)XXXX-XXXX";
	
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	String regexPattern() default "\\(\\d{2}\\)\\d{4}-\\d{4}";
}
