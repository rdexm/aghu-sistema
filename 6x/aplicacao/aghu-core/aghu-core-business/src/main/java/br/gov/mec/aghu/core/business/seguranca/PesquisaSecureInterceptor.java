package br.gov.mec.aghu.core.business.seguranca;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;

import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.UsuarioNaoLogadoException;
import br.gov.mec.aghu.core.exceptioncode.AghuSecureInterceptorExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Interceptor responsável por fazer a verificação de permissão no AGHU.
 * 
 * @author amenegotto
 * 
 */
@Secure
@Interceptor
public class PesquisaSecureInterceptor {

	@AroundInvoke
	public Object checkPermissions(InvocationContext ctx) throws Exception { // NOPMD
		Secure annotation = ctx.getMethod().getAnnotation(Secure.class);
		String valor = annotation.value();
		Pattern padrao = Pattern.compile("'[\\w/]+'"); // \\w representa qualquer caracter a-z maiúsculo ou minúsculo, além de todos os dígitos e mais o underscore (_). 
		Matcher m = padrao.matcher(valor);
		String target = null;
		String action = null;
		if (m.find()) {
			target = m.group().replace("'", "");
		}
		if (m.find()) {
			action = m.group().replace("'", "");
		}
		InitialContext ic = new InitialContext();
		SessionContext sesCtx = (SessionContext) ic
				.lookup("java:comp/EJBContext");
		Principal principal = sesCtx.getCallerPrincipal();
		if (principal == null) {
			throw new UsuarioNaoLogadoException();
		}
		String usuario = principal.getName();
		if (target == null || action == null) {
			throw new BaseRuntimeException(
					AghuSecureInterceptorExceptionCode.TARGET_ACTION_NAO_ESPECIFICADA);
		}
		
		IPermissionService permissionService = ServiceLocator.getBean(IPermissionService.class, "aghu-casca");
		if (!permissionService.usuarioTemPermissao(usuario, target, action)) {
			throw new AuthorizationException(
					AghuSecureInterceptorExceptionCode.ERRO_PERMISSAO, usuario, target,
					action);
		}
		return ctx.proceed();

	}	
}
