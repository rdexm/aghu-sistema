package br.gov.mec.controller;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.commons.Parametro;

/**
 * Classe controladora responsavel pelos menus e favoritos da casca.
 * 
 * @author Cristiano Quadros
 * 
 */
@SessionScoped
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class CascaController extends ActionController {
	private static final long serialVersionUID = -1877518006272597470L;
	
	private static final Log LOG = LogFactory.getLog(CascaController.class);
	
	public static final String PRONTUARIO_SELECIONADO = "prontuarioSelecionado";
	private static final String CSS_DEFAULT = "default";
	
	
	@Inject
	private HostRemotoCache hostRemoto;
	
	@Inject @Parametro("aghu_default_color_theme") 
	private String temaPadrao;
	
	@Inject @Parametro("aghu_home_accordion") 
	private String homeAccordion;		
	
	@Inject @Parametro("logout_onunload") 
	private String stLogoutOnUnload;
	
	private Boolean ativaLogoutOnUnload;
	
	private AipPacientes pacienteSelecionado;
	
	private String cssDefault=CSS_DEFAULT;
	private String primeTheme="south-street";   //ui-lightness  //south-street	
	
	@PostConstruct
	protected void init() {
		if (temaPadrao != null && !"".equalsIgnoreCase(temaPadrao.trim())) {
			cssDefault = temaPadrao;
			primeTheme = "ui-lightness";
			
			if (CSS_DEFAULT.equalsIgnoreCase(temaPadrao)) {
				primeTheme="south-street";
			}
		}
		
		if (stLogoutOnUnload != null && stLogoutOnUnload.equals("true")){
			ativaLogoutOnUnload = Boolean.TRUE;
		}else{
			ativaLogoutOnUnload = Boolean.FALSE;
		}
	}
	
	
	//Retorna o IP da origem de acesso do sistema
	public String getOrigemAcessoIP(){
		String ipAddress = "Não foi possível obter endereço IP.";
	    try {
			ipAddress = hostRemoto.getEnderecoIPv4HostRemoto().getHostAddress();
		} catch (UnknownHostException e) {
			LOG.error("Erro ao obter endereço de rede de origem do acesso ao AGHU.", e);
		}
	    return ipAddress;
	}

	
	//Retorna o nome da maquina de origem do acesso do sistema	
	public String getOrigemAcessoMaquina(){
	    String machineAddress = "Não foi possível obter nome de rede do host.";
	    try {
			machineAddress = hostRemoto.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Erro ao obter endereço de rede de origem do acesso ao AGHU.", e);
		}
	    return machineAddress;
	}
	
	
	//Retorna o nome da maquina de origem do acesso do sistema	
	public String getProtocoloAcessoMaquina(){
	    String machineAddress = "Não foi possível obter nome de rede do host.";
	    try {
			machineAddress = hostRemoto.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Erro ao obter endereço de rede de origem do acesso ao AGHU.", e);
		}
	    return machineAddress;
	}	
	
	public void inserirProntuarioContexto(Integer prontuario){
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(PRONTUARIO_SELECIONADO, prontuario);
	}

	public AipPacientes getPacienteSelecionado() {
		return pacienteSelecionado;
	}


	public void setPacienteSelecionado(AipPacientes pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}
	

	public String getCssDefault() {
		return cssDefault;
	}


	public void setCssDefault(String cssDefault) {
		this.cssDefault = cssDefault;
	}


	public String getPrimeTheme() {
		return primeTheme;
	}


	public void setPrimeTheme(String primeTheme) {
		this.primeTheme = primeTheme;
	}


	public String getStLogoutOnUnload() {
		return stLogoutOnUnload;
	}


	public void setStLogoutOnUnload(String stLogoutOnUnload) {
		this.stLogoutOnUnload = stLogoutOnUnload;
	}


	public Boolean getAtivaLogoutOnUnload() {
		return ativaLogoutOnUnload;
	}


	public void setAtivaLogoutOnUnload(Boolean ativaLogoutOnUnload) {
		this.ativaLogoutOnUnload = ativaLogoutOnUnload;
	}


	public String getHomeAccordion() {
		return homeAccordion;
	}


	public void setHomeAccordion(String homeAccordion) {
		this.homeAccordion = homeAccordion;
	}

	
}