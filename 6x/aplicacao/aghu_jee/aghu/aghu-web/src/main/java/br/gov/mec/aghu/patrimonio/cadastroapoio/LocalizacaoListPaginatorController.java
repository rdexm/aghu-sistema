package br.gov.mec.aghu.patrimonio.cadastroapoio;

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

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.LocalizacaoFiltroVO;
import br.gov.mec.aghu.patrimonio.vo.PtmEdificacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class LocalizacaoListPaginatorController extends ActionController implements ActionPaginator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5875532213557016704L;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject	@Paginator
	private DynamicDataModel<LocalizacaoFiltroVO> dataModel;
	
	@Inject	
	private  LocalizacaoCrudController localizacaoCrudController;
	
	private LocalizacaoFiltroVO localizacaoFiltroVO  = new LocalizacaoFiltroVO();
	
	private LocalizacaoFiltroVO localizacaoFiltroVOII  = new LocalizacaoFiltroVO();
	
	private List<LocalizacaoFiltroVO> listaLocalizacaoFiltroVO  = new ArrayList<LocalizacaoFiltroVO>();
	
	private Boolean pesquisaAtiva;
	
	private LocalizacaoFiltroVO localizacaoFiltroVOSelecionado  = new LocalizacaoFiltroVO();
	
	private final String PAGE_CADASTRAR_LOCALIZACAO = "patrimonio-localizacaoCRUD";
	
	private final String PAGE_EDITAR_LOCALIZACAO = "patrimonio-localizacaoCRUD";
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
	}
	
	public void iniciar(){
		pesquisaAtiva = Boolean.FALSE;
	}
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	public String novo(){
		localizacaoCrudController.setUpdate(false);
		return PAGE_CADASTRAR_LOCALIZACAO;
	}
	
	public String editar(){
		localizacaoCrudController.setUpdate(true);
		localizacaoCrudController.setSeqPtmEdificacao(localizacaoFiltroVOII.getEdificacaoSeq());
		return PAGE_EDITAR_LOCALIZACAO;
	}
	
	/**
     * Ação do botão Limpar
     */
    public void limpar() { 
    	localizacaoFiltroVO = new LocalizacaoFiltroVO();
    	pesquisaAtiva = false;
        Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
        while (componentes.hasNext()) {                 
                limparValoresSubmetidos(componentes.next());
        }
        this.dataModel.limparPesquisa();
        listaLocalizacaoFiltroVO= new ArrayList<LocalizacaoFiltroVO>();
    }

    public void limparValoresSubmetidos(Object object){
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
	 * 
	 * Obtem o centro de custo do suggestion
	 */
	public List<FccCentroCustos> obterCentroCusto(final String objPesquisa){
		if (objPesquisa!=null && !StringUtils.EMPTY.equalsIgnoreCase((String) objPesquisa)){
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
		 * 
		 * Obtem edificação do suggestion
		 */
		public List<PtmEdificacaoVO> obteSbEdificacao(final String objPesquisa){
				return returnSGWithCount(
						this.patrimonioFacade.pesquisarEdificacoesAtivasNrBemouSeqouNome(objPesquisa),			
						this.patrimonioFacade.pesquisarEdificacoesAtivasNrBemouSeqouNomeCount(objPesquisa));
		}


	@Override
	public Long recuperarCount() {
		return patrimonioFacade.pesquisarListaPaginadaLocalizacaoCount(localizacaoFiltroVO);
	}

	@Override
	public List<LocalizacaoFiltroVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		listaLocalizacaoFiltroVO = patrimonioFacade.pesquisarListaPaginadaLocalizacao(firstResult, maxResult, orderProperty, asc, localizacaoFiltroVO);
		return listaLocalizacaoFiltroVO;
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}

	public LocalizacaoFiltroVO getLocalizacaoFiltroVO() {
		return localizacaoFiltroVO;
	}

	public void setLocalizacaoFiltroVO(LocalizacaoFiltroVO localizacaoFiltroVO) {
		this.localizacaoFiltroVO = localizacaoFiltroVO;
	}

	public DynamicDataModel<LocalizacaoFiltroVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<LocalizacaoFiltroVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public LocalizacaoFiltroVO getLocalizacaoFiltroVOSelecionado() {
		return localizacaoFiltroVOSelecionado;
	}

	public void setLocalizacaoFiltroVOSelecionado(
			LocalizacaoFiltroVO localizacaoFiltroVOSelecionado) {
		this.localizacaoFiltroVOSelecionado = localizacaoFiltroVOSelecionado;
	}

	public LocalizacaoCrudController getLocalizacaoCrudController() {
		return localizacaoCrudController;
	}

	public void setLocalizacaoCrudController(LocalizacaoCrudController localizacaoCrudController) {
		this.localizacaoCrudController = localizacaoCrudController;
	}

	public LocalizacaoFiltroVO getLocalizacaoFiltroVOII() {
		return localizacaoFiltroVOII;
	}

	public void setLocalizacaoFiltroVOII(LocalizacaoFiltroVO localizacaoFiltroVOII) {
		this.localizacaoFiltroVOII = localizacaoFiltroVOII;
	}
}
