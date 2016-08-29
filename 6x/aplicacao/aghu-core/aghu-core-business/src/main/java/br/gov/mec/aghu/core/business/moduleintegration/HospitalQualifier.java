package br.gov.mec.aghu.core.business.moduleintegration;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Qualifier usado para identificar o hospital que o sistema está executando.
 *  
 * @author twickert
 * 
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD,
		ElementType.PARAMETER })
public @interface HospitalQualifier {

}
