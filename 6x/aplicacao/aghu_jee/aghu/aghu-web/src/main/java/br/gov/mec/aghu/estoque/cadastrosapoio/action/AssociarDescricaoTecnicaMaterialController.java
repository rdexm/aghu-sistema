package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.pesquisa.action.PesquisaManterMaterialPaginatorController;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class AssociarDescricaoTecnicaMaterialController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(AssociarDescricaoTecnicaMaterialController.class);
	
	private static final String PESQUISA_MANTER_MATERIAL = "estoque-pesquisaManterMaterial";
	
	private static final String CADASTRO_DESCRICAO_TECNICA_CRUD = "compras-cadastroDescricaoTecnicaCRUD";

	/**
	 * Associar Descrição Técnica Material
	 */
	private static final long serialVersionUID = 3506253757664308300L;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private PesquisaManterMaterialPaginatorController pesquisaManterMaterialPaginatorController;

	private ScoMaterial material = null;
	private Integer codigo;
	private ScoDescricaoTecnicaPadrao descricaoTecnicaPadrao;
	private List<ScoDescricaoTecnicaPadrao> listaDescricaoTecnicaPadrao;
	private Integer codigoDescricaoTecnica;
	private boolean retornoIntegracaoDescricaoTecnica = false;
	private boolean editarDescricaoTecnica = false;

	public List<ScoDescricaoTecnicaPadrao> getListaDescricaoTecnicaPadrao() {
		return listaDescricaoTecnicaPadrao;
	}

	public void setListaDescricaoTecnicaPadrao(List<ScoDescricaoTecnicaPadrao> listaDescricaoTecnicaPadrao) {
		this.listaDescricaoTecnicaPadrao = listaDescricaoTecnicaPadrao;
	}

	public void iniciar() {
	 


		if (isRetornoIntegracaoDescricaoTecnica() && this.codigoDescricaoTecnica != null) {
			if (this.editarDescricaoTecnica) {
				this.editarDescricaoTecnica = false;
				return;
			}

			this.descricaoTecnicaPadrao = this.comprasFacade.buscarScoDescricaoTecnicaPadraoByCodigo(this.codigoDescricaoTecnica);
			this.codigoDescricaoTecnica = null;
			return;
		}

		if (this.codigo != null) {
			setMaterial(this.comprasFacade.obterScoMaterialPorChavePrimaria(this.codigo));
			this.listaDescricaoTecnicaPadrao = this.comprasFacade.buscarListaDescricaoTecnicaMaterial(getMaterial());
			if (this.listaDescricaoTecnicaPadrao == null || this.listaDescricaoTecnicaPadrao.isEmpty()) {
				this.listaDescricaoTecnicaPadrao = new ArrayList<ScoDescricaoTecnicaPadrao>();
			}
		}
		this.listaDescricaoTecnicaPadrao = this.comprasFacade.buscarListaDescricaoTecnicaMaterial(getMaterial());
	
	}

	/**
	 * Método que adiciona descrição tecnica padrão
	 */

	public void adicionarDescricaoTecnica() {
		if (descricaoTecnicaPadrao != null && descricaoTecnicaPadrao.getCodigo() != null) {
			listaDescricaoTecnicaPadrao.add(descricaoTecnicaPadrao);
		}
		this.descricaoTecnicaPadrao = null;
		this.codigoDescricaoTecnica = null;
	}

	public Long pesquisarDescricaoTecnicaMateriaisCount(String param) {
		return this.comprasFacade.listarScoDescricaoTecnicaPadraoCount(param);
	}

	/**
	 * Metodo para Suggestion Box de Descricao Tecnica Materiais
	 */
	public List<ScoDescricaoTecnicaPadrao> pesquisarDescricaoTecnicaMateriais(String param) {
		return this.returnSGWithCount(this.comprasFacade.listarScoDescricaoTecnicaPadrao(param),pesquisarDescricaoTecnicaMateriaisCount(param));
	}

	public String criarDescricaoTecnica() {
		descricaoTecnicaPadrao = null;
		codigo = null;
		codigoDescricaoTecnica = null;
		return CADASTRO_DESCRICAO_TECNICA_CRUD;
	}

	public String editarDescricaoTecnica(ScoDescricaoTecnicaPadrao descricao) {
		this.editarDescricaoTecnica = true;
		setCodigoDescricaoTecnica(Integer.valueOf(descricao.getCodigo()));
		return CADASTRO_DESCRICAO_TECNICA_CRUD;
	}

	public void excluirDescricaoTecnica(ScoDescricaoTecnicaPadrao descricao) {
		listaDescricaoTecnicaPadrao.remove(descricao);
	}

	public void confirmar() {
		String mensagem = this.comprasFacade.associarMaterialDescricaoTecnicaByMaterial(this.material, this.listaDescricaoTecnicaPadrao);
		LOG.info(mensagem);
		this.apresentarMsgNegocio(Severity.INFO, mensagem);
		this.pesquisaManterMaterialPaginatorController.getDataModel().reiniciarPaginator();
	}

	public String voltar() {
		return PESQUISA_MANTER_MATERIAL;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public ScoDescricaoTecnicaPadrao getDescricaoTecnicaPadrao() {
		return descricaoTecnicaPadrao;
	}

	public void setDescricaoTecnicaPadrao(ScoDescricaoTecnicaPadrao descricaoTecnicaPadrao) {
		this.descricaoTecnicaPadrao = descricaoTecnicaPadrao;
	}

	public Integer getCodigoDescricaoTecnica() {
		return codigoDescricaoTecnica;
	}

	public void setCodigoDescricaoTecnica(Integer codigoDescricaoTecnica) {
		this.codigoDescricaoTecnica = codigoDescricaoTecnica;
	}

	public boolean isRetornoIntegracaoDescricaoTecnica() {
		return retornoIntegracaoDescricaoTecnica;
	}

	public void setRetornoIntegracaoDescricaoTecnica(boolean retornoIntegracaoDescricaoTecnica) {
		this.retornoIntegracaoDescricaoTecnica = retornoIntegracaoDescricaoTecnica;
	}

	public String edicao() {
		return "edicaoDescricaoPadrao";
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

}
