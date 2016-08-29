package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigObjetoCustoHistoricos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class VisualizarHistoricoAtividadePaginatorController extends ActionController implements ActionPaginator {

	private static final String PESQUISAR_ATIVIDADES = "pesquisarAtividades";

	@Inject @Paginator
	private DynamicDataModel<SigObjetoCustoHistoricos> dataModel;

	private static final long serialVersionUID = 4047010309797954845L;

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	private Integer seqAtividade;
	private SigAtividades atividade;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		
		if (this.seqAtividade != null) {
			//Busca o objeto de custo
			this.atividade = this.custosSigFacade.obterAtividade(this.seqAtividade);
			this.dataModel.reiniciarPaginator();
		}
	
	}
	
	public String voltar(){
		return PESQUISAR_ATIVIDADES;
	}

	public Integer getSeqAtividade() {
		return seqAtividade;
	}

	public void setSeqAtividade(Integer seqAtividade) {
		this.seqAtividade = seqAtividade;
	}

	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}
	
	@Override
	public Long recuperarCount() {
		return custosSigFacade.pesquisarHistoricoAtividadeCount(atividade);
	}

	@Override
	public List<SigObjetoCustoHistoricos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return custosSigFacade.pesquisarHistoricoAtividade(firstResult, maxResult, orderProperty, asc, atividade);
	} 


	public DynamicDataModel<SigObjetoCustoHistoricos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigObjetoCustoHistoricos> dataModel) {
	 this.dataModel = dataModel;
	}
}
