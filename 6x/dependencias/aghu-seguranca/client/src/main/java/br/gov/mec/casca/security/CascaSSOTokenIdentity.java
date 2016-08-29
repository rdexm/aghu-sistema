/*
 * CascaSSOTokenIdentity.java
 * Copyright (c) Ministério da Educação - MEC.
 *
 * Este software é confidencial e propriedade do Ministério da Educação - MEC.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem expressa autorização do MEC.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.mec.casca.security;

import java.security.Principal;
import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.web.ServletContexts;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.security.vo.SegurancaVO;
import br.gov.mec.casca.service.CascaService;
import br.gov.mec.seam.security.Token;
import br.gov.mec.seam.security.TokenIdentity;
import br.gov.mec.seam.security.TokenIdentityException;

/**
 * Especializacao da classe Identity do Seam para autenticacao via parametro de
 * URL e validada a cada requisição.
 * 
 * @author manoelsantos
 * @version 1.0
 */
public class CascaSSOTokenIdentity extends TokenIdentity {
	private static final long serialVersionUID = 9149780709912293769L;
    private static final String TOKEN_LOGIN_EVENT = "br.gov.mec.seam.security.tokenLoginSuccessful";
    private static final String URLLOGOUT = "URLLOGOUT";
    
    @In(required = true, create = true)
    private CascaService cascaService;

    @In
    private IdentityManager identityManager;

	@RequestParameter
	private String cascaSessionId;

    private Boolean logado = false;

    /**
     * Parametro utilizado na URL contendo o token criptografado
     */
    @RequestParameter
    private String tkn;

    @Logger
    private transient Log log;


    /**
     * @see br.gov.mec.seam.security.TokenIdentity#isLoggedIn()
     */
    @Override
    public boolean isLoggedIn() {
    	if (!logado) {
	    	if (tkn != null) {
	            try {
	            	log.info("Realizando autenticacao por tokenSSO: {0}", tkn);
	            	String clientSessionId = ServletContexts.instance().getRequest().getSession().getId();
	                Token t = createTokenObj();

	                if (log.isDebugEnabled()) {
	                    log.debug(
	                    			"Token criado em {0} com expiracao em {1}. Hora local: {2}. Timezone local: {3}",
	                    			t.getDate().toString(), t.getDataExpiracao().toString(),
	                    			new Date().toString(), System.getProperty("user.timezone"));
	                }

	                SegurancaVO segurancaVO = new SegurancaVO();
	                segurancaVO.setCascaSessionId(cascaSessionId);
	                segurancaVO.setClientSessionId(clientSessionId);
	                segurancaVO.setToken(tkn);
	                segurancaVO.setUrlLogout(montaUrlLogout());
	                if (cascaService.isLoggedIn(segurancaVO)) {
	                    Principal principal = new SimplePrincipal(t.getUsername());

	                    acceptExternallyAuthenticatedPrincipal(principal);

	                    // Adiciona as roles do usuario
	                    for (String role : identityManager.getImpliedRoles(t.getUsername())) {
	                        super.addRole(role);
	                    }

	                    log.info("Usuario autenticado por tokenSSO: {0}", t.getUsername());

	                    Events.instance().raiseEvent(TOKEN_LOGIN_EVENT);
	                    logado = true;
	                }
	            } catch (TokenIdentityException e) {
	                log.error("Erro na autenticacao por tokenSSO.", e);
	            } catch (CascaException e) {
	                log.error("Erro na autenticacao por tokenSSO.", e);
				} catch (Exception e) {
					log.error("Erro na autenticacao por tokenSSO.", e);
				}
	    	}
    	}

    	return logado;
    }

	/**
	 * @see org.jboss.seam.core.Mutable#clearDirty()
	 */
	@Override
	public boolean clearDirty() {
		return true;
	}

	/**
	 * Monta a URL de logout da aplicação.
	 * 
	 * @return
	 * @throws Exception
	 */
	private String montaUrlLogout() throws Exception {
		String urlBase = "http://" + ServletContexts.instance().getRequest().getServerName();
		String paramUrlLogout = ServletLifecycle.getServletContext().getInitParameter(URLLOGOUT);
        if (paramUrlLogout == null) {
        	throw new Exception("Parametro URLLOGOUT não informado no arquivo web.xml");
        }
		urlBase += ":" + ServletContexts.instance().getRequest().getServerPort();
		urlBase += ServletContexts.instance().getRequest().getContextPath();
		urlBase += paramUrlLogout;

		return urlBase;
	}
}