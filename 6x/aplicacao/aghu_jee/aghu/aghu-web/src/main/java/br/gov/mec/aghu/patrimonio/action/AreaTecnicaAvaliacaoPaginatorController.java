package br.gov.mec.aghu.patrimonio.action;

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

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class AreaTecnicaAvaliacaoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7635038264714181366L;

	@Inject @Paginator
	private DynamicDataModel<PtmAreaTecAvaliacao> dataModel;
	
	@Inject
	private AssociarUsuariosTecnicosAsOficinasPaginatorController associarUsuariosTecnicosAsOficinasPaginatorController;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private PtmAreaTecAvaliacao areaTecnicaSelecionada;	
	private List<PtmAreaTecAvaliacao> areasTecnicas;
	private final String PAGE_CADASTRAR_AREA_TECNICA = "areaTecnicaAvaliacaoCRUD";	
	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;	

	@PostConstruct
	public void init() {
		begin(conversation, true);
		areaTecnicaAvaliacao=new PtmAreaTecAvaliacao();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();

		while (componentes.hasNext()) {

		limparValoresSubmetidos(componentes.next());

		}

		this.dataModel.limparPesquisa();
		this.areasTecnicas = null;
		areaTecnicaAvaliacao=new PtmAreaTecAvaliacao();
	}

	/**
	 * Limpar campo com caracter especial
	 * @param object
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
	public List<PtmAreaTecAvaliacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(this.areaTecnicaAvaliacao.getServidorCC() != null){
			this.areasTecnicas = patrimonioFacade.pesquisarOficinasAreaTecnicaAvaliacao(firstResult, maxResult, orderProperty, asc, this.areaTecnicaAvaliacao.getNomeAreaTecAvaliacao(), this.areaTecnicaAvaliacao.getFccCentroCustos(), this.areaTecnicaAvaliacao.getServidorCC().getId(), this.areaTecnicaAvaliacao.getSituacao());
		}else{
			this.areasTecnicas = patrimonioFacade.pesquisarOficinasAreaTecnicaAvaliacao(firstResult, maxResult, orderProperty, asc, this.areaTecnicaAvaliacao.getNomeAreaTecAvaliacao(), this.areaTecnicaAvaliacao.getFccCentroCustos(), null, this.areaTecnicaAvaliacao.getSituacao());
		}
		return this.areasTecnicas;
	}
	
	@Override
	public Long recuperarCount() {
		if(this.areaTecnicaAvaliacao.getServidorCC() != null){
			return this.patrimonioFacade.pesquisarOficinasAreaTecnicaAvaliacaoCount(this.areaTecnicaAvaliacao.getNomeAreaTecAvaliacao(), this.areaTecnicaAvaliacao.getFccCentroCustos(), this.areaTecnicaAvaliacao.getServidorCC().getId(), this.areaTecnicaAvaliacao.getSituacao());
		}else{
			return this.patrimonioFacade.pesquisarOficinasAreaTecnicaAvaliacaoCount(this.areaTecnicaAvaliacao.getNomeAreaTecAvaliacao(), this.areaTecnicaAvaliacao.getFccCentroCustos(), null, this.areaTecnicaAvaliacao.getSituacao());
		}
	}
	
	public String inserir(){
		return PAGE_CADASTRAR_AREA_TECNICA;
	}
	
	/**
	 * 
	 * Obtem servidor reponsavel pela area tecnica do suggestion
	 */
	public List<RapServidores> obterResponsavelAreaTec(final String objPesquisa){		
		if (objPesquisa!=null && !StringUtils.EMPTY.equalsIgnoreCase((String) objPesquisa)){			
			return returnSGWithCount(
					this.registroColaboradorFacade.pesquisarServidorPorVinculoOuMatriculaOuNome(objPesquisa),
					this.registroColaboradorFacade.pesquisarServidorPorVinculoOuMatriculaOuNomeCount(objPesquisa));

		}else {			
			return returnSGWithCount(
					this.registroColaboradorFacade.listarTodosServidoresOrdernadosPorNome(),
					this.registroColaboradorFacade.pesquisarRapServidoresCount());
		}
	}

	/**
	 * 
	 * Obtem o centro de custo do suggestion
	 */
	public List<FccCentroCustos> obterCentroCusto(final String objPesquisa){
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return returnSGWithCount(
					this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemOuDescricao(objPesquisa),			
					this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemOuDescricaoCount(objPesquisa));
		}else {
			return returnSGWithCount(
					this.centroCustoFacade.pesquisarCentroCustosAtivos(objPesquisa),
					this.centroCustoFacade.obterCentroCustoAtivosCount());
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
	
	public DynamicDataModel<PtmAreaTecAvaliacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PtmAreaTecAvaliacao> dataModel) {
		this.dataModel = dataModel;
	}

	public PtmAreaTecAvaliacao getAreaTecnicaSelecionada() {
		return areaTecnicaSelecionada;
	}

	public void setAreaTecnicaSelecionada(PtmAreaTecAvaliacao areaTecnicaSelecionada) {
		this.areaTecnicaSelecionada = areaTecnicaSelecionada;
	}
	
	/**
	 * redireciona para pagina de cadastro
	 */
	public String editar() {
		return PAGE_CADASTRAR_AREA_TECNICA ;
	}

	/**
	 * redireciona estoria 43482 - Associar usuários técnicos às oficinas
	 */
	public String associarUsuariosTecnicos(){
		associarUsuariosTecnicosAsOficinasPaginatorController.setAreaTecnicaSelecionada(areaTecnicaSelecionada);
		return associarUsuariosTecnicosAsOficinasPaginatorController.associarUsuariosTecnicos();
	}

	public List<PtmAreaTecAvaliacao> getAreasTecnicas() {
		return areasTecnicas;
	}

	public void setAreasTecnicas(List<PtmAreaTecAvaliacao> areasTecnicas) {
		this.areasTecnicas = areasTecnicas;
	}

	public PtmAreaTecAvaliacao getAreaTecnicaAvaliacao() {
		return areaTecnicaAvaliacao;
	}

	public void setAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao) {
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
	}

	public AssociarUsuariosTecnicosAsOficinasPaginatorController getAssociarUsuariosTecnicosAsOficinasPaginatorController() {
		return associarUsuariosTecnicosAsOficinasPaginatorController;
	}

	public void setAssociarUsuariosTecnicosAsOficinasPaginatorController(
			AssociarUsuariosTecnicosAsOficinasPaginatorController associarUsuariosTecnicosAsOficinasPaginatorController) {
		this.associarUsuariosTecnicosAsOficinasPaginatorController = associarUsuariosTecnicosAsOficinasPaginatorController;
	}

}
