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
@Constraint(validatedBy=IdadeValidator.class)
public @interface Idade {
	String message() default "Idade inválida. Deve estar entre {min} e {max} anos.";
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	int max() default 100;
	int min() default 0;
}
