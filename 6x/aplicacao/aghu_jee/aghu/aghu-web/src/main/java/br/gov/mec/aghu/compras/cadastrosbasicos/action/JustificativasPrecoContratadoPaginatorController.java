package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoJustificativaPreco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class JustificativasPrecoContratadoPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ScoJustificativaPreco> dataModel;

	private static final long serialVersionUID = 1237194890613149890L;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoJustificativaPreco justificativa = new ScoJustificativaPreco();
	private boolean exibirTabela = true;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void limparPesquisa() {
		this.exibirTabela = false;
		this.dataModel.setPesquisaAtiva(false);
		justificativa = new ScoJustificativaPreco();
	}

	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.listarJustificativasPrecoContratadoCount(this.justificativa);
	}

	@Override
	public List<ScoJustificativaPreco> recuperarListaPaginada(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc) {
		List<ScoJustificativaPreco> result = comprasCadastrosBasicosFacade.pesquisarJustificativasPrecoContratado(_firstResult, _maxResult,
				_orderProperty, _asc, this.justificativa);

		if (result == null) {
			result = new ArrayList<ScoJustificativaPreco>();
		}
		return result;
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirTabela = true;
	}

	public String alterar(ScoJustificativaPreco justificativaManutencao) {
		return "justificativasPrecoContratadoCadastro";
	}

	public String novo() {
		return "justificativasPrecoContratadoCadastro";
	}

	public DynamicDataModel<ScoJustificativaPreco> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoJustificativaPreco> dataModel) {
		this.dataModel = dataModel;
	}

	public void setComprasCadastrosBasicosFacade(IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setJustificativa(ScoJustificativaPreco justificativa) {
		this.justificativa = justificativa;
	}

	public ScoJustificativaPreco getJustificativa() {
		return justificativa;
	}

	public void setExibirTabela(boolean exibirTabela) {
		this.exibirTabela = exibirTabela;
	}

	public boolean isExibirTabela() {
		return exibirTabela;
	}
}
