package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FcuAgrupaGrupoMaterial;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pela paginação de grupos de materiais.
 * 
 * @author luismoura
 * 
 */

public class GrupoMaterialListController extends ActionController {

	private static final String GRUPO_MATERIAL_CRUD = "grupoMaterialCRUD";

	private static final long serialVersionUID = 5486324448541862471L;

	// --------------------------------- MESSAGE CODES
	public enum EnumGrupoMaterialListMessageCode {
		M10_GRUPO_MATERIAL;
	}

	// --------------------------------- FACADES

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// --------------------------------- CONTROLES DE TELA
	private boolean ativo = false;
	private Integer gndCodigo = null;

	// --------------------------------- FILTROS
	private Integer codigo = null;
	private String descricao = null;
	private DominioSimNao patrimonio = DominioSimNao.N;
	private DominioSimNao engenhari = DominioSimNao.N;
	private DominioSimNao nutricao = DominioSimNao.N;
	private DominioSimNao exigeForn = DominioSimNao.N;
	private DominioSimNao geraMovEstoque = DominioSimNao.S;
	private DominioSimNao controleValidade = DominioSimNao.S;
	private DominioSimNao dispensario = DominioSimNao.N;

	private FsoNaturezaDespesa naturezaDespesa = null;

	private Integer codMercadoriaBb = null;
	private DominioDiaSemanaMes diaFavEntgMaterial = null;
	private FcuAgrupaGrupoMaterial agrupaGrupoMaterial = null;

	// --------------------------------- LISTAGEM
	private List<ScoGrupoMaterial> grupoMaterialList;

	// ---------------------------------

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Método executado ao entrar na página
	 */
	public void inicio() {
	 

		this.ajustarGndCodigo();
	
	}

	/**
	 * Busca um parametro de sistema utilizado na consulta
	 */
	private void ajustarGndCodigo() {
		try {
			this.gndCodigo = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_GR_NATUREZA_MAT_CONSUMO);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * SUGGESTION Natureza Despesa - List
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesa(String objParam) {
		if (this.gndCodigo == null) {
			return this.returnSGWithCount(new ArrayList<FsoNaturezaDespesa>(),pesquisarNaturezaDespesaCount(objParam));
		}
		return this.cadastrosBasicosOrcamentoFacade.pesquisarFsoNaturezaDespesaAtivosPorGrupo(gndCodigo, (String) objParam, 100);
	}

	/**
	 * SUGGESTION Natureza Despesa - Count
	 */
	public Long pesquisarNaturezaDespesaCount(String objParam) {
		if (this.gndCodigo == null) {
			return 0L;
		}
		return this.cadastrosBasicosOrcamentoFacade.pesquisarFsoNaturezaDespesaAtivosPorGrupoCount(gndCodigo, (String) objParam);
	}

	/**
	 * SUGGESTION Agrupamento - List
	 */
	public List<FcuAgrupaGrupoMaterial> pesquisarFcuAgrupaGrupoMaterialAtivos(String param) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarFcuAgrupaGrupoMaterialAtivos((String) param, 100),pesquisarFcuAgrupaGrupoMaterialAtivosCount(param));
	}

	/**
	 * SUGGESTION Agrupamento - Count
	 */
	public Long pesquisarFcuAgrupaGrupoMaterialAtivosCount(String param) {
		return centroCustoFacade.pesquisarFcuAgrupaGrupoMaterialAtivosCount((String) param);
	}

	/**
	 * Executa a pesquisa
	 */
	public void pesquisar() {
		this.ativo = true;

		Integer ntdCodigo = null;
		if (this.naturezaDespesa != null) {
			ntdCodigo = naturezaDespesa.getId().getCodigo().intValue();
		}

		Short seqAgrupa = null;
		if (this.agrupaGrupoMaterial != null) {
			seqAgrupa = agrupaGrupoMaterial.getSeq();
		}

		this.grupoMaterialList = comprasFacade.pesquisarScoGrupoMaterial(codigo, descricao, this.converterParaBoolean(patrimonio),
				this.converterParaBoolean(engenhari), this.converterParaBoolean(nutricao), this.converterParaBoolean(exigeForn),
				this.converterParaBoolean(geraMovEstoque), this.converterParaBoolean(controleValidade), this.converterParaBoolean(dispensario), ntdCodigo,
				codMercadoriaBb, diaFavEntgMaterial, seqAgrupa);
	}

	/**
	 * Converte um DominioSimNao para Boolean
	 * 
	 * @param simNao
	 * @return
	 */
	private Boolean converterParaBoolean(DominioSimNao simNao) {
		if (simNao == null) {
			return null;
		}
		return simNao.isSim();
	}

	/**
	 * Editar/Cadastrar registro
	 */
	public String novo() {
		return redirecionarGrupoMaterialCRUD();
	}
	
	public String redirecionarGrupoMaterialCRUD(){
		return GRUPO_MATERIAL_CRUD;
	}

	/**
	 * Executa exclusão
	 */
	public void excluir() {
		try {
			comprasFacade.excluirScoGrupoMaterial(codigo);
			this.pesquisar();
			super.apresentarMsgNegocio(Severity.INFO, EnumGrupoMaterialListMessageCode.M10_GRUPO_MATERIAL.toString());
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos() {
		this.codigo = null;
		this.descricao = null;
		this.patrimonio = DominioSimNao.N;
		this.engenhari = DominioSimNao.N;
		this.nutricao = DominioSimNao.N;
		this.exigeForn = DominioSimNao.N;
		this.geraMovEstoque = DominioSimNao.S;
		this.controleValidade = DominioSimNao.S;
		this.dispensario = DominioSimNao.N;
		this.naturezaDespesa = null;
		this.codMercadoriaBb = null;
		this.diaFavEntgMaterial = null;
		this.agrupaGrupoMaterial = null;

		this.ativo = false;
		this.grupoMaterialList = null;
	}

	/**
	 * Obtém o título pelo código
	 * 
	 * @param codigo
	 * @return
	 */
	public String obterTitulo(Integer codigo) {
		StringBuilder titulo = new StringBuilder();
		if (codigo != null) {
			ScoGrupoMaterial item = this.comprasFacade.obterGrupoMaterialPorId(codigo);
			if (item != null) {
				String natureza = this.obterNatureza(item);
				String agrupamento = this.obterAgrupamento(item);
				String diaFavoravel = this.obterDiaFavoravel(item);
				this.adicionarElemento(titulo, "LABEL_NATUREZA_DESPESA_GRUPO_MATERIAIS", natureza);
				this.adicionarElemento(titulo, "LABEL_CODIGO_MERCADORIA_GRUPO_MATERIAIS", item.getCodMercadoriaBb());
				this.adicionarElemento(titulo, "LABEL_DIA_FAVORITO_ENTREGA_GRUPO_MATERIAIS", diaFavoravel);
				this.adicionarElemento(titulo, "LABEL_AGRUPAMENTO_GRUPO_MATERIAIS", agrupamento);
			}
		}
		return titulo.toString();
	}

	private String obterDiaFavoravel(ScoGrupoMaterial item) {
		return item.getDiaFavEntgMaterial() != null ? String.valueOf(item.getDiaFavEntgMaterial().getCodigo()) : "";
	}

	private String obterAgrupamento(ScoGrupoMaterial item) {
		StringBuilder agrupamento = new StringBuilder();
		if (item.getAgrupaGrupoMaterial() != null) {
			agrupamento.append(item.getAgrupaGrupoMaterial().getSeq()).append(" - ").append(item.getAgrupaGrupoMaterial().getDescricao());
		}
		return agrupamento.toString();
	}

	private String obterNatureza(ScoGrupoMaterial item) {
		StringBuilder natureza = new StringBuilder(item.getNtdCodigo() != null ? item.getNtdCodigo().toString() : "");
		if (this.gndCodigo != null && item.getNtdCodigo() != null) {
			FsoNaturezaDespesa nat = this.cadastrosBasicosOrcamentoFacade.obterFsoNaturezaDespesa(gndCodigo, item.getNtdCodigo().byteValue());
			if (nat != null) {
				natureza.append(" - ").append(nat.getDescricao());
			}
		}
		return natureza.toString();
	}

	private void adicionarElemento(StringBuilder titulo, String key, Object value) {
		titulo.append(getBundle().getString(key)).append(": ").append(this.obterString(value)).append('\n');
	}

	private String obterString(Object valor) {
		if (valor == null) {
			return "";
		}
		return valor.toString();
	}

	// --------------------------------- LISTAGEM

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getPatrimonio() {
		return patrimonio;
	}

	public void setPatrimonio(DominioSimNao patrimonio) {
		this.patrimonio = patrimonio;
	}

	public DominioSimNao getEngenhari() {
		return engenhari;
	}

	public void setEngenhari(DominioSimNao engenhari) {
		this.engenhari = engenhari;
	}

	public DominioSimNao getNutricao() {
		return nutricao;
	}

	public void setNutricao(DominioSimNao nutricao) {
		this.nutricao = nutricao;
	}

	public DominioSimNao getExigeForn() {
		return exigeForn;
	}

	public void setExigeForn(DominioSimNao exigeForn) {
		this.exigeForn = exigeForn;
	}

	public DominioSimNao getGeraMovEstoque() {
		return geraMovEstoque;
	}

	public void setGeraMovEstoque(DominioSimNao geraMovEstoque) {
		this.geraMovEstoque = geraMovEstoque;
	}

	public DominioSimNao getControleValidade() {
		return controleValidade;
	}

	public void setControleValidade(DominioSimNao controleValidade) {
		this.controleValidade = controleValidade;
	}

	public DominioSimNao getDispensario() {
		return dispensario;
	}

	public void setDispensario(DominioSimNao dispensario) {
		this.dispensario = dispensario;
	}

	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public Integer getCodMercadoriaBb() {
		return codMercadoriaBb;
	}

	public void setCodMercadoriaBb(Integer codMercadoriaBb) {
		this.codMercadoriaBb = codMercadoriaBb;
	}

	public DominioDiaSemanaMes getDiaFavEntgMaterial() {
		return diaFavEntgMaterial;
	}

	public void setDiaFavEntgMaterial(DominioDiaSemanaMes diaFavEntgMaterial) {
		this.diaFavEntgMaterial = diaFavEntgMaterial;
	}

	public FcuAgrupaGrupoMaterial getAgrupaGrupoMaterial() {
		return agrupaGrupoMaterial;
	}

	public void setAgrupaGrupoMaterial(FcuAgrupaGrupoMaterial agrupaGrupoMaterial) {
		this.agrupaGrupoMaterial = agrupaGrupoMaterial;
	}

	public List<ScoGrupoMaterial> getGrupoMaterialList() {
		return grupoMaterialList;
	}

	public void setGrupoMaterialList(List<ScoGrupoMaterial> grupoMaterialList) {
		this.grupoMaterialList = grupoMaterialList;
	}

	public Integer getGndCodigo() {
		return gndCodigo;
	}

	public void setGndCodigo(Integer gndCodigo) {
		this.gndCodigo = gndCodigo;
	}

}