package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class VisualizarObjetosCustoAssociadosCentroCustoPaginatorController extends ActionController implements ActionPaginator{
	
	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final String PESQUISAR_OBJETOS_CUSTO_CENTRO_CUSTO = "pesquisarObjetosCustoCentroCusto";

	@Inject @Paginator
	private DynamicDataModel<SigObjetoCustoVersoes> dataModel;

	private static final Log LOG = LogFactory.getLog(VisualizarObjetosCustoAssociadosCentroCustoPaginatorController.class);

	private static final long serialVersionUID = 897861220047449259L;
		
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private Integer cctCodigo;
	private FccCentroCustos centroCusto;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		if(cctCodigo != null){
			this.setCentroCusto(this.centroCustoFacade.obterCentroCusto(cctCodigo));
			this.dataModel.reiniciarPaginator();
		}
	
	}

	@Override
	public Long recuperarCount() {
		return custosSigFacade.pesquisarObjetoCustoVersoesCount(null,this.getCentroCusto(),null,null,null,null);
	}

	@Override
	public List<SigObjetoCustoVersoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigFacade.pesquisarObjetoCustoVersoes(firstResult, maxResult, orderProperty, asc, 
				                           null, this.getCentroCusto(), null, null, null, null);
	}
	
	public List<SigObjetoCustoComposicoes> pesquisarComposicao(Integer seq) {
		return this.custosSigFacade.pesquisarComposicoesPorObjetoCustoVersao(seq);
	}
	
	public String voltar(){
		return PESQUISAR_OBJETOS_CUSTO_CENTRO_CUSTO;
	}
	
	public String visualizar(){
		return MANTER_OBJETOS_CUSTO;
	}
	
	public String editar() {
		return MANTER_OBJETOS_CUSTO;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public DynamicDataModel<SigObjetoCustoVersoes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigObjetoCustoVersoes> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
