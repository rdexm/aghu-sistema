package br.gov.mec.aghu.transplante.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTransplante;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ProcedimentoTransplanteCRUDController extends ActionController{

	
	


	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@Inject
	private ProcedimentoTransplanteListPaginatorController procedimentoTransplanteListPaginatorController;
	
	private static final String PAGE_PROCEDIMENTO_TRANSPLANTE_CRUD = "transplante-procedimentoTransplanteCRUD";
	private static final String PAGE_PROCEDIMENTO_TRANSPLANTE_LIST = "transplante-procedimentoTransplanteList";
	
	private DominioTipoOrgao orgaoMtxProcedimento;
	private DominioTransplante tipoTransplanteMtxProcedimento;
	private DominioSituacao indSituacao;
	private DominioSimNao indSimNao;
	private MtxProcedimentoTransplantes mtxProcedimentoTransplantes;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicosSG;
	private List<MtxProcedimentoTransplantes> listMtxProcedimentoTransplantes;
	private boolean edicaoAtiva = false;
	
	@PostConstruct
	public void init(){
		this.begin(conversation, true);	
	}
	
	public void iniciar(){
		if(edicaoAtiva){
			if(this.indSituacao != null){
				if(this.indSituacao == DominioSituacao.A){
					this.indSimNao = DominioSimNao.S;
				}else{
					this.indSimNao = DominioSimNao.N;
				}
			}
		}else{
			this.mtxProcedimentoTransplantes = new MtxProcedimentoTransplantes();
			this.mbcProcedimentoCirurgicosSG = null;
			this.orgaoMtxProcedimento = DominioTipoOrgao.R;
			this.tipoTransplanteMtxProcedimento = null;
			this.indSimNao = DominioSimNao.S;
		}
	}
	
	
	public String gravar(){
		
		try{
			if(this.indSimNao == DominioSimNao.S){
				this.indSituacao = DominioSituacao.A;
			}else{
				this.indSituacao = DominioSituacao.I;
			}
			if(edicaoAtiva){
				this.mtxProcedimentoTransplantes.setIndSituacao(this.indSituacao);
				this.mtxProcedimentoTransplantes.setOrgao(this.orgaoMtxProcedimento);
				this.mtxProcedimentoTransplantes.setTipo(this.tipoTransplanteMtxProcedimento);
				this.mtxProcedimentoTransplantes.setPciSeq(this.mbcProcedimentoCirurgicosSG);
				this.transplanteFacade.editarProcedimentoTransplantes(this.mtxProcedimentoTransplantes);
				this.procedimentoTransplanteListPaginatorController.setEdicaoOk(true);
			}else{
				this.mtxProcedimentoTransplantes.setIndSituacao(this.indSituacao);
				this.mtxProcedimentoTransplantes.setOrgao(this.orgaoMtxProcedimento);
				this.mtxProcedimentoTransplantes.setTipo(this.tipoTransplanteMtxProcedimento);
				this.mtxProcedimentoTransplantes.setPciSeq(this.mbcProcedimentoCirurgicosSG);
				this.transplanteFacade.adicionarProcedimentoTransplantes(this.mtxProcedimentoTransplantes);
				this.procedimentoTransplanteListPaginatorController.setPersistirOk(true);
			}

		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
			return  null;
		}
		this.edicaoAtiva = false;
		return PAGE_PROCEDIMENTO_TRANSPLANTE_LIST;
	}
	
	public String cancelar(){
		this.limpar();
		return PAGE_PROCEDIMENTO_TRANSPLANTE_LIST;
	}
	
	
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.orgaoMtxProcedimento = null;
		this.mbcProcedimentoCirurgicos  = null;
		this.tipoTransplanteMtxProcedimento = null;
		this.indSituacao = null;
		this.mbcProcedimentoCirurgicosSG = null;
	}
	
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	
	public List<MbcProcedimentoCirurgicos> listarProcedimentoTransplantes(String strPesquisa) {
		 return this.returnSGWithCount(this.transplanteFacade.obterListaProcedimentoTransplantes(strPesquisa), 
				 						this.transplanteFacade.obterListaProcedimentoTransplantesCount(strPesquisa));
	}
	
	
	public ITransplanteFacade getTransplanteFacade() {
		return transplanteFacade;
	}
	public void setTransplanteFacade(ITransplanteFacade transplanteFacade) {
		this.transplanteFacade = transplanteFacade;
	}
	public ProcedimentoTransplanteListPaginatorController getProcedimentoTransplanteListPaginatorController() {
		return procedimentoTransplanteListPaginatorController;
	}
	public void setProcedimentoTransplanteListPaginatorController(
			ProcedimentoTransplanteListPaginatorController procedimentoTransplanteListPaginatorController) {
		this.procedimentoTransplanteListPaginatorController = procedimentoTransplanteListPaginatorController;
	}
	public DominioTipoOrgao getOrgaoMtxProcedimento() {
		return orgaoMtxProcedimento;
	}
	public void setOrgaoMtxProcedimento(DominioTipoOrgao orgaoMtxProcedimento) {
		this.orgaoMtxProcedimento = orgaoMtxProcedimento;
	}
	public DominioTransplante getTipoTransplanteMtxProcedimento() {
		return tipoTransplanteMtxProcedimento;
	}
	public void setTipoTransplanteMtxProcedimento(
			DominioTransplante tipoTransplanteMtxProcedimento) {
		this.tipoTransplanteMtxProcedimento = tipoTransplanteMtxProcedimento;
	}
	public MtxProcedimentoTransplantes getMtxProcedimentoTransplantes() {
		return mtxProcedimentoTransplantes;
	}
	public void setMtxProcedimentoTransplantes(
			MtxProcedimentoTransplantes mtxProcedimentoTransplantes) {
		this.mtxProcedimentoTransplantes = mtxProcedimentoTransplantes;
	}
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return mbcProcedimentoCirurgicos;
	}
	public void setMbcProcedimentoCirurgicos(
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicosSG() {
		return mbcProcedimentoCirurgicosSG;
	}
	public void setMbcProcedimentoCirurgicosSG(
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicosSG) {
		this.mbcProcedimentoCirurgicosSG = mbcProcedimentoCirurgicosSG;
	}
	public List<MtxProcedimentoTransplantes> getListMtxProcedimentoTransplantes() {
		return listMtxProcedimentoTransplantes;
	}
	public void setListMtxProcedimentoTransplantes(
			List<MtxProcedimentoTransplantes> listMtxProcedimentoTransplantes) {
		this.listMtxProcedimentoTransplantes = listMtxProcedimentoTransplantes;
	}
	public boolean isEdicaoAtiva() {
		return edicaoAtiva;
	}
	public void setEdicaoAtiva(boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}
	public static String getPageProcedimentoTransplanteCrud() {
		return PAGE_PROCEDIMENTO_TRANSPLANTE_CRUD;
	}
	public static String getPageProcedimentoTransplanteList() {
		return PAGE_PROCEDIMENTO_TRANSPLANTE_LIST;
	}



	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}



	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioSimNao getIndSimNao() {
		return indSimNao;
	}

	public void setIndSimNao(DominioSimNao indSimNao) {
		this.indSimNao = indSimNao;
	}
	

}
