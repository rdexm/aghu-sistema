package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTransplante;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProcedimentoTransplanteListPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 8404741754242437252L;

	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MtxProcedimentoTransplantes> dataModel;
	
	@Inject
	private ProcedimentoTransplanteCRUDController procedimentoTransplanteCRUDController;
	
	
	private static final String PAGE_PROCEDIMENTO_TRANSPLANTE_CRUD = "transplante-procedimentoTransplanteCRUD";
	private static final String PAGE_PROCEDIMENTO_TRANSPLANTE_LIST = "transplante-procedimentoTransplanteList";
	
	private DominioTipoOrgao orgaoMtxProcedimento;
	private DominioTransplante tipoTransplanteMtxProcedimento;
	private MtxProcedimentoTransplantes mtxProcedimentoTransplantes;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicosSG;
	private List<MtxProcedimentoTransplantes> listMtxProcedimentoTransplantes;
	private boolean situacaoAtivoMtxProcedimento;
	private MtxProcedimentoTransplantes selecionadoEdicao;
	private MtxProcedimentoTransplantes parametroSelecionado;
	private boolean edicaoOk = false;
	private boolean persistirOk = false;
	
	
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation, true);
		this.orgaoMtxProcedimento = null; 	
		this.situacaoAtivoMtxProcedimento = true;
		this.mbcProcedimentoCirurgicosSG = null;
	}
	
	public void inicializar(){
		if(this.edicaoOk == true){
			apresentarMsgNegocio(Severity.INFO, "MSG_PROCEDIMENTO_TRANSPLANTES_EDICAO_SUCESSO");
			this.edicaoOk = false;
		}
		if(this.persistirOk == true){
			apresentarMsgNegocio(Severity.INFO, "MSG_PROCEDIMENTO_TRANSPLANTES_PERSISTIR_SUCESSO");
			this.persistirOk = false;
		}
	}

	public List<MbcProcedimentoCirurgicos> listarProcedimentoTransplantes(String strPesquisa) {
		 return this.returnSGWithCount(this.transplanteFacade.obterListaProcedimentoTransplantes(strPesquisa), 
				 						this.transplanteFacade.obterListaProcedimentoTransplantesCount(strPesquisa));
	}
	
	
	@Override
	public Long recuperarCount() {
		return this.transplanteFacade.pesquisarListaProcedimentoTransplantesCount(this.mtxProcedimentoTransplantes, (mbcProcedimentoCirurgicosSG != null)? this.mbcProcedimentoCirurgicosSG.getDescricao() : null );
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MtxProcedimentoTransplantes> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<MtxProcedimentoTransplantes> result = this.transplanteFacade.obterProcedimentosAssociados(firstResult, maxResults, orderProperty, asc, this.mtxProcedimentoTransplantes, (mbcProcedimentoCirurgicosSG != null)? this.mbcProcedimentoCirurgicosSG.getDescricao() : null);
		if (result == null) {
			result = new ArrayList<MtxProcedimentoTransplantes>();
		}
		return result;
	}

	

	public void pesquisar(){
		this.mtxProcedimentoTransplantes = new MtxProcedimentoTransplantes();
		if(this.tipoTransplanteMtxProcedimento != null){
			this.mtxProcedimentoTransplantes.setTipo(this.tipoTransplanteMtxProcedimento);
		}
		if(this.orgaoMtxProcedimento != null){
			this.mtxProcedimentoTransplantes.setOrgao(this.orgaoMtxProcedimento);
		}
		if(situacaoAtivoMtxProcedimento == true){
			this.mtxProcedimentoTransplantes.setIndSituacao(DominioSituacao.A);
		}else{
			this.mtxProcedimentoTransplantes.setIndSituacao(DominioSituacao.I);
		}
		this.parametroSelecionado = null;
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true);
	}
	
	public String editar(){
		this.procedimentoTransplanteCRUDController.setOrgaoMtxProcedimento(this.selecionadoEdicao.getOrgao());
		this.procedimentoTransplanteCRUDController.setTipoTransplanteMtxProcedimento(this.selecionadoEdicao.getTipo());
		this.procedimentoTransplanteCRUDController.setIndSituacao(this.selecionadoEdicao.getIndSituacao());
		this.procedimentoTransplanteCRUDController.setMbcProcedimentoCirurgicosSG(this.selecionadoEdicao.getPciSeq());
		this.procedimentoTransplanteCRUDController.setMtxProcedimentoTransplantes(this.selecionadoEdicao);
		this.procedimentoTransplanteCRUDController.setEdicaoAtiva(true);
		return PAGE_PROCEDIMENTO_TRANSPLANTE_CRUD;
	}
	
	public String gravar(){	
		return PAGE_PROCEDIMENTO_TRANSPLANTE_CRUD;
	}
	
	public void excluir(MtxProcedimentoTransplantes mtxProcedimentoTransplantes){
		try{
			this.transplanteFacade.excluirProcedimentoTransplantes(mtxProcedimentoTransplantes);
			apresentarMsgNegocio(Severity.INFO, "MSG_PROCEDIMENTO_TRANSPLANTES_REMOCAO_SUCESSO");
		}catch(ApplicationBusinessException e){
			apresentarMsgNegocio("MSG__EXAMES_TRANSPLANTES_ERRO");
		}
		this.pesquisar();
	}	
	
	public String obterHint(Integer seq, String item, Integer tamanhoMaximo) {
		StringBuilder resultado = new StringBuilder(String.valueOf(seq));
		resultado.append(" - ").append(item);
		if (resultado.length() > tamanhoMaximo) {
			resultado = new StringBuilder(StringUtils.abbreviate(resultado.toString(), tamanhoMaximo));
		}
		return resultado.toString();
	}	
	
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.dataModel.limparPesquisa();
		this.orgaoMtxProcedimento = null;
		this.mbcProcedimentoCirurgicos  = null;
		this.tipoTransplanteMtxProcedimento = null;
		this.mbcProcedimentoCirurgicosSG = null;
		this.situacaoAtivoMtxProcedimento = true;
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
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
	
	public DynamicDataModel<MtxProcedimentoTransplantes> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<MtxProcedimentoTransplantes> dataModel) {
		this.dataModel = dataModel;
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

	public boolean isSituacaoAtivoMtxProcedimento() {
		return situacaoAtivoMtxProcedimento;
	}


	public void setSituacaoAtivoMtxProcedimento(boolean situacaoAtivoMtxProcedimento) {
		this.situacaoAtivoMtxProcedimento = situacaoAtivoMtxProcedimento;
	}


	public ITransplanteFacade getTransplanteFacade() {
		return transplanteFacade;
	}


	public void setTransplanteFacade(ITransplanteFacade transplanteFacade) {
		this.transplanteFacade = transplanteFacade;
	}



	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return mbcProcedimentoCirurgicos;
	}



	public void setMbcProcedimentoCirurgicos(
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}



	public List<MtxProcedimentoTransplantes> getListMtxProcedimentoTransplantes() {
		return listMtxProcedimentoTransplantes;
	}



	public void setListMtxProcedimentoTransplantes(
			List<MtxProcedimentoTransplantes> listMtxProcedimentoTransplantes) {
		this.listMtxProcedimentoTransplantes = listMtxProcedimentoTransplantes;
	}



	public static String getPageProcedimentoTransplanteCrud() {
		return PAGE_PROCEDIMENTO_TRANSPLANTE_CRUD;
	}



	public static String getPageProcedimentoTransplanteList() {
		return PAGE_PROCEDIMENTO_TRANSPLANTE_LIST;
	}



	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicosSG() {
		return mbcProcedimentoCirurgicosSG;
	}




	public void setMbcProcedimentoCirurgicosSG(
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicosSG) {
		this.mbcProcedimentoCirurgicosSG = mbcProcedimentoCirurgicosSG;
	}




	public MtxProcedimentoTransplantes getParametroSelecionado() {
		return parametroSelecionado;
	}




	public void setParametroSelecionado(
			MtxProcedimentoTransplantes parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public ProcedimentoTransplanteCRUDController getProcedimentoTransplanteCRUDController() {
		return procedimentoTransplanteCRUDController;
	}

	public void setProcedimentoTransplanteCRUDController(
			ProcedimentoTransplanteCRUDController procedimentoTransplanteCRUDController) {
		this.procedimentoTransplanteCRUDController = procedimentoTransplanteCRUDController;
	}

	public boolean isEdicaoOk() {
		return edicaoOk;
	}

	public void setEdicaoOk(boolean edicaoOk) {
		this.edicaoOk = edicaoOk;
	}

	public boolean isPersistirOk() {
		return persistirOk;
	}

	public void setPersistirOk(boolean persistirOk) {
		this.persistirOk = persistirOk;
	}

	public MtxProcedimentoTransplantes getSelecionadoEdicao() {
		return selecionadoEdicao;
	}

	public void setSelecionadoEdicao(MtxProcedimentoTransplantes selecionadoEdicao) {
		this.selecionadoEdicao = selecionadoEdicao;
	}
}
