package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoAssociadoAtividadeVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class VisualizarObjetosCustoAssociadosPaginatorController extends ActionController implements ActionPaginator{

	private static final String MANTER_ATIVIDADES = "manterAtividades";

	private static final String PESQUISAR_ATIVIDADES = "pesquisarAtividades";

	@Inject @Paginator
	private DynamicDataModel<ObjetoCustoAssociadoAtividadeVO> dataModel;

	private static final long serialVersionUID = -3838208661963021016L;
	
	@EJB
	private ICustosSigFacade custosSigFacade;

	private Integer seqAtividade;
	private SigAtividades atividade;
	private String redirecionar;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(this.getSeqAtividade() != null){
			this.setAtividade(this.custosSigFacade.obterAtividade(this.getSeqAtividade()));
			this.dataModel.reiniciarPaginator();
		}
	
	}
	
	public String voltarPesquisaAtividade(){
		return PESQUISAR_ATIVIDADES;
	}
		
	public String voltarEdicaoAtividade(){
		return MANTER_ATIVIDADES;
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

	public String getRedirecionar() {
		return redirecionar;
	}

	public void setRedirecionar(String redirecionar) {
		this.redirecionar = redirecionar;
	}

	@Override
	public Long recuperarCount() {
		return Long.valueOf(this.custosSigFacade.pesquisarObjetosCustoAssociadosAtividadesCount(this.getAtividade()));
	}

	@Override
	public List<ObjetoCustoAssociadoAtividadeVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigFacade.pesquisarObjetosCustoAssociadosAtividades(firstResult, maxResult, orderProperty, asc, this.getAtividade());
	}

	public DynamicDataModel<ObjetoCustoAssociadoAtividadeVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ObjetoCustoAssociadoAtividadeVO> dataModel) {
	 this.dataModel = dataModel;
	}
}
