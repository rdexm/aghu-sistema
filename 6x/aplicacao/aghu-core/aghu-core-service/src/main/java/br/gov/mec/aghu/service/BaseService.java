package br.gov.mec.aghu.service;

import java.io.Serializable;
import java.util.Iterator;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import br.gov.mec.aghu.service.paginacao.PaginatedResult;
import br.gov.mec.aghu.service.paginacao.PaginationInfo;

/**
 * SuperClasse das classes de serviço. 
 */
public abstract class BaseService implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4294710148867519817L;
	
	@Context 
	protected UriInfo uriInfo;
	
	protected <T> void preencherLinksNavegacao(PaginatedResult<T> paginatedResult) {
		paginatedResult.setFirstPageLink(obterLinkPrimeiraPagina());
		paginatedResult.setLastPageLink(obterLinkUltimaPagina(paginatedResult.getPaginationInfo()));
		paginatedResult.setNextPageLink(obterLinkProximaPagina(paginatedResult.getPaginationInfo()));
		paginatedResult.setPreviousPageLink(obterLinkPaginaAnterior(paginatedResult.getPaginationInfo()));
	}
	
	private String obterLinkPrimeiraPagina() {
		StringBuilder ret = new StringBuilder();
		ret.append(uriInfo.getAbsolutePath()).append("?").append(processarParametros(1));
		return ret.toString();
	}
	
	private String obterLinkUltimaPagina(PaginationInfo paginationInfo) {
		StringBuilder ret = new StringBuilder();
		Integer firstResult = paginationInfo.getTotalCount().intValue() - paginationInfo.getMaxResults();
		if (firstResult < 0) {
			firstResult = 1;
		}
		ret.append(uriInfo.getAbsolutePath()).append("?").append(processarParametros(firstResult));
		return ret.toString();
	}
	
	private String obterLinkProximaPagina(PaginationInfo paginationInfo) {
		StringBuilder ret = new StringBuilder();
		Integer firstResult = paginationInfo.getFirstResult() + paginationInfo.getMaxResults();
		if (paginationInfo.getTotalCount() < paginationInfo.getMaxResults()) {
			firstResult = 1;
		}
		if (firstResult >= paginationInfo.getTotalCount()) {
			firstResult = paginationInfo.getFirstResult();
		}
		ret.append(uriInfo.getAbsolutePath()).append("?").append(processarParametros(firstResult));
		return ret.toString();
	}
	
	private String obterLinkPaginaAnterior(PaginationInfo paginationInfo) {
		StringBuilder ret = new StringBuilder();
		Integer firstResult = paginationInfo.getFirstResult() - paginationInfo.getMaxResults();
		if (firstResult < 0) {
			firstResult = 1;
		}
		ret.append(uriInfo.getAbsolutePath()).append("?").append(processarParametros(firstResult));
		return ret.toString();
	}
	
	private String processarParametros(Integer firstResult) {
		StringBuilder ret = new StringBuilder(100);
		Iterator<String> it = uriInfo.getQueryParameters().keySet().iterator();
        while(it.hasNext()){
        	 String param = (String)it.next();
        	 String value = uriInfo.getQueryParameters().getFirst(param);
        	 if (param.equalsIgnoreCase("firstresult")) {
        		 value = Integer.toString(firstResult);
        	 }        	 
        	 ret.append(param).append("=").append(value).append("&");
        }
        return ret.toString().substring(0, ret.toString().length() - 1);
	}
}