package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.action.MaterialBloqueioDesbloqueioProblemaController;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class PesquisarMaterialBloqueioDesbloqueioProblemaPaginatorController extends ActionController implements ActionPaginator {

	
	private static final long serialVersionUID = 2045794245553047684L;
	
	@Inject @Paginator
	private DynamicDataModel<SceHistoricoProblemaMaterial> dataModel;	
	
	private static final String REDIRECT_EDITAR = "estoque-materialBloqueioDesbloqueioProblema";
	
	@Inject
	private MaterialBloqueioDesbloqueioProblemaController materialBloqueioDesbloqueioProblemaController;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private SceAlmoxarifado almoxarifado;
	private ScoFornecedor fornecedor;
	private ScoFornecedor fornecedorEntrega;
	private ScoMaterial material;
	private SceMotivoProblema motivoProblema;
	private Integer seqHistorico;
	
	private SceHistoricoProblemaMaterial historicoSelecionado;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	/* Integração Efetivar transferência e Requisição*/
	private Short almSeq;
	private Integer codMaterial;
	private Integer frnNumero;
	private String voltarPara;
	private boolean exibirVoltar;
	
	
	private Short almoxSeq = null;
	private Integer numeroFornecedor = null;
	private Integer numeroFornecedorEntrega = null;
	private Integer codigoMaterial = null;
	private Short codMotivo = null;
	

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
		}else*/ 
		if(this.getAlmoxarifado()!=null){
			almoxSeq = this.getAlmoxarifado().getSeq();
		}else if(almSeq!=null){
			almoxSeq = almSeq;
			this.almoxarifado = this.estoqueFacade.obterAlmoxarifadoPorId(almoxSeq);
		}

		if(this.getFornecedor()!=null){
			numeroFornecedor = getFornecedor().getNumero();
		}else if(frnNumero !=null){
			numeroFornecedor = frnNumero;
			this.fornecedor = this.comprasFacade.obterFornecedorPorNumero(numeroFornecedor);
		}

		if(this.getFornecedorEntrega()!=null){
			numeroFornecedorEntrega = this.getFornecedorEntrega().getNumero();
		}

		if(this.getMaterial()!=null){
			codigoMaterial = this.getMaterial().getCodigo();
		}else if(codMaterial!=null){
			codigoMaterial = codMaterial;
			this.material = this.comprasFacade.obterMaterialPorId(codigoMaterial);
		}

		if(this.getMotivoProblema()!=null){
			codMotivo = this.getMotivoProblema().getSeq();
		}
		
		
		if(almSeq!=null || codMaterial!=null && frnNumero!=null){	
			this.pesquisar();
		}
	
	}


	/**
	 * Pesquisa principal
	 */
	public void pesquisar() {
		// Reinicia paginator e realiza a pesquisa principal
		if(this.material!=null){
			this.codigoMaterial = material.getCodigo();
		} else {
			this.codigoMaterial = null;
		}
		
		if(this.getAlmoxarifado()!=null){
			this.almoxSeq = almoxarifado.getSeq();
		} else {
			this.almoxSeq = null;
		}
		if(this.fornecedor!=null){
			this.numeroFornecedor = fornecedor.getNumero();
		} else {
			this.numeroFornecedor = null;
		}
		if(this.motivoProblema!=null){
			this.codMotivo = motivoProblema.getSeq();
		} else {
			this.codMotivo = null;
		}
		if(this.fornecedorEntrega!=null){
			this.numeroFornecedorEntrega = fornecedorEntrega.getNumero();
		} else {
			this.numeroFornecedorEntrega = null;
		}
			
			
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
		if(this.voltarPara!=null){
			this.exibirVoltar = true;
		}
	}
	
	public String iniciarInclusao(){
		materialBloqueioDesbloqueioProblemaController.iniciar();
		return REDIRECT_EDITAR;
	}
	
	public String prepararEdicao(){
		try{
		materialBloqueioDesbloqueioProblemaController.iniciarEdicao();
		} catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		
		return REDIRECT_EDITAR;
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisarHistoricosProblemaPorFiltroCount(codigoMaterial, 
				almoxSeq, 
				numeroFornecedor, 
				codMotivo, 
				numeroFornecedorEntrega);
	}

	/**
	 * Pesquisa principal e paginada
	 */	
	@Override
	public List<SceHistoricoProblemaMaterial> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return this.estoqueFacade.pesquisarHistoricosProblemaPorFiltro(firstResult, maxResult, codigoMaterial, 
					almoxSeq, 
					numeroFornecedor, 
					codMotivo, 
					numeroFornecedorEntrega);
	}

	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		this.material = null;
		this.almoxarifado = null;
		this.fornecedor = null;
		this.fornecedorEntrega = null;
		this.material = null;
		this.motivoProblema = null;
		this.exibirBotaoNovo = false;

		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
		
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

	// Metodo para pesquisa na suggestion box de motivo de problema
	public List<SceMotivoProblema> pesquisaMotivosProblemasPorSeqDescricao(String paramPesq) throws ApplicationBusinessException {
		return this.estoqueFacade.pesquisaMotivosProblemasPorSeqDescricao(paramPesq, null);
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

	// Metodo para pesquisa na suggestion box de fornecedor
	public List<ScoFornecedor> obterFornecedorEntrega(String param){
		return this.comprasFacade.obterFornecedor(param);
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


	public String cancelar(){
		
		this.limparPesquisa();

		this.limparPesquisa();
		
		return voltarPara;
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


	public SceMotivoProblema getMotivoProblema() {
		return motivoProblema;
	}


	public void setMotivoProblema(SceMotivoProblema motivoProblema) {
		this.motivoProblema = motivoProblema;
	}


	public ScoFornecedor getFornecedorEntrega() {
		return fornecedorEntrega;
	}


	public void setFornecedorEntrega(ScoFornecedor fornecedorEntrega) {
		this.fornecedorEntrega = fornecedorEntrega;
	}


	public Integer getSeqHistorico() {
		return seqHistorico;
	}


	public void setSeqHistorico(Integer seqHistorico) {
		this.seqHistorico = seqHistorico;
	}


	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}


	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}


	public Short getAlmSeq() {
		return almSeq;
	}


	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}


	public Integer getCodMaterial() {
		return codMaterial;
	}


	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}


	public Integer getFrnNumero() {
		return frnNumero;
	}


	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}


	public String getVoltarPara() {
		return voltarPara;
	}


	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}


	public boolean isExibirVoltar() {
		return exibirVoltar;
	}


	public void setExibirVoltar(boolean exibirVoltar) {
		this.exibirVoltar = exibirVoltar;
	}
	
	public DynamicDataModel<SceHistoricoProblemaMaterial> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceHistoricoProblemaMaterial> dataModel) {
	 this.dataModel = dataModel;
	}


	public SceHistoricoProblemaMaterial getHistoricoSelecionado() {
		return historicoSelecionado;
	}


	public void setHistoricoSelecionado(SceHistoricoProblemaMaterial historicoSelecionado) {
		this.historicoSelecionado = historicoSelecionado;
	}
}