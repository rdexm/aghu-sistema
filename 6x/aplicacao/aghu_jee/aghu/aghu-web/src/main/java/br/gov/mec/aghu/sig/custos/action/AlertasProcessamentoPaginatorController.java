package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class AlertasProcessamentoPaginatorController extends ActionController implements ActionPaginator {

	
	private static final String DETALHAR_ALERTAS_PROCESSAMENTO = "detalharAlertasProcessamento";

	@Inject @Paginator
	private DynamicDataModel<VisualizarAlertasProcessamentoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(AlertasProcessamentoPaginatorController.class);

	private static final long serialVersionUID = -8346981833568307417L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	private SigProcessamentoCusto competencia;
	private List<SigProcessamentoCusto> listaCompetencias;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}

	public void iniciar() {
	 

		if (this.dataModel.getPesquisaAtiva()) {
			this.pesquisar();
		}
	
	}

	public void pesquisar() {
		this.setAtivo(true);
		this.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		try {
			return Long.valueOf(this.custosSigProcessamentoFacade.buscarTotaisParaCadaTipoAlertaCount(this.getCompetencia()));
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return 0L;
	}

	@Override
	public List<VisualizarAlertasProcessamentoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return this.custosSigProcessamentoFacade.buscarTotaisParaCadaTipoAlerta(this.getCompetencia());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public void limpar() {
		this.setCompetencia(null);
		this.setAtivo(false);
		this.iniciar();
	}

	public String detalhar() {
		return DETALHAR_ALERTAS_PROCESSAMENTO;
	}

	public List<SigProcessamentoCusto> listarCompetencias(){
		return  this.custosSigProcessamentoFacade.pesquisarCompetencia(DominioSituacaoProcessamentoCusto.F);
	}
	
	// getters and setters
	
	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public List<SigProcessamentoCusto> getListaCompetencias() {
		return listaCompetencias;
	}

	public void setListaCompetencias(List<SigProcessamentoCusto> listaCompetencias) {
		this.listaCompetencias = listaCompetencias;
	}

	public DynamicDataModel<VisualizarAlertasProcessamentoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<VisualizarAlertasProcessamentoVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
