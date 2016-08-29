package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class PesquisarMaterialBloqueioDesbloqueioPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 2045794245553047684L;	

	private static final String MATERIAL_BLOQUEIO_DESBLOQUEIO = "estoque-materialBloqueioDesbloqueio";
	
	@Inject @Paginator
	private DynamicDataModel<SceEstoqueAlmoxarifado> dataModel;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private SceAlmoxarifado almoxarifado;
	private ScoFornecedor fornecedor;
	private ScoMaterial material;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {

		// Garante que os resultados da pesquisa serão mantidos ao retonar na tela 
		/*if(this.getFirstResult() == 0 && this.ativo){
			this.pesquisar();
		}*/
	}

		
	/**
	 * Pesquisa principal
	 */
	public void pesquisar() {
		// Reinicia paginator e realiza a pesquisa principal
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		Short almoxSeq = null;
		Integer numeroFornecedor = null;
		Integer codMaterial = null;

		if(this.getAlmoxarifado()!=null){
			almoxSeq = this.getAlmoxarifado().getSeq();
		}

		if(this.getFornecedor()!=null){
			numeroFornecedor = getFornecedor().getNumero();
		}

		if(this.getMaterial()!=null){
			codMaterial = this.getMaterial().getCodigo();
		}
		
		return this.estoqueFacade.pesquisarEstoqueMaterialPorAlmoxarifadoCount(almoxSeq, numeroFornecedor, codMaterial);
	}
	
	/**
	 * Pesquisa principal e paginada
	 */	
	@Override
	public List<SceEstoqueAlmoxarifado> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short almoxSeq = null;
		Integer numeroFornecedor = null;
		Integer codMaterial = null;

		if(this.getAlmoxarifado()!=null){
			almoxSeq = this.getAlmoxarifado().getSeq();
		}

		if(this.getFornecedor()!=null){
			numeroFornecedor = getFornecedor().getNumero();
		}

		if(this.getMaterial()!=null){
			codMaterial = this.getMaterial().getCodigo();
		}
		
		List<SceEstoqueAlmoxarifado> listaEAL = 
			this.estoqueFacade.pesquisarEstoqueMaterialPorAlmoxarifadoOrderByFornEAlmx(firstResult, maxResult, almoxSeq, numeroFornecedor, codMaterial);
			
		return listaEAL;
	}
	
	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		this.material = null;
		this.almoxarifado = null;
		this.fornecedor = null;
		
		dataModel.setPesquisaAtiva(false);
	}
	
	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}
	
	// Metodo para pesquisa na suggestion box de material
	public List<ScoMaterial> listaEstoqueMaterialPorAlmoxarifado(String paramPesq) throws ApplicationBusinessException {
		Short almoSeq = (this.getAlmoxarifado()!=null)?this.getAlmoxarifado().getSeq():null;
		return this.comprasFacade.pesquisaMateriaisPorParamAlmox(almoSeq, paramPesq);
	}
	
	// Metodo para pesquisa na suggestion box de fornecedor
	public List<ScoFornecedor> obterFornecedor(String param){
		Short almox = null;
		Integer materialCodigo = null;

		if(this.getAlmoxarifado()!=null){
			almox = this.getAlmoxarifado().getSeq();
		}

		if(this.getMaterial()!=null){
			materialCodigo = this.getMaterial().getCodigo();
		}

		return this.comprasFacade.obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(param, almox, materialCodigo);
	}
	
	/**
	 * Pesquisas para suggestion box
	 */	

	/**
	 * Obtem lista para sugestion box de fornecedores
	 * @param param
	 * @return
	 */
	public List<ScoFornecedor> obterFornecedores(String param){
		return comprasFacade.obterFornecedor(param);
	}
	
	/**
	 * Obtem lista para sugestion box de fornecedores eventuais
	 * @param param
	 * @return
	 */
	public List<SceFornecedorEventual> obterFornecedoresEventuais(Object param){
		return estoqueFacade.obterFornecedorEventual(param);
	}
	

	public String bloquear(){
		return MATERIAL_BLOQUEIO_DESBLOQUEIO;
	}
	
	public String desbloquear(){
		return MATERIAL_BLOQUEIO_DESBLOQUEIO;
	}
	
	/*
	 * Getters e setters
	 */
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}


	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}


	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public DynamicDataModel<SceEstoqueAlmoxarifado> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceEstoqueAlmoxarifado> dataModel) {
	 this.dataModel = dataModel;
	}
}