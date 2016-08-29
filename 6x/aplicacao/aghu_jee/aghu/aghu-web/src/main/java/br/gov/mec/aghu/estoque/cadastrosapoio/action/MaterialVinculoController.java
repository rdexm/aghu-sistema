package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialVinculo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MaterialVinculoController extends ActionController {

	private static final String ESTOQUE_MANTER_MATERIAL_CRUD2 = "estoque-manterMaterialCRUD";

	private static final String ESTOQUE_MANTER_MATERIAL_CRUD = ESTOQUE_MANTER_MATERIAL_CRUD2;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(MaterialVinculoController.class);

	/**
	 * Realiza o vinculo entre os materiais
	 */
	private static final long serialVersionUID = 3506253757664308300L;

	private ScoMaterial material;

	private ScoMaterial materialVinculo;

	@EJB
	private IComprasFacade comprasFacade;

	private String mensagem;

	private Integer codigoMaterial;

	private boolean criadoNovoMaterial;

	private Integer codigoMaterialPrincipal;

	private List<ScoMaterialVinculo> listaMateriaisVinculados;

	public void iniciar() {
	 

		if (this.codigoMaterial != null && !this.criadoNovoMaterial) {
			setMaterial(this.comprasFacade.obterScoMaterialPorChavePrimaria(getCodigoMaterial()));
			// guarda o codigo do material principal para utilizar caso precise
			// criar um novo material
			setCodigoMaterialPrincipal(getCodigoMaterial());
			listarMateriasVinculados();

		} else {
			setMaterial(this.comprasFacade.obterScoMaterialPorChavePrimaria(getCodigoMaterialPrincipal()));
			setMaterialVinculo(this.comprasFacade.obterScoMaterialPorChavePrimaria(getCodigoMaterial()));
			listarMateriasVinculados();
		}
	
	}

	/**
	 * Método que adiciona matérias vinculados
	 */

	public void adicionarMaterialVinculo() {
		this.comprasFacade.persistirMaterialVinculado(material, materialVinculo);
		// limpa campo
		setMaterialVinculo(null);
		listarMateriasVinculados();
		mensagem = "MENSAGEM_INCLUSAO_ACESSORIO_INSUMO";
		this.apresentarMsgNegocio(Severity.INFO, mensagem);

	}

	/**
	 * Método que lista matérias vinculados
	 */
	private void listarMateriasVinculados() {
		setListaMateriaisVinculados(this.comprasFacade.obterMateriaisVinculados(getMaterial()));
	}

	public Long pesquisarMateriaisCount(String param) {
		return this.comprasFacade.listarScoMatriaisAtivosCount(param);
	}

	/**
	 * Metodo para Suggestion Box de Material
	 */
	public List<ScoMaterial> pesquisarMateriais(String param) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisAtivos(param, ScoMaterial.Fields.CODIGO.toString()),pesquisarMateriaisCount(param));
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return ESTOQUE_MANTER_MATERIAL_CRUD;
	}

	public void setListaMateriaisVinculados(List<ScoMaterialVinculo> listaMateriaisVinculados) {
		this.listaMateriaisVinculados = listaMateriaisVinculados;

	}

	public List<ScoMaterialVinculo> getListaMateriaisVinculados() {
		return listaMateriaisVinculados;
	}

	public void excluirVinculo(ScoMaterialVinculo materialVinculo) {
		try {
			this.comprasFacade.excluirMaterialVinculado(materialVinculo);

			this.listaMateriaisVinculados = this.comprasFacade.obterMateriaisVinculados(getMaterial());

			listarMateriasVinculados();
			mensagem = "MENSAGEM_EXCLUSAO_ACESSORIO_INSUMO";

			this.apresentarMsgNegocio(Severity.INFO, mensagem);

		} catch (BaseException e) {
			LOG.error(e, e.getCause());
			super.apresentarExcecaoNegocio(e);
		}

	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterialVinculo(ScoMaterial materialVinculo) {
		this.materialVinculo = materialVinculo;
	}

	public ScoMaterial getMaterialVinculo() {
		return materialVinculo;
	}

	public String incluirMaterial() {
		return ESTOQUE_MANTER_MATERIAL_CRUD2;
	}

	public void setCriadoNovoMaterial(Boolean criadoNovoMaterial) {
		this.criadoNovoMaterial = criadoNovoMaterial;
	}

	public Boolean getCriadoNovoMaterial() {
		return criadoNovoMaterial;
	}

	public void setCodigoMaterialPrincipal(Integer codigoMaterialPrincipal) {
		this.codigoMaterialPrincipal = codigoMaterialPrincipal;
	}

	public Integer getCodigoMaterialPrincipal() {
		return codigoMaterialPrincipal;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
}