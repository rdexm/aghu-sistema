package br.gov.mec.aghu.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;


public class SobreAghuController extends ActionController {

	private static final long serialVersionUID = 2792440264689869707L;
	
	
	@EJB
	private IAghuFacade aghuFacade;
	

	public Date buscaDataHoraBancoDeDados() {
		return aghuFacade.buscaDataHoraBancoDeDados();
	}

	public Date buscaDataHoraServidorAplicacao() {
		return new Date();
	}
	
	public List<String> buscarInformacoesRequest() {
		List<String> listaElementos = new ArrayList<String>();
		 
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			listaElementos.add(key + ": " + value);
		}
		
		return listaElementos;
	}
}
