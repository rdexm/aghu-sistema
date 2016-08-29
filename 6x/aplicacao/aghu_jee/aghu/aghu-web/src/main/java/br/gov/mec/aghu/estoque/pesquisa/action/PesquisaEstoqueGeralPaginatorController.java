package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EstoqueGeralVO;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PesquisaEstoqueGeralPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<SceEstoqueGeral> dataModel;

	private static final long serialVersionUID = 8105157728584986863L;

	private static final String MANTER_MATERIAL = "estoque-manterMaterialCRUD";
	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SecurityController securityController;	

	private EstoqueGeralVO estoqueGeralFiltro;
	private EstoqueGeralVO estoqueGeralDataCompetencia;
	
	// Filtro Intervalo da Data/Mês de Competência
	private DominioComparacaoDataCompetencia comparacaoDataCompetencia = DominioComparacaoDataCompetencia.IGUAL;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(getEstoqueGeralFiltro() == null){
			setEstoqueGeralDataCompetencia(null);
			setEstoqueGeralFiltro(new EstoqueGeralVO());
			getEstoqueGeralFiltro().setMaterial(new ScoMaterial());
		}
	
	}
	
	public void limparCampos(){
		dataModel.limparPesquisa();
		setEstoqueGeralDataCompetencia(null);
		setEstoqueGeralFiltro(new EstoqueGeralVO());
		getEstoqueGeralFiltro().setMaterial(new ScoMaterial());
		getEstoqueGeralFiltro().getMaterial().setGrupoMaterial(null);
		getEstoqueGeralFiltro().setFornecedor(null);
		getEstoqueGeralFiltro().setUnidadeMedida(null);
		this.comparacaoDataCompetencia = DominioComparacaoDataCompetencia.IGUAL;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public List<SceEstoqueGeral> recuperarListaPaginada(Integer firstResult, Integer maxResult,String orderProperty, boolean asc) {
		Integer numeroFornecedor = null,
				codigoMaterial = null,
				codigoGrupoMaterial = null;
		String codigoUnidadeMedida = null,
			   nomeMaterial = null;
		Date dataComp = null;
		Boolean estocavel = null,
				generico = null;
		
		if(getEstoqueGeralFiltro().getFornecedor() != null){
			numeroFornecedor = getEstoqueGeralFiltro().getFornecedor().getNumero();
		}
		if(getEstoqueGeralFiltro().getMaterial() != null){
			codigoMaterial = getEstoqueGeralFiltro().getMaterial().getCodigo();
			nomeMaterial = getEstoqueGeralFiltro().getMaterial().getNome();
			if(getEstoqueGeralFiltro().getMaterial().getGrupoMaterial() != null){
				codigoGrupoMaterial = getEstoqueGeralFiltro().getMaterial().getGrupoMaterial().getCodigo();
			}
		}
		if(getEstoqueGeralFiltro().getUnidadeMedida() != null){
			codigoUnidadeMedida = getEstoqueGeralFiltro().getUnidadeMedida().getCodigo();
		}
		if(getEstoqueGeralDataCompetencia() != null){
			dataComp = getEstoqueGeralDataCompetencia().getDtCompetencia();
		}
		estocavel = getEstoqueGeralFiltro().getMaterial().getIndEstocavelBoolean();
		generico = getEstoqueGeralFiltro().getMaterial().getIndGenericoBoolean();
		List<SceEstoqueGeral> listaEstoqueGeral = this.estoqueFacade.pesquisarEstoqueGeral(
											   firstResult,
											   maxResult,
											   orderProperty,
											   false,
											   dataComp, 
											   this.comparacaoDataCompetencia,
											   numeroFornecedor, 
											   codigoMaterial,
											   nomeMaterial,
											   codigoGrupoMaterial, 
											   estocavel,
											   generico, 
											   codigoUnidadeMedida, 
											   getEstoqueGeralFiltro().getClassificacaoAbc(),
										       getEstoqueGeralFiltro().getSubClassificacaoAbc());
		
		return listaEstoqueGeral;
	}

	@Override
	public Long recuperarCount() {
		Integer numeroFornecedor = null,
				codigoMaterial = null,
				codigoGrupoMaterial = null;
		String codigoUnidadeMedida = null,
			   nomeMaterial = null;
		Date dataComp = null;
		Boolean estocavel = null,
				generico = null;

		if(getEstoqueGeralFiltro().getFornecedor() != null){
			numeroFornecedor = getEstoqueGeralFiltro().getFornecedor().getNumero();
		}
		if(getEstoqueGeralFiltro().getMaterial() != null){
			codigoMaterial = getEstoqueGeralFiltro().getMaterial().getCodigo();
			nomeMaterial = getEstoqueGeralFiltro().getMaterial().getNome();
			if(getEstoqueGeralFiltro().getMaterial().getGrupoMaterial() != null){
				codigoGrupoMaterial = getEstoqueGeralFiltro().getMaterial().getGrupoMaterial().getCodigo();
			}
		}
		if(getEstoqueGeralFiltro().getUnidadeMedida() != null){
			codigoUnidadeMedida = getEstoqueGeralFiltro().getUnidadeMedida().getCodigo();
		}
		if(getEstoqueGeralDataCompetencia() != null){
			dataComp = getEstoqueGeralDataCompetencia().getDtCompetencia();
		}
		estocavel = getEstoqueGeralFiltro().getMaterial().getIndEstocavelBoolean();
		generico = getEstoqueGeralFiltro().getMaterial().getIndGenericoBoolean();
		
		return this.estoqueFacade.pesquisarEstoqueGeralCount(
				 dataComp, 
				 this.comparacaoDataCompetencia,
				 numeroFornecedor, 
				 codigoMaterial,
				 nomeMaterial,
				 codigoGrupoMaterial, 
				 estocavel,
				 generico, 
				 codigoUnidadeMedida, 
				 getEstoqueGeralFiltro().getClassificacaoAbc(),
				 getEstoqueGeralFiltro().getSubClassificacaoAbc());
	}

	/**
	 * Obtém uma unidade de medida por código ou descrição
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(String parametro) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricao(parametro);
	}
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro);
	}

	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(Object parametro) {
		return this.comprasFacade.listarScoMateriais(parametro, null); //FIXME
	}

	/**
	 * Pesquisa para o suggestion de fornecedores
	 */
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(
			String objPesquisa) {
		return this.comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(
				objPesquisa);
	}	

	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 */
	public List<EstoqueGeralVO> pesquisarDatasCompetenciasEstoqueGeralPorMesAno(String paramPesquisa){
		List<EstoqueGeralVO> lista = null;
		try {
			lista = this.estoqueFacade.pesquisarDatasCompetenciasEstoqueGeralPorMesAno(paramPesquisa);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}	
		return lista;
	}
	
	public String manterMaterial(){
		return MANTER_MATERIAL;
	}
	
	public String pesquisarAlmoxarifado(){
		return PESQUISAR_ESTOQUE_ALMOXARIFADO;
	}
	
	

	public EstoqueGeralVO getEstoqueGeralFiltro() {
		return estoqueGeralFiltro;
	}

	public void setEstoqueGeralFiltro(EstoqueGeralVO estoqueGeralFiltro) {
		this.estoqueGeralFiltro = estoqueGeralFiltro;
	}

	public EstoqueGeralVO getEstoqueGeralDataCompetencia() {
		return estoqueGeralDataCompetencia;
	}

	public void setEstoqueGeralDataCompetencia(
			EstoqueGeralVO estoqueGeralDataCompetencia) {
		this.estoqueGeralDataCompetencia = estoqueGeralDataCompetencia;
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null) {
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public DominioComparacaoDataCompetencia getComparacaoDataCompetencia() {
		return comparacaoDataCompetencia;
	}
	
	public void setComparacaoDataCompetencia(DominioComparacaoDataCompetencia comparacaoDataCompetencia) {
		this.comparacaoDataCompetencia = comparacaoDataCompetencia;
	}
	
	public Boolean getAcessoEdicaoCadastroMaterial() {
		return securityController.usuarioTemPermissao("/estoque/cadastrosapoio/manterMaterialCRUD.xhtml", "render");
	}
	
	public Boolean getRenderizarLinkEstoqueAlmoxarifado() {
		return securityController.usuarioTemPermissao("/estoque/pesquisa/pesquisarEstoqueAlmoxarifado.xhtml", "render");
	}

	public DynamicDataModel<SceEstoqueGeral> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceEstoqueGeral> dataModel) {
		this.dataModel = dataModel;
	}
}