package br.gov.mec.controller;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.AGHUUtil;

@ApplicationScoped
public class ConfiguracaoImagemController extends ActionController {

	private static final long serialVersionUID = 2877518006272597470L;
	
	private static final String IMAGEM_LOGO = "/logo-hospital.png";

	public StreamedContent getLogoHospital() {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		InputStream imageStream = AGHUUtil.getResourceAsStream(IMAGEM_LOGO, ctx.getResourceAsStream(IMAGEM_LOGO));		
		return new DefaultStreamedContent(imageStream);
	}
	
}
