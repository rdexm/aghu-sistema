package br.gov.mec.aghu.core.business.moduleintegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação usada nas fachadas para especificar o módulo ao qual pertencem.
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Modulo {

	ModuloEnum value();
}
