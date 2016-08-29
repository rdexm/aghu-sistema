package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MpmTextoPadraoParecer;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterTextoPadraoParecerController extends ActionController {

	private static final long serialVersionUID = 7296150341482430833L;
	
	private static final String PAGE_PESQUISAR_TEXTO_PADRAO_PARECER = "prescricaomedica-pesquisarTextoPadraoParecer";
	
	private static final String PAGE_CADASTRAR_TEXTO_PADRAO_PARECER = "prescricaomedica-manterTextoPadraoParecer";
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private PesquisarTextoParecerPadraoController pesquisarTextoParecerPadraoController;
	
	private String siglaAntiga;
	
	private String siglaMpmTextoPadraoParecer;
	
	private String descricaoMpmTextoPadraoParecer;
	
	private boolean editarAtiva;
	
	private boolean falhaEditar = false;
	
	private MpmTextoPadraoParecer mpmTextoPadraoParecer;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation);		
	}
	
	public void inicio(){
		if(this.editarAtiva){			
			if(this.falhaEditar){
				if (this.mpmTextoPadraoParecer != null){
					this.siglaMpmTextoPadraoParecer = this.mpmTextoPadraoParecer.getSigla() != null ? this.mpmTextoPadraoParecer.getSigla() : "";
					this.descricaoMpmTextoPadraoParecer = this.mpmTextoPadraoParecer.getDescricao() != null ? this.mpmTextoPadraoParecer.getDescricao() : "";
					this.falhaEditar = false;					
				}
			} else {
				this.mpmTextoPadraoParecer = this.prescricaoMedicaFacade.obterMpmTextoPadraoParecerOriginal(this.siglaAntiga);
				this.siglaMpmTextoPadraoParecer = this.mpmTextoPadraoParecer.getSigla();
				this.descricaoMpmTextoPadraoParecer = this.mpmTextoPadraoParecer.getDescricao();
			}		
		}else{
			if (this.mpmTextoPadraoParecer != null){
				this.siglaMpmTextoPadraoParecer = this.mpmTextoPadraoParecer.getSigla();
				this.descricaoMpmTextoPadraoParecer = this.mpmTextoPadraoParecer.getDescricao();
				this.falhaEditar = false;					
			} else {
				this.siglaMpmTextoPadraoParecer = null;
				this.descricaoMpmTextoPadraoParecer = null;
			}			
		}			
	}	
	
	public String gravar() throws BaseException {		
		
		if (this.descricaoMpmTextoPadraoParecer != null && this.siglaMpmTextoPadraoParecer != null){ 
			this.descricaoMpmTextoPadraoParecer = this.descricaoMpmTextoPadraoParecer.trim().replace("\\n", " ");
			this.siglaMpmTextoPadraoParecer = this.siglaMpmTextoPadraoParecer.trim().replace("\\n", " ");
			
			if (this.descricaoMpmTextoPadraoParecer.length() > 500 || this.siglaMpmTextoPadraoParecer.length() > 10){
				if (this.descricaoMpmTextoPadraoParecer.length() > 500){
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ESTOURO_TAMANHO_1");
				}
				if (this.siglaMpmTextoPadraoParecer.length() > 10){
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ESTOURO_TAMANHO_2");
				}			
				if (!editarAtiva){
					this.mpmTextoPadraoParecer = new MpmTextoPadraoParecer();
				}						
				
				this.mpmTextoPadraoParecer.setSigla(this.siglaMpmTextoPadraoParecer.toUpperCase());					
				this.mpmTextoPadraoParecer.setDescricao(this.descricaoMpmTextoPadraoParecer.toUpperCase());									
				return PAGE_CADASTRAR_TEXTO_PADRAO_PARECER;
			}
		
			if(this.editarAtiva){
				try {
					if(this.siglaAntiga.equals(this.siglaMpmTextoPadraoParecer)){
						this.prescricaoMedicaFacade.editarMpmTextoPadraoParecer(this.mpmTextoPadraoParecer, this.siglaMpmTextoPadraoParecer.toUpperCase(), this.descricaoMpmTextoPadraoParecer.toUpperCase());
						this.limpar();
						this.editarAtiva = false;
						this.pesquisarTextoParecerPadraoController.setEdicaoAtiva(true);
					}
					else if(this.prescricaoMedicaFacade.obterSiglaMpmTextoPadraoParecer(this.siglaMpmTextoPadraoParecer) == null){
						this.prescricaoMedicaFacade.editarMpmTextoPadraoParecer(this.mpmTextoPadraoParecer, this.siglaMpmTextoPadraoParecer.toUpperCase(), this.descricaoMpmTextoPadraoParecer.toUpperCase());
						this.limpar();
						this.editarAtiva = false;
						this.pesquisarTextoParecerPadraoController.setEdicaoAtiva(true);
					}
					else{ 
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SIGLA_CADASTRADA");	
						this.falhaEditar = true;
						this.mpmTextoPadraoParecer.setDescricao(this.descricaoMpmTextoPadraoParecer.toUpperCase());
						this.mpmTextoPadraoParecer.setSigla(this.siglaMpmTextoPadraoParecer.toUpperCase());
						return PAGE_CADASTRAR_TEXTO_PADRAO_PARECER;
					}
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			else {				
				if(this.prescricaoMedicaFacade.obterSiglaMpmTextoPadraoParecer(this.siglaMpmTextoPadraoParecer) == null){
					try {
						this.prescricaoMedicaFacade.adicionarMpmTextoPadraoParecer(this.siglaMpmTextoPadraoParecer.toUpperCase(), this.descricaoMpmTextoPadraoParecer.toUpperCase());
						this.descricaoMpmTextoPadraoParecer = this.descricaoMpmTextoPadraoParecer.trim();
						this.pesquisarTextoParecerPadraoController.setGravarAtiva(true);
						this.limpar();
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
				else{
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SIGLA_CADASTRADA");
					this.falhaEditar = true;			
					this.mpmTextoPadraoParecer = new MpmTextoPadraoParecer();										 
					this.mpmTextoPadraoParecer.setSigla(this.siglaMpmTextoPadraoParecer.toUpperCase());
					this.mpmTextoPadraoParecer.setDescricao(this.descricaoMpmTextoPadraoParecer.toUpperCase());
					return PAGE_CADASTRAR_TEXTO_PADRAO_PARECER;
				}
			}
		}
		return PAGE_PESQUISAR_TEXTO_PADRAO_PARECER;
	}
	
		
	public String voltar(){		
		this.editarAtiva = false;
		this.pesquisarTextoParecerPadraoController.setEdicaoAtiva(false);
		this.limpar();
		return PAGE_PESQUISAR_TEXTO_PADRAO_PARECER;
	}
	
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance()
				.getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.siglaMpmTextoPadraoParecer = null;
		this.descricaoMpmTextoPadraoParecer = null;
		this.mpmTextoPadraoParecer = null;
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
	
	public String getDescricaoMpmTextoPadraoParecer() {
		return descricaoMpmTextoPadraoParecer;
	}

	public void setDescricaoMpmTextoPadraoParecer(
			String descricaoMpmTextoPadraoParecer) {
		this.descricaoMpmTextoPadraoParecer = descricaoMpmTextoPadraoParecer;
	}

	public String getSiglaMpmTextoPadraoParecer() {
		return siglaMpmTextoPadraoParecer;
	}

	public void setSiglaMpmTextoPadraoParecer(String siglaMpmTextoPadraoParecer) {
		this.siglaMpmTextoPadraoParecer = siglaMpmTextoPadraoParecer;
	}

	public boolean isEditarAtiva() {
		return editarAtiva;
	}

	public void setEditarAtiva(boolean editarAtiva) {
		this.editarAtiva = editarAtiva;
	}

	public String getSiglaAntiga() {
		return siglaAntiga;
	}

	public void setSiglaAntiga(String siglaAntiga) {
		this.siglaAntiga = siglaAntiga;
	}

	public MpmTextoPadraoParecer getMpmTextoPadraoParecer() {
		return mpmTextoPadraoParecer;
	}

	public void setMpmTextoPadraoParecer(MpmTextoPadraoParecer mpmTextoPadraoParecer) {
		this.mpmTextoPadraoParecer = mpmTextoPadraoParecer;
	}

	public PesquisarTextoParecerPadraoController getPesquisarTextoParecerPadraoController() {
		return pesquisarTextoParecerPadraoController;
	}

	public void setPesquisarTextoParecerPadraoController(
			PesquisarTextoParecerPadraoController pesquisarTextoParecerPadraoController) {
		this.pesquisarTextoParecerPadraoController = pesquisarTextoParecerPadraoController;
	}	

	public boolean isFalhaEditar() {
		return falhaEditar;
	}

	public void setFalhaEditar(boolean falhaEditar) {
		this.falhaEditar = falhaEditar;
	}
	
}
