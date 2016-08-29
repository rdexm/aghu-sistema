package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class VisualizarListaEsperaController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);	 
	}

	@Inject @Paginator
	private DynamicDataModel<ListaEsperaVO> dataModel;

	private static final long serialVersionUID = -7102047377830610820L;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	private PortalPesquisaCirurgiasParametrosVO parametros;
	
	private ListaEsperaVO selecionado;

	public void inicializaParametros(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		parametros = portalPesquisaCirurgiasParametrosVO;
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public List<ListaEsperaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return getBlocoCirurgicoPortalPlanejamentoFacade().listaEsperaRecuperarListaPaginada(firstResult, maxResult, orderProperty, asc, parametros);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	@Override
	public Long recuperarCount() {
		return getBlocoCirurgicoPortalPlanejamentoFacade().listaEsperaRecuperarCount(parametros);
	}

	public PortalPesquisaCirurgiasParametrosVO getParametros() {
		return parametros;
	}

	public void setParametros(PortalPesquisaCirurgiasParametrosVO parametros) {
		this.parametros = parametros;
	}

	public IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return blocoCirurgicoPortalPlanejamentoFacade;
	}

	public void setBlocoCirurgicoPortalPlanejamentoFacade(IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade) {
		this.blocoCirurgicoPortalPlanejamentoFacade = blocoCirurgicoPortalPlanejamentoFacade;
	}

	public DynamicDataModel<ListaEsperaVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ListaEsperaVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public ListaEsperaVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ListaEsperaVO selecionado) {
		this.selecionado = selecionado;
	}
}