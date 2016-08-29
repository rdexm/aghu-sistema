package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class JustificativaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1289998913199756967L;

	private static final String JUSTIFICATIVA_CRUD = "justificativaCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private SecurityController securityController;	

	private ScoJustificativa justificativa = new ScoJustificativa();

	private Short codigo;
	
	@Inject @Paginator
	private DynamicDataModel<ScoJustificativa> dataModel;
	
	private ScoJustificativa selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setJustificativa(new ScoJustificativa());
	}

	public String inserir() {
		return JUSTIFICATIVA_CRUD;
	}
	
	public String editar() {
		return JUSTIFICATIVA_CRUD;
	}
	public String visualizar() {
		return JUSTIFICATIVA_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarJustificativaCount(this
				.getJustificativa());
	}

	@Override
	public List<ScoJustificativa> recuperarListaPaginada(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarJustificativas(firstResult, maxResults, orderProperty, asc, this.getJustificativa());
	}

	public ScoJustificativa getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(ScoJustificativa justificativa) {
		this.justificativa = justificativa;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public DynamicDataModel<ScoJustificativa> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoJustificativa> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoJustificativa getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoJustificativa selecionado) {
		this.selecionado = selecionado;
	}
}
