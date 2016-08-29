package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisarEstoqueAlmoxarifadoPaginatorController extends ActionController implements ActionPaginator{

	/**
	 * Felipe Cruz #6617
	 */
	private static final long serialVersionUID = -5271884724378314890L;

	@Inject @Paginator
	private DynamicDataModel<SceEstoqueAlmoxarifado> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisarEstoqueAlmoxarifadoPaginatorController.class);

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	private Boolean visivel = false;
	
	private String novo = "novo";
	
	private DominioSimNao estocavel;

	private DominioSimNao consignado;
	
	private DominioSimNao IndConsignado;

	private DominioSimNao IndControleValidade;

	private DominioSimNao IndEstocavel;
	
	private DominioSimNao validade;
	
	private SceEstoqueAlmoxarifado estoqueAlmox  = new SceEstoqueAlmoxarifado();
	
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando conversation");
		this.begin(conversation);
	}
	
	//suggestions
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	public List<ScoMaterial>pesquisarMateriais(String param){
		return comprasFacade.listarScoMateriaisAtiva(param);
	}
	
	public List<ScoFornecedor>pesquisarFornecedores(String param){
		return comprasFacade.obterFornecedor(param);
	}
	
	
	public void pesquisar() {
		
		if (this.consignado != null){
			this.estoqueAlmox.setIndConsignado(this.consignado.isSim());
		} else {
			this.estoqueAlmox.setIndConsignado(null);
		}

		if (this.validade!= null){
			this.estoqueAlmox.setIndControleValidade(this.validade.isSim());
		} else {
			this.estoqueAlmox.setIndControleValidade(null);
		}
		
		if(this.estocavel!=null){
			this.estoqueAlmox.setIndEstocavel(this.estocavel.isSim());
		} else {
			this.estoqueAlmox.setIndEstocavel(null);
		}
		
		this.reiniciarPaginator();
		this.visivel = true;
	}
	
	public void limparPesquisa() {
		this.estoqueAlmox = new SceEstoqueAlmoxarifado();
		this.consignado = null;
		this.validade= null;
		this.estocavel=null;
		this.reiniciarPaginator();
		this.setAtivo(false);
		this.visivel = false;
	}
	
	public String redirecionarPaginaManterEstoqueAlmoxarifado(){
		return "estoque-manterEstoqueAlmoxarifado";
	}
	
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
//		return estoqueFacade.listaEstoqueAlmoxarifadoCount(this.estoqueAlmox);
		return estoqueFacade.pesquisarSceEstoqueAlmoxarifadoCount(this.estoqueAlmox);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		//List<SceEstoqueAlmoxarifado> result =this.estoqueFacade.listarEstoqueAlmoxarifado(firstResult, maxResult, orderProperty, asc, this.estoqueAlmox);
		List<SceEstoqueAlmoxarifado> result = this.estoqueFacade.pesquisarSceEstoqueAlmoxarifado(firstResult, maxResult, orderProperty, asc, this.estoqueAlmox);
		
		if (result == null){
			result = new ArrayList<SceEstoqueAlmoxarifado>();
		}
		
		return result;
	}

	
	//getters e setters
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}
	
	public SceEstoqueAlmoxarifado getEstoqueAlmox() {
		return estoqueAlmox;
	}

	public void setEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox) {
		this.estoqueAlmox = estoqueAlmox;
	}

	public DominioSimNao getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(DominioSimNao estocavel) {
		this.estocavel = estocavel;
	}

	public DominioSimNao getConsignado() {
		return consignado;
	}

	public void setConsignado(DominioSimNao consignado) {
		this.consignado = consignado;
	}

	public DominioSimNao getValidade() {
		return validade;
	}

	public void setValidade(DominioSimNao validade) {
		this.validade = validade;
	}

	public String getNovo() {
		return novo;
	}

	public void setNovo(String novo) {
		this.novo = novo;
	}

	public DynamicDataModel<SceEstoqueAlmoxarifado> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceEstoqueAlmoxarifado> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public DominioSimNao getIndConsignado() {
		return IndConsignado;
	}

	public void setIndConsignado(DominioSimNao indConsignado) {
		IndConsignado = indConsignado;
	}

	public DominioSimNao getIndControleValidade() {
		return IndControleValidade;
	}

	public void setIndControleValidade(DominioSimNao indControleValidade) {
		IndControleValidade = indControleValidade;
	}

	public DominioSimNao getIndEstocavel() {
		return IndEstocavel;
	}

	public void setIndEstocavel(DominioSimNao indEstocavel) {
		IndEstocavel = indEstocavel;
	}

	
	
	
}
