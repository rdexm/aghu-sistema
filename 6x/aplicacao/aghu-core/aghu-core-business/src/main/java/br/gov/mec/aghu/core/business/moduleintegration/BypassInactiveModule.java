package br.gov.mec.aghu.core.business.moduleintegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação usada para indicar que um método pode ser executado mesmo se o
 * módulo ao qual pertence está inativo.
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BypassInactiveModule {

}
