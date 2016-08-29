package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class UnidadeMedidaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3956333382492469930L;

	private static final String UNIDADE_MEDIDA_CRUD = "unidadeMedidaCRUD";

	@EJB 
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private SecurityController securityController;	
	
	private ScoUnidadeMedida unMedida = new ScoUnidadeMedida();
	private String codigo;
	private boolean visualizar;

	@Inject @Paginator
	private DynamicDataModel<ScoUnidadeMedida> dataModel;
	
	private ScoUnidadeMedida selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioEstoque,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}

	@Override
	public List<ScoUnidadeMedida> recuperarListaPaginada(Integer _firstResult, Integer _maxResult, String _orderProperty, boolean _asc) {
		return comprasCadastrosBasicosFacade.pesquisarUnidadeMedida(_firstResult, _maxResult,_orderProperty, _asc, this.unMedida);
	}

	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.listarUnidadeMedidaCount(this.unMedida);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		this.unMedida = new ScoUnidadeMedida();
	}
	
	public String inserir() {
		return UNIDADE_MEDIDA_CRUD;
	}
	
	public String editar() {
		return UNIDADE_MEDIDA_CRUD;
	}
	
	public String visualizar() {
		return UNIDADE_MEDIDA_CRUD;
	}

	public ScoUnidadeMedida getUnMedida() {
		return unMedida;
	}

	public void setUnMedida(ScoUnidadeMedida unMedida) {
		this.unMedida = unMedida;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public DynamicDataModel<ScoUnidadeMedida> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoUnidadeMedida> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoUnidadeMedida getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoUnidadeMedida selecionado) {
		this.selecionado = selecionado;
	}
}
