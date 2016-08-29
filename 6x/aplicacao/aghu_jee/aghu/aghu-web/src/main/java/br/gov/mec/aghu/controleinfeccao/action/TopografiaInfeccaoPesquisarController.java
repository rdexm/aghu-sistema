package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class TopografiaInfeccaoPesquisarController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -518952133860286863L;
	private static final String INCLUSAO_EDICAO = "topografiaInfeccaoCadastro";
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private String descricao;
	private DominioSituacao situacao;
	@Inject @Paginator
	private DynamicDataModel<TopografiaInfeccaoVO> dataModel;
	private TopografiaInfeccaoVO itemSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		itemSelecionado = new TopografiaInfeccaoVO();
		definirPermissoes();
	
	}
	

	private void definirPermissoes() {
		
		final Boolean permissaoExcluir = permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "superTopografiaInfeccao", "excluir");
		dataModel.setUserRemovePermission(permissaoExcluir);
		
		final Boolean permissaoEditarSuperTopografiaInfeccao = permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "superTopografiaInfeccao", "editar");
		final Boolean permissaoEditarManterTopografiaInfeccao = permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterTopografiaInfeccao", "editar");
		
		dataModel.setUserRemovePermission(permissaoEditarSuperTopografiaInfeccao  ? permissaoEditarSuperTopografiaInfeccao : permissaoEditarManterTopografiaInfeccao);
		
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		dataModel.limparPesquisa();
		descricao =  null;
		situacao = null;
		itemSelecionado = null;
	}
	
	public String  incluir(){
		return INCLUSAO_EDICAO;
	}
	
	public String editar(){
		return INCLUSAO_EDICAO;
	}
	
	public void  excluir(){

		try {
			controleInfeccaoFacade.excluirTopografiaInfeccao(itemSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MSG_TOPO_INFE_SUCESSO_EXCLUSAO", itemSelecionado.getDescricao());
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return controleInfeccaoFacade.listarMciTopografiaInfeccaoPorDescricaoESituacaoCount(descricao, situacao);
	}

	@Override
	public List<TopografiaInfeccaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return controleInfeccaoFacade.listarMciTopografiaInfeccaoPorDescricaoESituacao(firstResult, maxResult,	orderProperty, asc, descricao, situacao);
	}

	// get set
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public TopografiaInfeccaoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(TopografiaInfeccaoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public DynamicDataModel<TopografiaInfeccaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<TopografiaInfeccaoVO> dataModel) {
		this.dataModel = dataModel;
	}
	
}
