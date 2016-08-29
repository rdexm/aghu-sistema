package br.gov.mec.aghu.core.commons;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Qualifier usado para identificar o hospital que o sistema está executando.
 *  
 * @author cqsilva
 * 
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD,
		ElementType.PARAMETER })
public @interface MessagesBundle {

}
