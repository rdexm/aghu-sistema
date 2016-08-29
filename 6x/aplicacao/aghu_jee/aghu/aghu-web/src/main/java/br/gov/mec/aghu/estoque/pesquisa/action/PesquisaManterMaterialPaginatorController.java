package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PesquisaManterMaterialVO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSiasgMaterialMestre;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisaManterMaterialPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5642105422283639731L;

	private static final String MANTER_MATERIAL_CRUD = "estoque-manterMaterialCRUD";
	
	private static final String ASSOCIAR_DESCRICAO_TECNICA = "estoque-associarDescricaoTecnicaCRUD";

	@Inject @Paginator
	private DynamicDataModel<ScoMaterial> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisaManterMaterialPaginatorController.class);

	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// Campos de filtro para pesquisa
	private Integer codigo;
	private Integer codCatmat;
	private Integer codMatAntigo;
	private String nome;
	private DominioSituacao situacao;
	private ScoGrupoMaterial grupo;
	private SceAlmoxarifado localEstoque;

	// #26669
	private String descricao;
	private ScoSiasgMaterialMestre catMat;
	private VScoClasMaterial classificacaoMaterial;

	

	// Controla exibição do "botão Novo"
	private boolean exibirBotaoNovo;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando conversation");
		this.begin(conversation);
	}
	
	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}
	

	/**
	 * Pesquisa principal e paginada
	 */
	
	/**
	 * Recupera uma instancia com os filtros de pesquisa atualizados
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private ScoMaterial getElementoFiltroPesquisa(){
		
		final ScoMaterial elementoFiltroPesquisa = new ScoMaterial();
		
		// Popula filtro de pesquisa
		elementoFiltroPesquisa.setCodigo(this.codigo != null ? this.codigo : null);
		elementoFiltroPesquisa.setNome(this.nome != null ? StringUtils.trim(this.nome) : null);
		elementoFiltroPesquisa.setIndSituacao(this.situacao != null ? this.situacao : null);
		elementoFiltroPesquisa.setGrupoMaterial(this.grupo != null ? this.grupo : null);
		elementoFiltroPesquisa.setAlmoxarifado(this.localEstoque != null ? this.localEstoque : null);
		
		if(this.catMat != null){
			String codigoCatMat = this.catMat.getCodigo();
			String catMat = codigoCatMat.replaceAll( "\\D*", "" ); 
			elementoFiltroPesquisa.setCodCatmat(Integer.valueOf(catMat));
		} else {
			elementoFiltroPesquisa.setCodCatmat(null);
		}
	
		elementoFiltroPesquisa.setCodMatAntigo(this.codMatAntigo != null ? this.codMatAntigo.longValue() : null);
		elementoFiltroPesquisa.setDescricao(this.descricao);
		
		return elementoFiltroPesquisa;
	}
	
	/**
	 * Pesquisa principal
	 */
	public void pesquisar() {

		this.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return this.comprasFacade.pesquisarManterMaterialCount(this.getElementoFiltroPesquisa(), this.classificacaoMaterial);
	}
	
	@Override
	public List<PesquisaManterMaterialVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<ScoMaterial> listaMateriais = this.comprasFacade.pesquisarManterMaterial(firstResult, maxResult, orderProperty, asc, this.getElementoFiltroPesquisa(), this.classificacaoMaterial);
		List<PesquisaManterMaterialVO> resultado = new LinkedList<PesquisaManterMaterialVO>();
		
		if(listaMateriais != null && !listaMateriais.isEmpty()){
			
			for (ScoMaterial material : listaMateriais) {
				
				PesquisaManterMaterialVO vo = new PesquisaManterMaterialVO();

				vo.setCodigo(material.getCodigo());
				vo.setNome(material.getNome());
				
				// Unidade
				vo.setUnidadeSigla(material.getUnidadeMedida().getCodigo());

				// Grupo
				vo.setGrupoCodigo(material.getGrupoMaterial().getCodigo());
				vo.setGrupoDescricao(material.getGrupoMaterial().getDescricao());
				
				// Local de estoque
				vo.setLocalEstoqueSeq(material.getAlmoxarifado().getSeq());
				vo.setLocalEstoqueDescricao(material.getAlmoxarifado().getDescricao());

				vo.setEstocavel(material.getIndEstocavel().getDescricao());
				vo.setPadronizado(material.getIndPadronizado() != null ? material.getIndPadronizado().getDescricao() : null);
				vo.setGenerico(material.getIndGenerico().getDescricao());
				vo.setControlaValidade(material.getIndControleValidade().getDescricao());
				vo.setMenorPreco(material.getIndMenorPreco().getDescricao());
				vo.setQuantidadeDisponivel(material.getIndAtuQtdeDisponivel().getDescricao());
				vo.setFaturavel(material.getIndFaturavel().getDescricao());
				vo.setSituacao(material.getIndSituacao().getDescricao());
				vo.setCodCatmat(material.getCodCatmat());
				vo.setCodMatAntigo(material.getCodMatAntigo());
				
				resultado.add(vo);
				
			}
		}
		return resultado;
	}
	
	public String redirecionarPaginaManterMaterial(){
		return MANTER_MATERIAL_CRUD;
	}
	
	public String redirecionarPaginaDescricaoTecnica(){
		return ASSOCIAR_DESCRICAO_TECNICA;
	}
	
	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		this.codigo = null;
		this.nome = null;
		this.situacao = null;	
		this.classificacaoMaterial = null;
		this.grupo = null;
		this.localEstoque = null;
		this.exibirBotaoNovo = false;
		this.catMat = null;
		this.codMatAntigo = null;
		this.descricao = null;
		this.setAtivo(false);
	}
	
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	/**
	 * Pesquisas para suggestion box
	 */	

	public List<ScoGrupoMaterial> obterGrupos(String objPesquisa) {
		return this.comprasFacade.obterGrupoMaterialPorSeqDescricao(objPesquisa);
	}
	
	public List<SceAlmoxarifado> obterLocaisEstoque(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}
	
	public List<ScoSiasgMaterialMestre> obterCatMat(String objCatMat) {
		return this.estoqueFacade.obterCatMat(objCatMat);
	}
	
	/*
	 * Getters e setters
	 */

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public ScoGrupoMaterial getGrupo() {
		return grupo;
	}

	public void setGrupo(ScoGrupoMaterial grupo) {
		this.grupo = grupo;
	}

	public SceAlmoxarifado getLocalEstoque() {
		return localEstoque;
	}

	public void setLocalEstoque(SceAlmoxarifado localEstoque) {
		this.localEstoque = localEstoque;
	}
	
	public ScoSiasgMaterialMestre getCatMat() {
		return catMat;
	}

	public void setCatMat(ScoSiasgMaterialMestre catMat) {
		this.catMat = catMat;
	}


	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getCodCatmat() {
		return codCatmat;
	}

	public void setCodCatmat(Integer codCatmat) {
		this.codCatmat = codCatmat;
	}

	public Integer getCodMatAntigo() {
		return codMatAntigo;
	}

	public void setCodMatAntigo(Integer codMatAntigo) {
		this.codMatAntigo = codMatAntigo;
	}
	
	public DynamicDataModel<ScoMaterial> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMaterial> dataModel) {
	 this.dataModel = dataModel;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}
}
