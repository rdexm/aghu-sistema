package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class FormaPagamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1877086062229123300L;

	private static final String FORMA_PAGAMENTO_CRUD = "formaPagamentoCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoFormaPagamento formaPagamento = new ScoFormaPagamento();

	@Inject @Paginator
	private DynamicDataModel<ScoFormaPagamento> dataModel;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarFormasPagamentoCount(formaPagamento);
	}

	@Override
	public List<ScoFormaPagamento> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarFormasPagamento(firstResult, maxResult, orderProperty, asc, formaPagamento);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setFormaPagamento(new ScoFormaPagamento());
	}
	
	public String inserir() {
		return FORMA_PAGAMENTO_CRUD;
	}
	
	public String editar() {
		return FORMA_PAGAMENTO_CRUD;
	}
	
	public String visualizar() {
		return FORMA_PAGAMENTO_CRUD;
	}

	public ScoFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(ScoFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	} 


	public DynamicDataModel<ScoFormaPagamento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoFormaPagamento> dataModel) {
	 this.dataModel = dataModel;
	}
}
