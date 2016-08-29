/*
 * CascaSSOTokenIdentity.java
 * Copyright (c) Ministério da Educação - MEC.
 *
 * Este software é confidencial e propriedade do Ministério da Educação - MEC.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem expressa autorização do MEC.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.mec.casca.security.vo;

import java.io.Serializable;

/**
 * Especializacao da classe Identity do Seam para autenticacao via parametro de
 * URL e validada a cada requisição.
 * 
 * @author manoelsantos
 * @version 1.0
 */
public class SegurancaVO implements Serializable {
	private static final long serialVersionUID = 4089926755689766446L;
	private String cascaSessionId;
	private String clientSessionId;
	private String token;
	private String urlLogout;

	/**
	 * @return the cascaSessionId
	 */
	public String getCascaSessionId() {
		return cascaSessionId;
	}
	/**
	 * @param _cascaSessionId the cascaSessionId to set
	 */
	public void setCascaSessionId(String _cascaSessionId) {
		cascaSessionId = _cascaSessionId;
	}
	/**
	 * @return the clientSessionId
	 */
	public String getClientSessionId() {
		return clientSessionId;
	}
	/**
	 * @param _clientSessionId the clientSessionId to set
	 */
	public void setClientSessionId(String _clientSessionId) {
		clientSessionId = _clientSessionId;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param _token the token to set
	 */
	public void setToken(String _token) {
		token = _token;
	}
	/**
	 * @return the urlLogout
	 */
	public String getUrlLogout() {
		return urlLogout;
	}
	/**
	 * @param _urlLogout the urlLogout to set
	 */
	public void setUrlLogout(String _urlLogout) {
		urlLogout = _urlLogout;
	}
}