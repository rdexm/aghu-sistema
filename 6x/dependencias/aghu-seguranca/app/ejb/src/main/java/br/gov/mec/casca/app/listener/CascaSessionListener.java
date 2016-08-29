/*
 * CascaSessionListener.java
 * Copyright (c) Ministério da Educação - MEC.
 *
 * Este software é confidencial e propriedade do Ministério da Educação - MEC.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem expressa autorização do MEC.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.mec.casca.app.listener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.web.ServletContexts;

import br.gov.mec.casca.app.business.GerenciadorTokenCliente;
import br.gov.mec.casca.security.vo.SegurancaVO;

/**
 * Componente que fica "escutando" os eventos da sessão do casca para efetuar o logout nas aplicações
 * autenticadas ou quando for feito o logout no CASCA ou a sessão expirar.
 * 
 * @author manoelsantos
 * @version 1.0
 */
@Name("cascaSessionListener")
@Scope(ScopeType.SESSION)
public class CascaSessionListener implements HttpSessionListener {
	/**
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent _event) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent _event) {
		GerenciadorTokenCliente gerenciador = (GerenciadorTokenCliente) _event.getSession().getServletContext().getAttribute("gerenciadorSessaoCliente");
		if (gerenciador != null) {
			String sessionId = _event.getSession().getId();
			logOutAplicacoes(gerenciador, sessionId);
		}
	}

	/**
	 * Método que é executado quando é efetuado o logout.
	 */
	@Observer("org.jboss.seam.security.loggedOut")
	public void logOut() {
		if (ServletContexts.getInstance().getRequest() != null) {
			GerenciadorTokenCliente gerenciador = (GerenciadorTokenCliente) Component.getInstance(GerenciadorTokenCliente.class);
			String sessionId = ServletContexts.instance().getRequest().getSession().getId();
			logOutAplicacoes(gerenciador, sessionId);
		}
	}

	/**
	 * Efetua o logout nas aplicações autenticadas no CASCA.
	 * 
	 * @param _gerenciador
	 * @param _sessionId
	 */
	private void logOutAplicacoes(GerenciadorTokenCliente _gerenciador, String _sessionId) {
		List<SegurancaVO> aplicacoes = _gerenciador.getApplicacoes(_sessionId);
		if (aplicacoes != null) {
			for (SegurancaVO vo : aplicacoes) {
				URL url;
				if (vo.getUrlLogout() != null) {
					try {
						url = new URL(vo.getUrlLogout());
						URLConnection urlConnection = url.openConnection();
						urlConnection.setRequestProperty("Cookie", "JSESSIONID=" + vo.getClientSessionId());
						urlConnection.getInputStream();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}