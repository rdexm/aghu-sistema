package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PesquisaRequisicaoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class PesquisaRequisicaoMaterialEfetivarPaginatorController extends ActionController implements ActionPaginator {

	private static final String EFETIVAR_REQUISICAO_MATERIAL = "estoque-efetivarRequisicaoMaterial";

	private static final long serialVersionUID = 6730861641005611991L;
	
	@Inject @Paginator
	private DynamicDataModel<SceReqMaterial> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisaRequisicaoMaterialEfetivarPaginatorController.class);

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private PesquisaRequisicaoMaterialVO filtro = new PesquisaRequisicaoMaterialVO();
	
	private DominioSituacaoRequisicaoMaterial[] situacoes ={DominioSituacaoRequisicaoMaterial.C, 
															DominioSituacaoRequisicaoMaterial.E};
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Inicio conversation");
		this.begin(conversation);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.filtro = new PesquisaRequisicaoMaterialVO();
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String efetivarRequisicaoMaterial(){
		return EFETIVAR_REQUISICAO_MATERIAL;
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisaRequisicoesMateriaisEfetivarCount(filtro);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.estoqueFacade.pesquisaRequisicoesMateriaisEfetivar(firstResult, maxResult, orderProperty, asc, filtro);
	}

	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}
	
	// Metodo para pesquisa na suggestion box de CC Requisicao
	public List<FccCentroCustos> obterFccCentroCustos(String objPesquisa) {
		List<FccCentroCustos> result = new ArrayList<FccCentroCustos>();
		try{
			result.addAll(centroCustoFacade.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(objPesquisa, DominioCaracteristicaCentroCusto.GERAR_RM));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return result;
	}
	
	// Metodo para pesquisa na suggestion box de Grupo de Materiais
	public List<ScoGrupoMaterial> obterScoGrupoMaterial(String objPesquisa) {
		Short almoSeq = (filtro.getAlmoxarifado()!=null)?filtro.getAlmoxarifado().getSeq():null;
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroAlmoxarifado(almoSeq, objPesquisa);
	}
	
	public PesquisaRequisicaoMaterialVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaRequisicaoMaterialVO filtro) {
		this.filtro = filtro;
	}

	public DominioSituacaoRequisicaoMaterial[] getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(DominioSituacaoRequisicaoMaterial[] situacoes) {
		this.situacoes = situacoes;
	} 


	public DynamicDataModel<SceReqMaterial> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceReqMaterial> dataModel) {
	 this.dataModel = dataModel;
	}
}