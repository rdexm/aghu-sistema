package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.SigObjetoCustoHistoricos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class VisualizarHistoricoObjetoCustoPaginatorController extends ActionController implements ActionPaginator{

	private static final String PESQUISAR_OBJETOS_CUSTO = "pesquisarObjetosCusto";

	@Inject @Paginator
	private DynamicDataModel<SigObjetoCustoHistoricos> dataModel;

	private static final long serialVersionUID = 4047015379797954845L;

	@EJB
	private ICustosSigFacade custosSigFacade;
	private SigObjetoCustoVersoes objetoCustoVersao;
	private Integer seqObjetoCustoVersao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		
		if (this.seqObjetoCustoVersao != null) {
			//Busca o objeto de custo
			this.objetoCustoVersao = this.custosSigFacade.obterObjetoCustoVersoes(seqObjetoCustoVersao);
			this.dataModel.reiniciarPaginator();
		}
	
	}
	
	public String voltar(){
		return PESQUISAR_OBJETOS_CUSTO;
	}

	public SigObjetoCustoVersoes getObjetoCustoVersao() {
		return objetoCustoVersao;
	}

	public void setObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao) {
		this.objetoCustoVersao = objetoCustoVersao;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}
	
	@Override
	public Long recuperarCount() {
		return custosSigFacade.pesquisarHistoricoObjetoCustoCount(objetoCustoVersao);
	}

	@Override
	public List<SigObjetoCustoHistoricos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return custosSigFacade.pesquisarHistoricoObjetoCusto(firstResult, maxResult, orderProperty, asc, objetoCustoVersao);
	}

	public DynamicDataModel<SigObjetoCustoHistoricos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigObjetoCustoHistoricos> dataModel) {
	 this.dataModel = dataModel;
	}
}
