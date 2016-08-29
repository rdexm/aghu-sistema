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
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ComorbidadePaginatorController extends	ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8141207457021389090L;

	private static final String PAGE_COMORBIDADE_CRUD = "comorbidadeCRUD";
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MtxComorbidade> dataModel;
	
	private DominioSituacao situacao;
	private List<String> listaDomTipoTransplante = new ArrayList<String>();
	private List<String> listaDomTipoTransplanteSelecionado = new ArrayList<String>();
	private MtxComorbidade mtxComorbidade = new MtxComorbidade();
	private MtxComorbidade mtxComorbidadeDoenca;
	private MtxComorbidade mtxComorbidadeSelect;
	
	private AghCid cid;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		carregarListaTipoTransplante();
	}
	
	public void pesquisar(){
		mtxComorbidade.setSituacao(situacao);
		mtxComorbidade.setCidSeq(mtxComorbidadeDoenca == null ? null : mtxComorbidadeDoenca.getCidSeq() );
		mtxComorbidade.setDescricao(mtxComorbidadeDoenca == null ? null : mtxComorbidadeDoenca.getDescricao());
		mtxComorbidade.setTipo(DominioTipoTransplante.getInstance(listaDomTipoTransplanteSelecionado));
		dataModel.setPesquisaAtiva(true);
		dataModel.reiniciarPaginator();
	}
	
	public List<MtxComorbidade> pesquisarComorbidade(String doenca) {
		mtxComorbidade.setDescricao(doenca);
		return this.returnSGWithCount(transplanteFacade.pesquisarDoenca(mtxComorbidade), transplanteFacade.pesquisarDoencaCount(mtxComorbidade));
	}

	public String editar(){
		return PAGE_COMORBIDADE_CRUD;
	}
	
	public String incluir(){
		return PAGE_COMORBIDADE_CRUD;
	}
	
	public String obterHint(String string, Short tamanho){

        if(string.length() > tamanho){
            return StringUtils.abbreviate(string, tamanho); 
        }
        return string;
	}

	private void carregarListaTipoTransplante(){
		listaDomTipoTransplante.add(DominioTipoTransplante.M.getDescricao());
		listaDomTipoTransplante.add(DominioTipoTransplante.O.getDescricao());
	}
	
	/**
	 * @info Se for chamada pela funcao de edicao, nao executa pesquisa
	 * @param pesquisar
	 */
	public void limparPesquisa() {
		listaDomTipoTransplanteSelecionado = new ArrayList<String>();
		situacao = null;
		mtxComorbidade = new MtxComorbidade();
		mtxComorbidadeDoenca = null;
		cid = null;
		dataModel.setPesquisaAtiva(false);
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
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

	@Override
	public Long recuperarCount() {
		return transplanteFacade.pesquisarComorbidadeCount(mtxComorbidade);
	}

	@Override
	public  List<MtxComorbidade> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return transplanteFacade.pesquisarComorbidade(mtxComorbidade, firstResult, maxResults, orderProperty, asc);
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<MtxComorbidade> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MtxComorbidade> dataModel) {
		this.dataModel = dataModel;
	}

	public MtxComorbidade getMtxComorbidade() {
		return mtxComorbidade;
	}

	public void setMtxComorbidade(MtxComorbidade mtxComorbidade) {
		this.mtxComorbidade = mtxComorbidade;
	}

	public List<String> getListaDomTipoTransplante() {
		return listaDomTipoTransplante;
	}

	public void setListaDomTipoTransplante(List<String> listaDomTipoTransplante) {
		this.listaDomTipoTransplante = listaDomTipoTransplante;
	}

	public List<String> getListaDomTipoTransplanteSelecionado() {
		return listaDomTipoTransplanteSelecionado;
	}

	public void setListaDomTipoTransplanteSelecionado(List<String> listaDomTipoTransplanteSelecionado) {
		this.listaDomTipoTransplanteSelecionado = listaDomTipoTransplanteSelecionado;
	}
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public MtxComorbidade getMtxComorbidadeSelect() {
		return mtxComorbidadeSelect;
	}

	public void setMtxComorbidadeSelect(MtxComorbidade mtxComorbidadeSelect) {
		this.mtxComorbidadeSelect = mtxComorbidadeSelect;
	}

	public MtxComorbidade getMtxComorbidadeDoenca() {
		return mtxComorbidadeDoenca;
	}

	public void setMtxComorbidadeDoenca(MtxComorbidade mtxComorbidadeDoenca) {
		this.mtxComorbidadeDoenca = mtxComorbidadeDoenca;
	}
	
}
