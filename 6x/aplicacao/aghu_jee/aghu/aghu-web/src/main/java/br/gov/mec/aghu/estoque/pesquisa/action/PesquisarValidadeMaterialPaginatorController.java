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

public class PesquisarValidadeMaterialPaginatorController extends ActionController implements ActionPaginator{

	private static final String MANTER_ESTOQUE_ALMOXARIFADO = "estoque-manterEstoqueAlmoxarifado";

	private static final String MANTER_MATERIAL_CRUD = "estoque-manterMaterialCRUD";

	private static final String MANTER_VALIDADE_MATERIAL = "estoque-manterValidadeMaterial";

	private static final long serialVersionUID = -5271884724378314890L;

	@Inject @Paginator
	private DynamicDataModel<SceEstoqueAlmoxarifado> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisarValidadeMaterialPaginatorController.class);
	
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private SceEstoqueAlmoxarifado estoqueAlmox  = new SceEstoqueAlmoxarifado();
	
	private DominioSimNao indControleValidade;
	private DominioSimNao indEstocavel;
	
	private String voltarPara;
	
	// Parâmetros para integração com outras estórias
	private Short seqAlmoxarifado;
	private Integer numeroFornecedor;
	private Integer codigoMaterial;
	private String endereco;
	private Integer qtdeBloqueada;
	private Integer qtdeDisponivel;
	private Boolean controleValidade;
	private Boolean estocavel;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando conversation");
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio(){
	 


		// Realiza a integração com estórias que utilizam o parâmetro "codigoMaterial"
		if(this.codigoMaterial != null || this.seqAlmoxarifado != null){ 
			
			// Popula automaticamente filtros da pesquisa
			if(this.seqAlmoxarifado != null){
				this.estoqueAlmox.setAlmoxarifado(this.estoqueFacade.obterAlmoxarifadoPorId(this.seqAlmoxarifado));
			}
			if(this.numeroFornecedor != null){
				this.estoqueAlmox.setFornecedor(this.comprasFacade.obterFornecedorPorNumero(this.numeroFornecedor));
			}
			if(this.codigoMaterial != null){
				this.estoqueAlmox.setMaterial(this.comprasFacade.obterMaterialPorId(this.codigoMaterial));
			}
			
			this.estoqueAlmox.setQtdeBloqueada(this.qtdeBloqueada);
			this.estoqueAlmox.setQtdeDisponivel(this.qtdeDisponivel);
			
			this.indControleValidade = DominioSimNao.getInstance(this.controleValidade);
			this.estoqueAlmox.setEndereco(this.endereco);
			this.indEstocavel = DominioSimNao.getInstance(this.estocavel);
	
			// Pesquisa automaticamente
			this.pesquisar();

		} 
		// TODO MIGRAÇÃO: Provavelmente não será necessário
//		else if(this.getFirstResult() == 0 && this.ativo){
//			this.pesquisar();
//		}

	
	}
	
	//suggestions
		public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
			return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
		}
		
		public List<ScoMaterial>pesquisarMateriais(String param){
			return comprasFacade.listarScoMateriais(param, null);
		}
		
		public List<ScoFornecedor>pesquisarFornecedores(String param){
			return comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(param);
		}


	public void pesquisar() {

		if(getIndControleValidade() == null){
			estoqueAlmox.setIndControleValidade(null);
		}else{
			estoqueAlmox.setIndControleValidade(getIndControleValidade().isSim());
		}

		if(getIndEstocavel() == null){
			estoqueAlmox.setIndEstocavel(null);
		}else{
			estoqueAlmox.setIndEstocavel(getIndEstocavel().isSim());
		}

		this.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.estoqueAlmox = new SceEstoqueAlmoxarifado();
		this.reiniciarPaginator();
		setIndControleValidade(null);
		setIndEstocavel(null);

		this.setAtivo(false);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return estoqueFacade.pesquisarEstoqueAlmoxarifadoValidadeMaterialCount(estoqueAlmox);
	}
	
	@Override
	public List<SceEstoqueAlmoxarifado> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
	
		List<SceEstoqueAlmoxarifado> result = this.estoqueFacade.pesquisarEstoqueAlmoxarifadoValidadeMaterial(firstResult, maxResult, orderProperty, asc, estoqueAlmox);
		
		if (result == null){
			result = new ArrayList<SceEstoqueAlmoxarifado>();
		}
		
		return result;
	}
	
	public String manterValidadeMaterial(){
		return MANTER_VALIDADE_MATERIAL;
	}
	
	public String voltar() {
		if(voltarPara != null){
			if(voltarPara.equals("manterMaterial")){
				return MANTER_MATERIAL_CRUD; 
			}
			else if(voltarPara.equals("manterEstoqueAlmoxarifado")){
				return MANTER_ESTOQUE_ALMOXARIFADO;
			}
		}
		return voltarPara;
	}

		
	//getters e setters

	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public SceEstoqueAlmoxarifado getEstoqueAlmox() {
		return estoqueAlmox;
	}

	public void setEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox) {
		this.estoqueAlmox = estoqueAlmox;
	}

	public DominioSimNao getIndControleValidade() {
		return indControleValidade;
	}

	public void setIndControleValidade(DominioSimNao indControleValidade) {
		this.indControleValidade = indControleValidade;
	}

	public DominioSimNao getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(DominioSimNao indEstocavel) {
		this.indEstocavel = indEstocavel;
	}
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}

	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Boolean getControleValidade() {
		return controleValidade;
	}

	public void setControleValidade(Boolean controleValidade) {
		this.controleValidade = controleValidade;
	}

	public Boolean getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(Boolean estocavel) {
		this.estocavel = estocavel;
	}
	
	public Integer getQtdeBloqueada() {
		return qtdeBloqueada;
	}
	
	public void setQtdeBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}
	
	public Integer getQtdeDisponivel() {
		return qtdeDisponivel;
	}
	
	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}
	 


	public DynamicDataModel<SceEstoqueAlmoxarifado> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceEstoqueAlmoxarifado> dataModel) {
	 this.dataModel = dataModel;
	}
}
