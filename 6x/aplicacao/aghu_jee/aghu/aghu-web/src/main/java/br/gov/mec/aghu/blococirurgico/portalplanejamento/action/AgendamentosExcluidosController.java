package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.AgendamentosExcluidosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class AgendamentosExcluidosController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AgendamentosExcluidosVO> dataModel;

	private static final long serialVersionUID = 8506093549019024398L;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;

	private PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO;
	private List<AgendamentosExcluidosVO> agendamentosExcluidosVO;

	private AgendamentosExcluidosVO selecionado;
	
	// recebe parameros na estoria Portal de Pesquisa de Cirurgias
	public void recebeParametros(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		this.portalPesquisaCirurgiasParametrosVO = portalPesquisaCirurgiasParametrosVO;
		agendamentosExcluidosVO = null;
		this.dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return getBlocoCirurgicoPortalPlanejamentoFacade().pesquisarAgendamentosExcluidosCount(portalPesquisaCirurgiasParametrosVO);
	}
	
	@Override
	public List<AgendamentosExcluidosVO> recuperarListaPaginada
		(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		// Ordem descendente da data de exclusão
		if (orderProperty == null) {
			asc = false;
		}

		agendamentosExcluidosVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendamentosExcluidos
			(firstResult,maxResult,orderProperty, asc, portalPesquisaCirurgiasParametrosVO);
		return agendamentosExcluidosVO;
	}
	
	public IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return blocoCirurgicoPortalPlanejamentoFacade;
	}

	public PortalPesquisaCirurgiasParametrosVO getPortalPesquisaCirurgiasParametrosVO() {
		return portalPesquisaCirurgiasParametrosVO;
	}

	public void setPortalPesquisaCirurgiasParametrosVO(
			PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		this.portalPesquisaCirurgiasParametrosVO = portalPesquisaCirurgiasParametrosVO;
	}

	public List<AgendamentosExcluidosVO> getAgendamentosExcluidosVO() {
		return agendamentosExcluidosVO;
	}

	public void setAgendamentosExcluidosVO(
			List<AgendamentosExcluidosVO> agendamentosExcluidosVO) {
		this.agendamentosExcluidosVO = agendamentosExcluidosVO;
	}

	public DynamicDataModel<AgendamentosExcluidosVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AgendamentosExcluidosVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public AgendamentosExcluidosVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AgendamentosExcluidosVO selecionado) {
		this.selecionado = selecionado;
	}
}