/*
 * GerenciadorTokenCliente.java
 * Copyright (c) Ministério da Educação - MEC.
 *
 * Este software é confidencial e propriedade do Ministério da Educação - MEC.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem expressa autorização do MEC.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.mec.casca.app.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.gov.mec.casca.security.vo.SegurancaVO;

/**
 * Gerenciador dos tokens gerados pelo CASCA para determinar se o usuário está logado.
 * 
 * @author manoelsantos
 * @version 1.0
 */
@Scope(ScopeType.APPLICATION)
@Name("gerenciadorSessaoCliente")
public class GerenciadorTokenCliente {
	private Map<String, List<SegurancaVO>> mapAplicacoes = new HashMap<String, List<SegurancaVO>>();

	/**
	 * Adciona um token na lista para ser validado posteriomente.
	 * 
	 * @param cascaSessionId
	 * @param token
	 */
	public void addToken(String cascaSessionId, String token) {
		if (mapAplicacoes.get(cascaSessionId) == null) {
			mapAplicacoes.put(cascaSessionId, new Vector<SegurancaVO>());
		}
		SegurancaVO segurancaVO = new SegurancaVO();
		segurancaVO.setToken(token);
		mapAplicacoes.get(cascaSessionId).add(segurancaVO);
	}

	/**
	 * Verifica se é um token válido buscando dentro da lista, caso positivo remove da lista para para não ser
	 * usado novamente.
	 * 
	 * @param cascaSessionId
	 * @param clientSessionId
	 * @param token
	 * @return
	 */
	public Boolean isTokenValido(String cascaSessionId, String clientSessionId, String token, String urlLogout) {
		SegurancaVO segurancaVO = null;
		Boolean tokenExiste = false;
		List<SegurancaVO> vos = mapAplicacoes.get(cascaSessionId);
		if (vos == null) {
			return false;
		}

		//Verifica se existe um token na lista
		for (SegurancaVO vo : vos) {
			if (vo.getToken().equals(token)) {
				segurancaVO = vo;
				tokenExiste = true;
				break;
			}
		}

		if (tokenExiste) {
			if (segurancaVO.getClientSessionId() == null) {
				if (segurancaVO.getToken().equals(token)) {
					segurancaVO.setClientSessionId(clientSessionId);
					segurancaVO.setUrlLogout(urlLogout);
					return true;
				}
			} else if (!segurancaVO.getClientSessionId().equals(clientSessionId)) {
				/*
				 * Esta validação evita que o usuário use o mesmo id de sessão do CASCA
				 * em outra sessão cliente. 
				 */
				return false;
			}
		}

		return false;
	}

	/**
	 * Retorna a lista de aplicações autenticadas.
	 * 
	 * @param cascaSessionId
	 * @return
	 */
	public List<SegurancaVO> getApplicacoes(String cascaSessionId) {
		return mapAplicacoes.get(cascaSessionId);
	}
}