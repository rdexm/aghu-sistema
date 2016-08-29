package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MotivoAlteracaoAfPaginatorController extends ActionController implements ActionPaginator{



	private static final long serialVersionUID = -7781706374954598794L;


	private static final String MOTIVO_ALTERACAO_AF_CRUD = "motivoAlteracaoAfCRUD";


	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	

	private ScoMotivoAlteracaoAf scoMotivoAlteracaoAf = new ScoMotivoAlteracaoAf();
	
	private Short codigo;
	
	@Inject @Paginator
	private DynamicDataModel<ScoMotivoAlteracaoAf> dataModel;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		dataModel.limparPesquisa();
		this.setScoMotivoAlteracaoAf(new ScoMotivoAlteracaoAf());
	}
	
	public String inserir(){
		return MOTIVO_ALTERACAO_AF_CRUD;
	}
	
	public String editar(){
		return MOTIVO_ALTERACAO_AF_CRUD;
	}
	
	public String visualizar(){
		return MOTIVO_ALTERACAO_AF_CRUD;
	}
	
	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarScoMotivoAlteracaoAfCount(this.getScoMotivoAlteracaoAf());
	}

	@Override
	public List<ScoMotivoAlteracaoAf> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<ScoMotivoAlteracaoAf> result = comprasCadastrosBasicosFacade
				.pesquisarScoMotivoAlteracaoAf(firstResult, maxResult, orderProperty, asc, this.getScoMotivoAlteracaoAf());

		if (result == null) {
			result = new ArrayList<ScoMotivoAlteracaoAf>();
		}

		return result;
	}

	public void setScoMotivoAlteracaoAf(
			ScoMotivoAlteracaoAf ScoMotivoAlteracaoAf) {
		this.scoMotivoAlteracaoAf = ScoMotivoAlteracaoAf;
	}

	public ScoMotivoAlteracaoAf getScoMotivoAlteracaoAf() {
		return scoMotivoAlteracaoAf;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public Short getCodigo() {
		return this.codigo;
	}

	public DynamicDataModel<ScoMotivoAlteracaoAf> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMotivoAlteracaoAf> dataModel) {
		this.dataModel = dataModel;
	}
}
