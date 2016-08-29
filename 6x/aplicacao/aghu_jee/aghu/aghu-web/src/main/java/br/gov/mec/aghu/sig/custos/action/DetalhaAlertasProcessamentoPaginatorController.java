package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSigTipoAlertaDetalhado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;


public class DetalhaAlertasProcessamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final String PESQUISAR_ALERTAS_PROCESSAMENTO = "pesquisarAlertasProcessamento";

	@Inject @Paginator
	private DynamicDataModel<VisualizarAlertasProcessamentoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(DetalhaAlertasProcessamentoPaginatorController.class);

	private static final long serialVersionUID = -8346981833568307417L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private SigProcessamentoCusto competencia;
	private FccCentroCustos fccCentroCustos;

	private Integer seqProcessamento;
	private String tipoAlerta;
	private DominioSigTipoAlertaDetalhado alertaDetalhado;

	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (!this.dataModel.getPesquisaAtiva()) {
			this.competencia = this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.seqProcessamento);
			this.alertaDetalhado = DominioSigTipoAlertaDetalhado.valueOf(this.tipoAlerta);
			this.pesquisar();
		}
	
	}

	public void pesquisar() {
		this.setAtivo(true);
		this.reiniciarPaginator();
	}

	public void limpar() {
		//this.setCompetencia(null);
		this.setFccCentroCustos(null);
		this.alertaDetalhado = null;
		this.setAtivo(false);
	}

	public String voltar() {
		this.setAtivo(false);
		this.limpar();
		return 	PESQUISAR_ALERTAS_PROCESSAMENTO;
	}

	@Override
	public Long recuperarCount() {
		return Long.valueOf(this.custosSigProcessamentoFacade.buscarAlertasPorProcessamentoCentroCustoSemAnaliseCount(this.getCompetencia(), this.getFccCentroCustos(),
				this.alertaDetalhado));
	}

	@Override
	public List<VisualizarAlertasProcessamentoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigProcessamentoFacade.buscarAlertasPorProcessamentoCentroCustoSemAnalise(this.getCompetencia(), this.getFccCentroCustos(),
				this.alertaDetalhado, firstResult, maxResult);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return  this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, DominioSituacao.A);
	}
	
	public List<SigProcessamentoCusto> listarCompetencias(){
		return this.custosSigProcessamentoFacade.pesquisarCompetencia(DominioSituacaoProcessamentoCusto.F);
	}

	public void limparCentroCusto() {
		this.setFccCentroCustos(null);
	}

	// getters and setters
	public Integer getSeqProcessamento() {
		return seqProcessamento;
	}

	public void setSeqProcessamento(Integer seqProcessamento) {
		this.seqProcessamento = seqProcessamento;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}


	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public String getTipoAlerta() {
		return tipoAlerta;
	}

	public void setTipoAlerta(String tipoAlerta) {
		this.tipoAlerta = tipoAlerta;
	}

	public DominioSigTipoAlertaDetalhado getAlertaDetalhado() {
		return alertaDetalhado;
	}

	public void setAlertaDetalhado(DominioSigTipoAlertaDetalhado alertaDetalhado) {
		this.alertaDetalhado = alertaDetalhado;
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
