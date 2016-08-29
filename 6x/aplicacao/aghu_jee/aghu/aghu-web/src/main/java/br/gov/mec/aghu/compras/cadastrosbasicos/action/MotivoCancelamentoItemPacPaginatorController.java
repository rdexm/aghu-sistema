package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMotivoCancelamentoItem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * "#24709 - Cadastro de Motivo de Cancelamento de Itens PAC"
 * @author joao.gloria
 */
public class MotivoCancelamentoItemPacPaginatorController extends ActionController implements ActionPaginator{

	@Inject @Paginator
	private DynamicDataModel<ScoMotivoCancelamentoItem> dataModel;
	private static final long serialVersionUID = -7781706374954598794L;
	private static final String MOTIVO_CANCELAMENTO_ITEM_PAC_CRUD = "motivoCancelamentoItemPacCRUD";
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IPermissionService permissionService;

	
	private String codigo;
	
	private String descricao;
	private DominioSituacao indAtivo;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		final Boolean userEditPermission = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastrarApoioPAC", "gravar");
		this.dataModel.setUserEditPermission(userEditPermission);
	}
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		this.codigo = null;
		this.descricao = null;
		this.indAtivo = null;
		this.dataModel.limparPesquisa();		
	}
	
	public String iniciarNovo(){		
		return MOTIVO_CANCELAMENTO_ITEM_PAC_CRUD;
	}
	
	public String editar() {
		return MOTIVO_CANCELAMENTO_ITEM_PAC_CRUD;
	}
	
	public ScoMotivoCancelamentoItem montarMotivoCancelamentoItem(){
		
        ScoMotivoCancelamentoItem scoMotivoCancelamentoItem = new ScoMotivoCancelamentoItem();		
		scoMotivoCancelamentoItem.setCodigo(this.codigo);
		scoMotivoCancelamentoItem.setDescricao(this.descricao);
		scoMotivoCancelamentoItem.setIndAtivo(this.indAtivo);
		
		return scoMotivoCancelamentoItem;
		
	}
	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarScoMotivoCancelamentoItemCount(montarMotivoCancelamentoItem());
	}

	@Override
	public List<ScoMotivoCancelamentoItem> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<ScoMotivoCancelamentoItem> result = comprasCadastrosBasicosFacade
				.pesquisarScoMotivoCancelamentoItem(firstResult, maxResult, orderProperty, asc, montarMotivoCancelamentoItem());

		if (result == null) {
			result = new ArrayList<ScoMotivoCancelamentoItem>();
		}
		return result;
	}
	
	
	
	public void setCodigo(String codigo){
		this.codigo = codigo;
	}
	
	public String getCodigo(){
		return this.codigo;
	}
	public DynamicDataModel<ScoMotivoCancelamentoItem> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMotivoCancelamentoItem> dataModel) {
	 this.dataModel = dataModel;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(DominioSituacao indAtivo) {
		this.indAtivo = indAtivo;
	}
}
