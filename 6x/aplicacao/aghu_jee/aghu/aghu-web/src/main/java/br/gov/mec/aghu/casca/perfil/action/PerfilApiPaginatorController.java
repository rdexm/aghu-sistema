package br.gov.mec.aghu.casca.perfil.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PerfilApiPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3729713876475195723L;
	
	private final String REDIRECIONA_CADASTRAR_PERFIL = "cadastrarPerfilApi";	

	@EJB
	private ICascaFacade cascaFacade;	
	
	@Inject
	private PerfilApiController perfilApiController;
	
	@Inject @Paginator
	private DynamicDataModel<PerfilApi> dataModel;
	
	private String nome;
	private String descricao;
	private DominioSituacao situacao;
	

	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public String editar(Integer seq) {	
		perfilApiController.setSeqPerfil(seq);
		return REDIRECIONA_CADASTRAR_PERFIL;
	}

	public String incluirPerfilApi() {
		perfilApiController.setSeqPerfil(null);
		return REDIRECIONA_CADASTRAR_PERFIL;
	}
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarPerfilApiCount(nome, descricao, situacao);
	}

	@Override
	public List<PerfilApi> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarPerfilApi(firstResult, maxResult, orderProperty, asc, nome, descricao, situacao);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.nome = null;
		this.descricao = null;
		this.situacao = null;
		this.dataModel.limparPesquisa();
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public PerfilApiController getPerfilApiController() {
		return perfilApiController;
	}

	public void setPerfilApiController(PerfilApiController perfilApiController) {
		this.perfilApiController = perfilApiController;
	}

	public DynamicDataModel<PerfilApi> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PerfilApi> dataModel) {
		this.dataModel = dataModel;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}