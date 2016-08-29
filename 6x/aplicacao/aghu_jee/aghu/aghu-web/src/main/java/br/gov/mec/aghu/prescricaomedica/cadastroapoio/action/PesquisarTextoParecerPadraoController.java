package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;


import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.MpmTextoPadraoParecer;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarTextoParecerPadraoController extends ActionController{


	private static final long serialVersionUID = -893127319755879623L;
	
	private static final String PAGE_CADASTRAR_TEXTO_PADRAO_PARECER = "prescricaomedica-manterTextoPadraoParecer";
	
	private static final String PAGE_PESQUISAR_TEXTO_PADRAO_PARECER = "prescricaomedica-pesquisarTextoPadraoParecer";
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private ManterTextoPadraoParecerController manterTextoPadraoParecerController; 
	
	private MpmTextoPadraoParecer mpmTextoPadraoParecer;
	
	private List<MpmTextoPadraoParecer> listaMpmTextoPadraoParecer;
	
	private String siglaMpmTextoPadraoParecer;
	
	private String descricaoMpmTextoPadraoParecer;
	
	private boolean pesquisaAtiva;
	
	private boolean edicaoAtiva = false;
	
	private boolean gravarAtiva = false;
	
		
	private MpmTextoPadraoParecer mpmTextoPadraoParecerSelecionado;

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	

	public void inicio() throws BaseException{
		if(this.mpmTextoPadraoParecer == null){
			this.mpmTextoPadraoParecer = new MpmTextoPadraoParecer();
			this.siglaMpmTextoPadraoParecer = null;
			this.descricaoMpmTextoPadraoParecer = null;
			return;
		}
		if(this.edicaoAtiva){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EDICAO_SUCESSO");
			pesquisar();
			this.edicaoAtiva = false;
			return;
		}
		if(this.gravarAtiva){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TEXTO_PADRAO_CADASTRADA_SUCESSO");
			this.gravarAtiva = false;
			pesquisar();
			return;
		}
	}
	
	
	public String editar() throws BaseException{
		return PAGE_CADASTRAR_TEXTO_PADRAO_PARECER;
	}
	
	public String novo() throws BaseException{
		this.manterTextoPadraoParecerController.limpar();
		return PAGE_CADASTRAR_TEXTO_PADRAO_PARECER;
	}
	
	public String pesquisar() throws BaseException{		
		this.listaMpmTextoPadraoParecer = this.prescricaoMedicaFacade.pesquisarMpmTextoPadraoParecer(
				this.siglaMpmTextoPadraoParecer, this.descricaoMpmTextoPadraoParecer);
		if(this.descricaoMpmTextoPadraoParecer != null){
			this.descricaoMpmTextoPadraoParecer = this.descricaoMpmTextoPadraoParecer.trim();
		}
		this.pesquisaAtiva = true;
		return PAGE_PESQUISAR_TEXTO_PADRAO_PARECER;
	}
	
	
	public void excluir(MpmTextoPadraoParecer mpmTextoPadraoParecer) throws BaseException {			
		this.prescricaoMedicaFacade.removerMpmTextoPadraoParecer(mpmTextoPadraoParecer) ;
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TEXTO_PADRAO_EXCLUSAO");
		pesquisar();
	}
	
	
	public void limpar() {
			Iterator<UIComponent> componentes = FacesContext.getCurrentInstance()
					.getViewRoot().getFacetsAndChildren();
			while (componentes.hasNext()) {
				limparValoresSubmetidos(componentes.next());
			}
			this.siglaMpmTextoPadraoParecer = null;
			this.descricaoMpmTextoPadraoParecer = null;
			this.listaMpmTextoPadraoParecer = null;
			this.pesquisaAtiva = false;
		}
	
	
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	 public String obterHint(String item, Integer tamanhoMaximo) {
			if (item.length() > tamanhoMaximo) {
				item = StringUtils.abbreviate(item, tamanhoMaximo);
			}
			return item;
		}
	 
	
	public String getSiglaMpmTextoPadraoParecer() {
		return siglaMpmTextoPadraoParecer;
	}

	public void setSiglaMpmTextoPadraoParecer(String siglaMpmTextoPadraoParecer) {
		this.siglaMpmTextoPadraoParecer = siglaMpmTextoPadraoParecer;
	}

	public String getDescricaoMpmTextoPadraoParecer() {
		return descricaoMpmTextoPadraoParecer;
	}

	public void setDescricaoMpmTextoPadraoParecer(
			String descricaoMpmTextoPadraoParecer) {
		this.descricaoMpmTextoPadraoParecer = descricaoMpmTextoPadraoParecer;
	}

	
	public List<MpmTextoPadraoParecer> getListaMpmTextoPadraoParecer() {
		return listaMpmTextoPadraoParecer;
	}

	public ManterTextoPadraoParecerController getManterTextoPadraoParecerController() {
		return manterTextoPadraoParecerController;
	}


	public void setManterTextoPadraoParecerController(
			ManterTextoPadraoParecerController manterTextoPadraoParecerController) {
		this.manterTextoPadraoParecerController = manterTextoPadraoParecerController;
	}


	public void setListaMpmTextoPadraoParecer(
			List<MpmTextoPadraoParecer> listaMpmTextoPadraoParecer) {
		this.listaMpmTextoPadraoParecer = listaMpmTextoPadraoParecer;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}


	public MpmTextoPadraoParecer getMpmTextoPadraoParecerSelecionado() {
		return mpmTextoPadraoParecerSelecionado;
	}


	public void setMpmTextoPadraoParecerSelecionado(
			MpmTextoPadraoParecer mpmTextoPadraoParecerSelecionado) {
		this.mpmTextoPadraoParecerSelecionado = mpmTextoPadraoParecerSelecionado;
	}


	public boolean isEdicaoAtiva() {
		return edicaoAtiva;
	}


	public void setEdicaoAtiva(boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}


	public static String getPagePesquisarTextoPadraoParecer() {
		return PAGE_PESQUISAR_TEXTO_PADRAO_PARECER;
	}


	public boolean isGravarAtiva() {
		return gravarAtiva;
	}


	public void setGravarAtiva(boolean gravarAtiva) {
		this.gravarAtiva = gravarAtiva;
	}

}
