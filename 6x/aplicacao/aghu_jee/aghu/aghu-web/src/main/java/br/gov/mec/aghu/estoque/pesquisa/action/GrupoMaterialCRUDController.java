package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FcuAgrupaGrupoMaterial;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pela paginação de grupos de materiais.
 * 
 * @author luismoura
 * 
 */

public class GrupoMaterialCRUDController extends ActionController {

	private static final String GRUPO_MATERIAL_LIST = "grupoMaterialList";

	private static final long serialVersionUID = 5486324448541862471L;

	// --------------------------------- MESSAGE CODES
	public enum EnumGrupoMaterialCRUDMessageCode {
		M1_GRUPO_MATERIAL, //
		M2_GRUPO_MATERIAL, //
		;
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

	// --------------------------------- EDICAO
	private Integer codGrupMat = null;
	private Integer gndCodigo = null;
	private ScoGrupoMaterial grupoMaterial;
	private FsoNaturezaDespesa naturezaDespesa;

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
		if (codGrupMat != null) {
			this.ajustarGrupoMaterial();
		} else {
			this.inicializaCampos();
		}
	
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
	 * Obter Natureza Despesa
	 */
	private void ajustarNaturezaDespesa(Byte codigo) {
		if (this.gndCodigo == null || codigo == null) {
			this.naturezaDespesa = null;
			return;
		}
		this.naturezaDespesa = this.cadastrosBasicosOrcamentoFacade.obterFsoNaturezaDespesa(this.gndCodigo, codigo);
	}

	/**
	 * Busca um parametro de sistema utilizado na consulta
	 */
	private void ajustarGrupoMaterial() {
		this.grupoMaterial = comprasFacade.obterGrupoMaterialPorId(codGrupMat);
		if (this.grupoMaterial != null) {
			Byte nat = (grupoMaterial.getNtdCodigo() != null ? grupoMaterial.getNtdCodigo().byteValue() : null);
			this.ajustarNaturezaDespesa(nat);
		}
	}

	/**
	 * Inicializa os campos da tela
	 */
	public void inicializaCampos() {
		this.grupoMaterial = new ScoGrupoMaterial();
		this.grupoMaterial.setCodigo(null);
		this.grupoMaterial.setDescricao(null);
		this.grupoMaterial.setPatrimonio(Boolean.FALSE);
		this.grupoMaterial.setEngenhari(Boolean.FALSE);
		this.grupoMaterial.setNutricao(Boolean.FALSE);
		this.grupoMaterial.setExigeForn(Boolean.FALSE);
		this.grupoMaterial.setGeraMovEstoque(Boolean.TRUE);
		this.grupoMaterial.setControleValidade(Boolean.TRUE);
		this.grupoMaterial.setDispensario(Boolean.FALSE);
		this.grupoMaterial.setNtdCodigo(null);
		this.grupoMaterial.setCodMercadoriaBb(null);
		this.grupoMaterial.setDiaFavEntgMaterial(null);
		this.grupoMaterial.setAgrupaGrupoMaterial(null);
	}

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos() {
		this.codGrupMat = null;
		this.gndCodigo = null;
		this.grupoMaterial = null;
		this.naturezaDespesa = null;
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
	 * Cancelar
	 * 
	 * @return
	 */
	public String cancelar() {
		this.limparCampos();
		return GRUPO_MATERIAL_LIST;
	}

	/**
	 * Executa a persistencia
	 */
	public String confirmar() {
		try {
			boolean inclusao = (grupoMaterial.getCodigo() == null);
			if(this.naturezaDespesa != null){
				grupoMaterial.setNtdCodigo(this.naturezaDespesa.getId().getCodigo().intValue());
			}			
			comprasFacade.persistirScoGrupoMaterial(this.grupoMaterial);
			if (inclusao) {
				super.apresentarMsgNegocio(Severity.INFO, EnumGrupoMaterialCRUDMessageCode.M1_GRUPO_MATERIAL.toString());
			} else {
				super.apresentarMsgNegocio(Severity.INFO, EnumGrupoMaterialCRUDMessageCode.M2_GRUPO_MATERIAL.toString());
			}
			return GRUPO_MATERIAL_LIST;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	// --------------------------------- LISTAGEM

	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public Integer getCodGrupMat() {
		return codGrupMat;
	}

	public void setCodGrupMat(Integer codGrupMat) {
		this.codGrupMat = codGrupMat;
	}

	public Integer getGndCodigo() {
		return gndCodigo;
	}

	public void setGndCodigo(Integer gndCodigo) {
		this.gndCodigo = gndCodigo;
	}
}