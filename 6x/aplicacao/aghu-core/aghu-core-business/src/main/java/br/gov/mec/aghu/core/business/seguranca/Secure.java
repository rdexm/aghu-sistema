package br.gov.mec.aghu.core.business.seguranca;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Anotação usada para especificar regras de autorização no AGHU.
 * 
 * @author geraldo
 *
 */
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {
	
	@Nonbinding String value() default "";
	
	

}
