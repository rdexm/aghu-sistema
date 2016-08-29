package br.gov.mec.controller;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.CentralPendenciasInterface.TipoPendenciaEnum;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe controladora responsavel pelos menus e favoritos da casca.
 * 
 * @author Cristiano Quadros
 * 
 */
@SessionScoped
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class PendenciaController extends ActionController {
	private static final long serialVersionUID = -1877518006272597470L;
	
	private static final Log LOG = LogFactory.getLog(PendenciaController.class);

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICentralPendenciaFacade centralPendenciaFacade;	

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private List<PendenciaVO> pendencias;
	private Boolean existePendenciaBloqueante;
	private Long seqCaixaPostal;
	private int oldSizePendencias;
	private int pollTime=300000;  //5min
	private Boolean pollEnable=true; 
	private static final String PENDENCIA_OPME = "Acompanhar Processos de Autorização de OPMEs";

	@PostConstruct
	public void init() {
		atualizaPendencias();
		oldSizePendencias=0;
	}

	public String getUrl(Menu menu){
		String url="";
		String contextPath=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		if (menu != null && menu.getUrl()!=null && !menu.getUrl().isEmpty()){
			url = contextPath + "/pages" + menu.getUrl();
			url = url.replace(".seam", ".xhtml");
		}	
		return url;
	}
	
	public String getUrlOpme(Menu menu){
		String url="";
		String contextPath=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		if (menu != null && menu.getUrl()!=null && !menu.getUrl().isEmpty()){
			if(!menu.getUrl().contains("/pages/")){
				url = contextPath + "/pages" + menu.getUrl();
			}
			url = url.replace(".seam", ".xhtml");
		}	
		return url;
	}

	
	public void atualizaPendencias(){
		try {
			prescricaoMedicaFacade.gerarPendenciasSolicitacaoConsultoria();
			
			if (pendencias!=null){
				oldSizePendencias = pendencias.size();
			}
			pendencias = buscaPendencias();
			pollEnable=true;
			
		} catch (ApplicationBusinessException e) {
			LOG.warn("Erro buscando pendências");
		}
	}
	
	
	public void resolvePendencia(boolean bloqueante){
		pollEnable=!bloqueante;
	}

	private List<PendenciaVO> buscaPendencias() throws ApplicationBusinessException {		
		RapServidores servidorLogado = null;
		servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		List<PendenciaVO> list = Collections.emptyList();
		existePendenciaBloqueante=false;
		pollTime = centralPendenciaFacade.buscaTempoRefreshPendencias(servidorLogado);

		if(servidorLogado != null){
			list = centralPendenciaFacade.getListaPendencias();
	
			for (PendenciaVO vo : list) {
				Menu menuAux = new Menu();
				menuAux.setNome(vo.getNomeMenu());
				menuAux.setUrl(vo.getUrl());
				vo = verificaPendenciaOpme(vo,menuAux);
				//vo.setUrl(getUrl(menuAux));
				if (TipoPendenciaEnum.BLOQUEANTE.equals(vo.getTipo())){
					existePendenciaBloqueante=true;
				}
			}
		}
		return list;
	}
	
	public void excluirPendencia() throws ApplicationBusinessException{
		centralPendenciaFacade.excluirPendencia(this.getSeqCaixaPostal());
		
		this.atualizaPendencias();
	}	

	private PendenciaVO verificaPendenciaOpme(PendenciaVO vo,Menu menuAux){
		if(PENDENCIA_OPME.equals(vo.getNomeMenu())){
			vo.setUrl(getUrlOpme(menuAux));
		}
		if(vo.getUrl()!=null && !vo.getUrl().contains("/pages/")){
			vo.setUrl(getUrlOpme(menuAux));
		}
		return vo;
	}
	
	public Boolean getMostraPendencias(){
		boolean mostra=false;
		if (pendencias!=null && !pendencias.isEmpty() && pendencias.size() > oldSizePendencias){
			mostra=true;
		}
		return mostra;
	}
	
	
	public Long getSeqCaixaPostal() {
		return seqCaixaPostal;
	}

	public void setSeqCaixaPostal(Long seqCaixaPostal) {
		this.seqCaixaPostal = seqCaixaPostal;
	}

	public List<PendenciaVO> getPendencias() {
		return pendencias;
	}

	public void setPendencias(List<PendenciaVO> pendencias) {
		this.pendencias = pendencias;
	}

	public Boolean getExistePendenciaBloqueante() {
		return existePendenciaBloqueante;
	}

	public void setExistePendenciaBloqueante(Boolean existePendenciaBloqueante) {
		this.existePendenciaBloqueante = existePendenciaBloqueante;
	}

	public int getPollTime() {
		return pollTime;
	}

	public void setPollTime(int pollTime) {
		this.pollTime = pollTime;
	}

	public Boolean getPollEnable() {
		return pollEnable;
	}

	public void setPollEnable(Boolean pollEnable) {
		this.pollEnable = pollEnable;
	}
}