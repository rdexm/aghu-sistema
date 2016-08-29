/*
 * URLConfig.java
 * Copyright (c) Ministério da Educação - MEC.
 *
 * Este software é confidencial e propriedade do Ministério da Educação - MEC.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem expressa autorização do MEC.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.mec.casca.app.business;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.web.ServletContexts;

import br.gov.mec.casca.model.Aplicacao;

/**
 * Classe utilitária para montagem de URL.
 * 
 * @author manoelsantos
 * @version 1.0
 */
class URLConfig {
	/**
	 * Monta a url da aplicação tratando se a aplicação é interna ou externa.
	 * 
	 * @param urlBase
	 * @param app
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected static String montarURL(String urlBase, Aplicacao app)
			throws UnsupportedEncodingException {
		// FIXME Esta dependencia nao deveria estar na ON
		String context = ServletContexts.instance().getRequest().getContextPath();
		
		// Troca .xhtml por .seam
		urlBase = urlBase.replaceAll(".xhtml", ".seam");

		// Se o link for externo entao criar link para o redirect.seam,
		// passando a url como parametro junto com o token. A criação do
		// token é postergado para quando o menu for invocado.
		// FIXME app.getExterno != null é uma verificacao extra pois a
		// coluna do banco é null
		if (app.getExterno() != null && app.getExterno()) {
			
			if (app.getContexto() == null) {
				throw new NullPointerException("Contexto do servidor de aplicação externo " + app.getNome() + " nulo");
			}
			
			String urlExterna = String.format("/%s%s", app.getContexto(), urlBase);
			
			// Se o servidor não for null ou diferente de localhost,
			// então a aplicacao está mesmo em outro servidor
			if (!StringUtils.isBlank(app.getServidor()) && !app.getServidor().equals("localhost")) {
				// FIXME Recuperar protocolo do banco (mudar de Integer para String ou criar um enum)
				urlExterna = String.format("http://%s:%s%s",
						app.getServidor(), app.getPorta(), urlExterna);
			}

			urlBase = String.format("%s/casca/redirect.seam?url=%s",
					context, URLEncoder.encode(urlExterna, "UTF-8"));

		} else {
			// Se a url for interna, adicionar o contexto da aplicacao
			urlBase = context + urlBase;
		}
		return urlBase;
	}
}