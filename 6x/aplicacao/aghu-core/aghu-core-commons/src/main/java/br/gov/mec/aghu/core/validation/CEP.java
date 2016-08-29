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
@Constraint(validatedBy=CEPValidator.class)
public @interface CEP {
	String message() default "CEP Inválido";
	
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
