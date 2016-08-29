package br.gov.mec.aghu.core.business.moduleintegration;

import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Interceptor para verificar integração entre modulos. Bloqueando chamadas a
 * modulos desativados.
 *
 */
@ModuleChecked @Interceptor
public class ModuleIntegrationInterceptor {
	
	
	private static final Log LOG = LogFactory.getLog(ModuleIntegrationInterceptor.class);
	
	@Inject @ModulosAtivosQualifier 
	Set<ModuloEnum> modulosAtivos;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	@AroundInvoke
	public Object aroundInvoke(InvocationContext ctx) throws Exception {

		if (!ctx.getMethod().isAnnotationPresent(BypassInactiveModule.class)) {

			Modulo moduloAnotacao = ctx.getMethod().getDeclaringClass()
					.getAnnotation(Modulo.class);
			if (moduloAnotacao != null) {
				ModuloEnum modulo = moduloAnotacao.value();
				
				if (!modulosAtivos.contains(modulo)) {
					LOG.error("Tentativa de chamar  modulo " + modulo
							+ " inativo ou não existente");
					throw new InactiveModuleException("O modulo " + modulo
							+ " está inativo ou não existe");
				}
			}
		}
		return ctx.proceed();
	}

}
