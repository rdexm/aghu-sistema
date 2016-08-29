/**
 * 
 */
package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * @author bruno.mourao
 *
 */

public class ConsultaCatalogoMaterialPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4357350475240994890L;
	
	private static final Log LOG = LogFactory.getLog(ConsultaCatalogoMaterialPaginatorController.class);

	@Inject @Paginator
	private DynamicDataModel<ScoMaterial> dataModel;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	//Fornece os atributos de filtro para a realização
	//da pesquisa
	private ScoMaterial material;
	//private ScoMaterial materialSuggestion;
	private DominioSimNao matIndEstocavel;
	private String origem;
	private Boolean origemSolicitacaoCompra;
	private Boolean origemRequisicaoMaterial;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean origemConsultarParecerTecnico;
	private VScoClasMaterial classificacaoMaterial;


	private String voltarParaUrl;
	
	private final String SOLICITACAO_COMPRAS_CRUD = "compras-solicitacaoCompraCRUD";
	private final String GERACAO_REQUISICAO_MATERIAL = "estoque-geracaoRequisicaoMaterial";
	private final String CONSULTAR_PARECER = "compras-consultarParecer";
	
	private enum EnumTargetCatalogoMaterial{
		CATALOGO_MATERIAL;
	}
	
	private enum EnumHintClassifXYZCatalogoMaterial {
		HINT_CLASSIF_X, HINT_CLASSIF_Y, HINT_CLASSIF_Z;
	}
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando conversation");
		this.begin(conversation);
	}
	
	
	/**
	 * Realiza operações iniciais
	 */
	public void inicio() {
		if (origem != null && origem.equalsIgnoreCase(SOLICITACAO_COMPRAS_CRUD)) {
			origemSolicitacaoCompra = true;
			origemRequisicaoMaterial = false;
			origemConsultarParecerTecnico = false;
		} else if (origem != null
				&& origem.equalsIgnoreCase(GERACAO_REQUISICAO_MATERIAL)) {
			origemRequisicaoMaterial = true;
			origemSolicitacaoCompra = false;
			origemConsultarParecerTecnico = false;
		}else if (origem != null
				&& origem.equalsIgnoreCase(CONSULTAR_PARECER)) {
			origemConsultarParecerTecnico = true;
			origemRequisicaoMaterial = false;
			origemSolicitacaoCompra = false;		
		}  
		
		else {
			origemRequisicaoMaterial = false;
			origemSolicitacaoCompra = false;
			origemConsultarParecerTecnico = false;
		}

		if (getMaterial() == null) {
			setMaterial(new ScoMaterial());
		}
		iniciaValoresDefault();
		setDescricao(this.material.getDescricao());
		setIndSituacao(this.material.getIndSituacao());
		setAtivo(true);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Obtém uma lista de materiais para o catálogo
	 */
	@Override
	public List<ScoMaterial> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		
		List<ScoMaterial> listaMateriais = new ArrayList<ScoMaterial>();
		
		if (this.getMatIndEstocavel()!= null) {
			getMaterial().setEstocavel(this.getMatIndEstocavel().equals(DominioSimNao.S));
		}
		else {
			getMaterial().setEstocavel(null);
		}
		
		//TODO: Resolver na migração
		listaMateriais = this.estoqueFacade.pesquisarListaMateriaisParaCatalogo(firstResult,
					maxResults, orderProperty, asc, getMaterial().getCodigo(),
					getMaterial().getNome(), getMaterial().getIndSituacao(),
					getMaterial().getIndEstocavel(), getMaterial().getIndGenerico(),
					getMaterial().getUnidadeMedida()!=null?getMaterial().getUnidadeMedida().getCodigo():null,
					getMaterial().getClassifXyz(),
					getMaterial().getGrupoMaterial()!=null?getMaterial().getGrupoMaterial().getCodigo():null,getMaterial().getCodCatmat(),
					getMaterial().getCodMatAntigo(), this.classificacaoMaterial);
		
		return listaMateriais;
	}
	
	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}

	/**
	 * Rtorna a quantidade de itens da lista de materiais para catálogo
	 */
	@Override
	public Long recuperarCount() {
		String codigoUmd = null;
		Integer codigoGrp = null;
		if(getMaterial().getUnidadeMedida()!=null){
			codigoUmd = getMaterial().getUnidadeMedida().getCodigo();
		}
		
		if(getMaterial().getGrupoMaterial()!=null){
			codigoGrp = getMaterial().getGrupoMaterial().getCodigo();
		}
		
		if (this.getMatIndEstocavel()!= null) {
			getMaterial().setEstocavel(this.getMatIndEstocavel().equals(DominioSimNao.S));
		}
		else {
			getMaterial().setEstocavel(null);
		}
		
		return comprasFacade.pesquisarListaMateriaisParaCatalogoCount(
				getMaterial().getCodigo(),
				getMaterial().getNome(), getMaterial().getIndSituacao(),
				getMaterial().getIndEstocavel(), getMaterial().getIndGenerico(),
				codigoUmd,
				getMaterial().getClassifXyz(),
				codigoGrp,
				getMaterial().getCodCatmat(),
				getMaterial().getCodMatAntigo(), this.classificacaoMaterial);
	}
	
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	/**
	 * Obtém uma unidade(s) de medida ativa(s) por código ou descrição
	 * @param parametro
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaAtivaPorCodigoDescricao(String parametro) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaAtivaPorCodigoDescricao(parametro);
	}

	/**
	 * Obtém grupo(s) de material por código ou descrição
	 * @param parametro
	 * @return
	 */
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro);
	}
	
	
	
	private void iniciaValoresDefault() {
		this.setMatIndEstocavel(null);
	}
	
	/**
	 * Executa a pesquisa
	 */
	public void pesquisar(){
		reiniciarPaginator();
	}
	
	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos(){
		material = new ScoMaterial();
		this.classificacaoMaterial = null;
		iniciaValoresDefault();
		this.setAtivo(false);
	}
	
	/**
	 * abrevia strings(nome, descrição) para apresentação na tela
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	

	public String getEnumTargetCatalogoMaterial(){
		return EnumTargetCatalogoMaterial.CATALOGO_MATERIAL.toString();
	}
	
	public String recuperarHintClassifXYZ(DominioClassifyXYZ classifXYZ) {
		String retorno = "";	
		if (classifXYZ != null) {				
			switch (classifXYZ) {
			case X:
				retorno = this.getBundle().getString(EnumHintClassifXYZCatalogoMaterial.HINT_CLASSIF_X.toString());
				break;
			case Y:
				retorno = this.getBundle().getString(EnumHintClassifXYZCatalogoMaterial.HINT_CLASSIF_Y.toString());
				break;
			case Z:
				retorno = this.getBundle().getString(EnumHintClassifXYZCatalogoMaterial.HINT_CLASSIF_Z.toString());
				break;
			default:
				retorno = "";
				break;
			}
		}
		return retorno;
	}
	
	public String voltar() {
		if (StringUtils.isNotBlank(this.origem)
				&& this.origem.equalsIgnoreCase(SOLICITACAO_COMPRAS_CRUD)) {
			this.voltarParaUrl = SOLICITACAO_COMPRAS_CRUD;
		}
		if (StringUtils.isNotBlank(this.origem)
				&& this.origem.equalsIgnoreCase(GERACAO_REQUISICAO_MATERIAL)) {
			this.voltarParaUrl = GERACAO_REQUISICAO_MATERIAL;
			
		}
		if (StringUtils.isNotBlank(this.origem)
				&& this.origem.equalsIgnoreCase(CONSULTAR_PARECER)) {
			this.voltarParaUrl = CONSULTAR_PARECER;
			
		}
		return voltarParaUrl;
	}

	public String solicitarCompra() {
		if (origemSolicitacaoCompra == true) {
			return SOLICITACAO_COMPRAS_CRUD;
		} else if (origemRequisicaoMaterial == true) {
			return GERACAO_REQUISICAO_MATERIAL;
		}
		return " ";
	}
	
	public String consultarParecer() {		
		return CONSULTAR_PARECER;
	}

	public String manterMaterial() {
		return "estoque-manterMaterialCRUD";
	}

	public Boolean habilitarVoltar() {
		if (StringUtils.isNotBlank(this.origem)	&& this.origem.equalsIgnoreCase(SOLICITACAO_COMPRAS_CRUD)
				|| StringUtils.isNotBlank(this.origem)&& this.origem.equalsIgnoreCase(GERACAO_REQUISICAO_MATERIAL)
				|| StringUtils.isNotBlank(this.origem)&& this.origem.equalsIgnoreCase(CONSULTAR_PARECER) ) {
			return true;
		} else {
			return false;
		}
	}

	public DominioSimNao getMatIndEstocavel() {
		return matIndEstocavel;
	}

	public void setMatIndEstocavel(DominioSimNao matIndEstocavel) {
		this.matIndEstocavel = matIndEstocavel;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getOrigemSolicitacaoCompra() {
		return origemSolicitacaoCompra;
	}

	public void setOrigemSolicitacaoCompra(Boolean origemSolicitacaoCompra) {
		this.origemSolicitacaoCompra = origemSolicitacaoCompra;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public DynamicDataModel<ScoMaterial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMaterial> dataModel) {
		this.dataModel = dataModel;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}


	public Boolean getOrigemRequisicaoMaterial() {
		return origemRequisicaoMaterial;
	}


	public void setOrigemRequisicaoMaterial(Boolean origemRequisicaoMaterial) {
		this.origemRequisicaoMaterial = origemRequisicaoMaterial;
	}


	public Boolean getOrigemConsultarParecerTecnico() {
		return origemConsultarParecerTecnico;
	}


	public void setOrigemConsultarParecerTecnico(
			Boolean origemConsultarParecerTecnico) {
		this.origemConsultarParecerTecnico = origemConsultarParecerTecnico;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}



	/*public ScoMaterial getMaterialSuggestion() {return materialSuggestion;}
	public void setMaterialSuggestion(ScoMaterial materialSuggestion) {	this.materialSuggestion = materialSuggestion;}*/
	/*
      Durante correão do defeito #43118 foi identificado que o atributo materialSuggestion não era utilizado pra nada e nem o método listarMateriais da suggestion existia
	 */
}
