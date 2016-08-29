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

import br.gov.mec.aghu.dominio.DominioTipoMotivoAlteraSituacoes;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MotivoAlteraSituacaoPaginatorController extends ActionController implements ActionPaginator{


	/**
	 * Danilo Santos - 06/05/2015
	 */
	private static final long serialVersionUID = 7621673807684165550L;
	@EJB
	private ITransplanteFacade transplanteFacade;
	@Inject @Paginator
	private DynamicDataModel<MtxMotivoAlteraSituacao> dataModel;
	
	private MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao = new MtxMotivoAlteraSituacao();
	private Boolean ativo;
	private Boolean exibir = Boolean.FALSE;
	private Boolean edicaoAtiva = Boolean.FALSE;
	private static final String PAGE_CADASTRA_ALTERACAO_SITUACAO = "transplante-motivoAlteracaoSituacaoCRUD";
	private List<String> listaTipoTransplante = new ArrayList <String>();
	private List<String> listaTipoTransplanteSelecionado = new ArrayList <String>();
	private MtxMotivoAlteraSituacao parametroSelecionado;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
		carregarListaTipoTransplante();
	}
	
	public void pesquisar(){
		setExibir(true);
		setEdicaoAtiva(false);
		mtxMotivoAlteraSituacao.setTipo(DominioTipoMotivoAlteraSituacoes.getInstance(listaTipoTransplanteSelecionado));
		this.dataModel.reiniciarPaginator();
		
	}
	
	public String inserirEditar() {
		return PAGE_CADASTRA_ALTERACAO_SITUACAO;
	}
	
	public void limparPesquisa(){
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.dataModel.limparPesquisa();
		mtxMotivoAlteraSituacao = new MtxMotivoAlteraSituacao();
		listaTipoTransplanteSelecionado = new ArrayList <String>();
		listaTipoTransplante = new ArrayList <String>();
		carregarListaTipoTransplante();
		pesquisar();	
	}
	
	private void carregarListaTipoTransplante(){
		listaTipoTransplante.add(DominioTipoMotivoAlteraSituacoes.M.getDescricao());
        listaTipoTransplante.add(DominioTipoMotivoAlteraSituacoes.O.getDescricao());
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}
	//Configurações do MODAL
	
	public DynamicDataModel<MtxMotivoAlteraSituacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MtxMotivoAlteraSituacao> dataModel) {
		this.dataModel = dataModel;
	}
	
	@Override
	public Long recuperarCount() {
		return this.transplanteFacade.obterListaMotivoAlteraSituacaoCount(mtxMotivoAlteraSituacao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtxMotivoAlteraSituacao> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return this.transplanteFacade.obterListaMotivoAlteraSituacao(firstResult, maxResults, orderProperty,asc, mtxMotivoAlteraSituacao);
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
	
	//Configurações do MODAL - FIM
	
	public MtxMotivoAlteraSituacao getMtxMotivoAlteraSituacao() {
		return mtxMotivoAlteraSituacao;
	}

	public void setMtxMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		this.mtxMotivoAlteraSituacao = mtxMotivoAlteraSituacao;
	}
	
	
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}


	public List<String> getListaTipoTransplante() {
		return listaTipoTransplante;
	}

	public void setListaTipoTransplante(List<String> listaTipoTransplante) {
		this.listaTipoTransplante = listaTipoTransplante;
	}

	public List<String> getListaTipoTransplanteSelecionado() {
		return listaTipoTransplanteSelecionado;
	}

	public void setListaTipoTransplanteSelecionado(List<String> listaTipoTransplanteSelecionado) {
		this.listaTipoTransplanteSelecionado = listaTipoTransplanteSelecionado;
	}

	public Boolean getExibir() {
		return exibir;
	}

	public void setExibir(Boolean exibir) {
		this.exibir = exibir;
	}

	public Boolean getEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(Boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}
	
	public MtxMotivoAlteraSituacao getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MtxMotivoAlteraSituacao parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}
