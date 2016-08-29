package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class CadastroCriterioEscolhaFornecedorPaginatorController extends ActionController implements ActionPaginator {

	private static final String CADASTRAR_CRITERIO_ESCOLHA_FORNECEDOR_CRUD = "cadastrarCriterioEscolhaFornecedorCRUD";

	@Inject @Paginator
	private DynamicDataModel<ScoCriterioEscolhaForn> dataModel;

	private static final long serialVersionUID = -480247529602377287L;

	@EJB
	private IComprasFacade comprasFacade;

	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		if(!isAtivo() && (codigo!=null || (descricao!=null && descricao.isEmpty()) || situacao!=null)){
			this.reiniciarPaginator();
		}
	
	}
	
	
	public void pesquisar() {
		this.reiniciarPaginator();
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		setAtivo(false);
	}
	
	public String redirecionarCadastrarCriterioEscolhaFornecedorCRUD(){
		return CADASTRAR_CRITERIO_ESCOLHA_FORNECEDOR_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return comprasFacade.listarCriterioEscolhaFornCount(codigo, descricao, situacao);
	}

	@Override
	public List<ScoCriterioEscolhaForn> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return comprasFacade.listarCriterioEscolhaForn(codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	 
	public DynamicDataModel<ScoCriterioEscolhaForn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoCriterioEscolhaForn> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
