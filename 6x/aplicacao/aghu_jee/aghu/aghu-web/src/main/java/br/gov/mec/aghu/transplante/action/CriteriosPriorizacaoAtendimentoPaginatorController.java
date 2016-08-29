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
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class CriteriosPriorizacaoAtendimentoPaginatorController extends
		ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4157596040360656196L;
	private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";
	private static final String PAGE_CRITERIOS_PRIOR_ATEND_CRUD = "criteriosPriorizacaoAtendimentoCRUD";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@Inject
	private CriteriosPriorizacaoAtendimentoController criteriosPriorizacaoAtendimentoController;
	
	@Inject @Paginator
	private DynamicDataModel<CriteriosPriorizacaoAtendVO> dataModel;
	
	private CriteriosPriorizacaoAtendVO filtro = new CriteriosPriorizacaoAtendVO();
	
	private CriteriosPriorizacaoAtendVO parametroSelecionado;
	
	private AghCid cid;
	private Integer gravidade = null;
	private Integer criticidade = null;
	private String status = null;
	
	private List<Integer> listaGravidade;
	private List<Integer> listaCriticidade;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		
		preencherListasCombo();
	}
	
	public void preencherListasCombo(){
		this.listaGravidade = new ArrayList<Integer>();
		this.listaCriticidade = new ArrayList<Integer>();
		
		listaCriticidade.add(0);
		listaCriticidade.add(5);
		listaCriticidade.add(10);
		listaCriticidade.add(15);
		listaCriticidade.add(20);
		listaCriticidade.add(25);
		listaCriticidade.add(30);
		listaCriticidade.add(35);
		listaCriticidade.add(40);
		listaCriticidade.add(45);
		listaCriticidade.add(50);
		listaCriticidade.add(55);
		listaCriticidade.add(60);
		listaCriticidade.add(65);
		listaCriticidade.add(70);
		listaCriticidade.add(75);
		listaCriticidade.add(80);
		listaCriticidade.add(85);
		listaCriticidade.add(90);
		listaCriticidade.add(95);
		listaCriticidade.add(100);
		
		listaGravidade.addAll(listaCriticidade);
	}
	
	public void pesquisar(){
		if(gravidade != null){
			filtro.setGravidade(gravidade);
		}
		if(criticidade != null){
			filtro.setCriticidade(criticidade);
		}
		if(status != null){
			filtro.setStatus(status);
		}
		
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(true);
	}

	@Override
	public Long recuperarCount() {
		return this.transplanteFacade.pesquisarCriteriosPriorizacaoAtendimentoCount(filtro);
	}

	@Override
	public List<CriteriosPriorizacaoAtendVO> recuperarListaPaginada(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
				return transplanteFacade.pesquisarCriteriosPriorizacaoAtendimento(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	/**Pesquisa Cid por seq, codigo e descricao**/
	public List<AghCid> obterCid(String pesquisa){		
		return returnSGWithCount(this.transplanteFacade.pesquisarCidPorSeqCodDescricao(pesquisa), this.transplanteFacade.pesquisarCidPorSeqCodDescricaoCount(pesquisa));
	}
	/**Redireciona para a página pesquisaCid.xhtml*/
	public String cidPorCapitulo(){
		return PAGE_PESQUISA_CID;
	}
	/**Redireciona para a página criteriosPriorizacaoAtendimentoCRUD.xhtml em modo de inserção*/
	public String novoCriterioPriorizacao(){
		criteriosPriorizacaoAtendimentoController.setFiltro(new CriteriosPriorizacaoAtendVO());
		criteriosPriorizacaoAtendimentoController.setSituacao(Boolean.TRUE);
		return PAGE_CRITERIOS_PRIOR_ATEND_CRUD;
	}
	/**Redireciona para a página criteriosPriorizacaoAtendimentoCRUD.xhtml em modo de edição*/
	public String editar(){
		return PAGE_CRITERIOS_PRIOR_ATEND_CRUD;
	}
	
	/**Ação do botão Limpar**/
	public void limparPesquisa() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {
			
			limparValoresSubmetidos(componentes.next());
		}

		setFiltro(new CriteriosPriorizacaoAtendVO());
		this.dataModel.limparPesquisa();
		this.cid = null;
		this.gravidade = null;
		this.criticidade = null;
		this.status = null;
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
	
	
	/**
	 * Truncar os itens e adiciona o símbolo de reticências (...)
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		String itemCapitalizado = WordUtils.capitalizeFully(item);
		if (itemCapitalizado.length() > tamanhoMaximo) {
			itemCapitalizado = StringUtils.abbreviate(itemCapitalizado, tamanhoMaximo);
		}
			
		return itemCapitalizado;
	}
	
	//Getters e Setters
	public DynamicDataModel<CriteriosPriorizacaoAtendVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CriteriosPriorizacaoAtendVO> dataModel) {
		this.dataModel = dataModel;
	}

	public CriteriosPriorizacaoAtendVO getFiltro() {
		return filtro;
	}

	public void setFiltro(CriteriosPriorizacaoAtendVO filtro) {
		this.filtro = filtro;
	}

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public Integer getGravidade() {
		return gravidade;
	}

	public void setGravidade(Integer gravidade) {
		this.gravidade = gravidade;
	}

	public Integer getCriticidade() {
		return criticidade;
	}

	public void setCriticidade(Integer criticidade) {
		this.criticidade = criticidade;
	}

	public CriteriosPriorizacaoAtendVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(CriteriosPriorizacaoAtendVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public List<Integer> getListaGravidade() {
		return listaGravidade;
	}

	public void setListaGravidade(List<Integer> listaGravidade) {
		this.listaGravidade = listaGravidade;
	}

	public List<Integer> getListaCriticidade() {
		return listaCriticidade;
	}

	public void setListaCriticidade(List<Integer> listaCriticidade) {
		this.listaCriticidade = listaCriticidade;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
