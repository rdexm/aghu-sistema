package br.gov.mec.aghu.core.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;

@ConversationScoped
@Named
@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Action {

}
