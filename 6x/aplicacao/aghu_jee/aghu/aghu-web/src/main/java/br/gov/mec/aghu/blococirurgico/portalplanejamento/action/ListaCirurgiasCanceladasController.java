package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ListaCirurgiasCanceladasController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -7102047377830610820L;
	
	private static final Log LOG = LogFactory.getLog(ListaCirurgiasCanceladasController.class);

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;

	private PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO;
	private List<CirurgiasCanceladasVO> cirurgiasCanceladasVO;
	private Long tamLista;
	
	private CirurgiasCanceladasVO selecionado;
	
	@Inject @Paginator
	private DynamicDataModel<CirurgiasCanceladasVO> dataModel;
	
	// recebe parameros na estoria Portal de Pesquisa de Cirurgias
	public void recebeParametros(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		
		this.portalPesquisaCirurgiasParametrosVO = portalPesquisaCirurgiasParametrosVO;
		tamLista = null;
		cirurgiasCanceladasVO = null;
		this.dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		if(cirurgiasCanceladasVO == null && tamLista == null){
			recuperarListaPaginada(0, 10, "", true);
		}
		return tamLista;
	}
	
	@Override
	public List<CirurgiasCanceladasVO> recuperarListaPaginada
		(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			cirurgiasCanceladasVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarCirurgiasCanceladas
				(orderProperty, asc, portalPesquisaCirurgiasParametrosVO);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
		}
		
		if ((cirurgiasCanceladasVO != null) && !(cirurgiasCanceladasVO.isEmpty())){
			paginarLista(firstResult,maxResult,orderProperty, asc);
		}
		
		return cirurgiasCanceladasVO;
	}
	
	private void paginarLista(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(tamLista == null || tamLista == 0){
			tamLista = Long.valueOf(cirurgiasCanceladasVO.size()) ;
		}
		Integer lastResult = (firstResult + maxResult) > cirurgiasCanceladasVO.size() ? 
				cirurgiasCanceladasVO.size() : (firstResult + maxResult);

		cirurgiasCanceladasVO = cirurgiasCanceladasVO.subList(firstResult, lastResult);
	}

	// Gets e Sets

	public void setCirurgiasCanceladasVO(List<CirurgiasCanceladasVO> cirurgiasCanceladasVO) {
		this.cirurgiasCanceladasVO = cirurgiasCanceladasVO;
	}

	public List<CirurgiasCanceladasVO> getCirurgiasCanceladasVO() {
		return cirurgiasCanceladasVO;
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

	public Long getTamLista() {
		return tamLista;
	}

	public void setTamLista(Long tamLista) {
		this.tamLista = tamLista;
	}

	public DynamicDataModel<CirurgiasCanceladasVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CirurgiasCanceladasVO> dataModel) {
		this.dataModel = dataModel;
	}

	public CirurgiasCanceladasVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(CirurgiasCanceladasVO selecionado) {
		this.selecionado = selecionado;
	}
}