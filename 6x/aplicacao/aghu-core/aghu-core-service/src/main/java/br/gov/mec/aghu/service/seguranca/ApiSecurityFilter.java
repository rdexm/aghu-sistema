package br.gov.mec.aghu.service.seguranca;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import br.gov.mec.aghu.core.locator.ServiceLocator;

@Provider
public class ApiSecurityFilter implements javax.ws.rs.container.ContainerRequestFilter
{
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Acesso negado ao recurso", 401, new Headers<Object>());;
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Acesso proibido ao recurso", 403, new Headers<Object>());;
    private static final ServerResponse SERVER_ERROR = new ServerResponse("Internal Server Error", 500, new Headers<Object>());;
     
    @Override
    public void filter(ContainerRequestContext requestContext) {    	
    	ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();
        //Access allowed for all
        if( ! method.isAnnotationPresent(PermitAll.class)) {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }
             
            //Fetch authorization header
            final String authorization = requestContext.getHeaderString(AUTHORIZATION_PROPERTY);
             
            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty() || !authorization.startsWith("Bearer ")) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            
            try {
            	final String token = authorization.substring(7);
            	if (token == null || token.isEmpty()) {
            		requestContext.abortWith(ACCESS_DENIED);
                    return;
            	}
	    		IApiPermissionService permissionService = ServiceLocator.getBeanRemote(IApiPermissionService.class, "aghu-casca");
	    		if (!permissionService.verificarTokenAtivo(token)) {
	    			requestContext.abortWith(ACCESS_DENIED);
	                return;
	    		}
	
	            //Verify user access
	            if(method.isAnnotationPresent(RolesAllowed.class)) {
	            	
	                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
	                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
	                 
	                //Is user valid?
	                if(!permissionService.verificarPerfilToken(token, rolesSet)) {
	                    requestContext.abortWith(ACCESS_DENIED);
	                    return;
	                }
	            }
            } catch (Exception e) {
                requestContext.abortWith(SERVER_ERROR);
            }
        }
    }
}