package br.gov.mec.aghu.core.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Qualifier usado para especificar que a string sendo injetada é um parâmetro
 * do sistema. o valor da propriedade value é o nome do parâmetro.
 * 
 * @author geraldo
 * 
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD,
		ElementType.PARAMETER })
public @interface Parametro {
	@Nonbinding
	String value() default "";

}
