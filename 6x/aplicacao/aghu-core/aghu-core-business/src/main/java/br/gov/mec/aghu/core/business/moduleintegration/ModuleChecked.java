package br.gov.mec.aghu.core.business.moduleintegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Anotação usada para fazer o bind com o interceptor de vericação de modulos ativos.
 * 
 * @author geraldo
 *
 */

@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ModuleChecked {

}
