package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class EstornarRequisicaoMaterialController extends ActionController implements ActionPaginator {

	private static final String VISUALIZAR_ITEM_REQUISICAO_MATERIAL = "estoque-visualizarItemRequisicaoMaterial";

	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";

	private static final long serialVersionUID = 6751815764775729061L;

	@Inject @Paginator
	private DynamicDataModel<SceReqMaterial> dataModel;

	private static final Log LOG = LogFactory.getLog(EstornarRequisicaoMaterialController.class);


	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	private SceReqMaterial sceReqMateriais = new SceReqMaterial();
	private SceReqMaterial sceReqMateriaisEstornar = new SceReqMaterial();
	
	private Integer reqMaterialSeq;
	private Integer seqEstoqueAlmox;
	
	// Parâmetros da integração com Manter Materiais
	private Integer codigoMaterial;
	private Boolean indEstocavel;
	private Boolean situacaoMaterial;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	/**
	 * Realiza o estorno da requisição de material
	 */
	public void estornar() {
		
		sceReqMateriaisEstornar = this.estoqueFacade.obterRequisicaoMaterial(getReqMaterialSeq());
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {
			estoqueBeanFacade.estornarRequisicaoMaterial(sceReqMateriaisEstornar, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ESTORNO_REQUISICAO_MATERIAL");
			pesquisar();
		
		} catch (BaseException e) {
		
			apresentarExcecaoNegocio(e);
		
		}
		
		this.reqMaterialSeq = null;
	
	}
	
	public void pesquisar() { 
		
		this.dataModel.reiniciarPaginator();
		
	}

	public void limparPesquisa() {
		
		this.sceReqMateriais = new SceReqMaterial();
		this.sceReqMateriaisEstornar = new SceReqMaterial();
		this.reqMaterialSeq = null;
		this.dataModel.setPesquisaAtiva(false);
		
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisaRequisicoesMateriaisEstornarCount(sceReqMateriais);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return  this.estoqueFacade.pesquisaRequisicoesMateriaisEstornar(firstResult, maxResult, orderProperty, asc, sceReqMateriais);
	}
	
	/**
	 * 
	 * @param reqMaterial
	 * @return
	 */
	public String consultarEstoque(){
		return PESQUISAR_ESTOQUE_ALMOXARIFADO;
	}
	
	public String consultarItens(){
		return VISUALIZAR_ITEM_REQUISICAO_MATERIAL;
	}
	

	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> pesquisarAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}
	
	// Metodo para pesquisa na suggestion box de CC Requisicao
	public List<FccCentroCustos> pesquisarCCRequisicao(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}
	
	// Metodo para pesquisa na suggestion box de CC Requisicao
	public List<FccCentroCustos> pesquisarCCAplicacao(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}
	
	public SceReqMaterial getSceReqMateriais() {
		return sceReqMateriais;
	}

	public void setSceReqMateriais(SceReqMaterial sceReqMateriais) {
		this.sceReqMateriais = sceReqMateriais;
	}

	public Integer getReqMaterialSeq() {
		return reqMaterialSeq;
	}

	public void setReqMaterialSeq(Integer reqMaterialSeq) {
		this.reqMaterialSeq = reqMaterialSeq;
	}

	public SceReqMaterial getSceReqMateriaisEstornar() {
		return sceReqMateriaisEstornar;
	}

	public void setSceReqMateriaisEstornar(SceReqMaterial sceReqMateriaisEstornar) {
		this.sceReqMateriaisEstornar = sceReqMateriaisEstornar;
	}

	public Integer getSeqEstoqueAlmox() {
		return seqEstoqueAlmox;
	}

	public void setSeqEstoqueAlmox(Integer seqEstoqueAlmox) {
		this.seqEstoqueAlmox = seqEstoqueAlmox;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Boolean getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}

	public Boolean getSituacaoMaterial() {
		return situacaoMaterial;
	}

	public void setSituacaoMaterial(Boolean situacaoMaterial) {
		this.situacaoMaterial = situacaoMaterial;
	}
	
	
	 


	public DynamicDataModel<SceReqMaterial> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceReqMaterial> dataModel) {
	 this.dataModel = dataModel;
	}
}