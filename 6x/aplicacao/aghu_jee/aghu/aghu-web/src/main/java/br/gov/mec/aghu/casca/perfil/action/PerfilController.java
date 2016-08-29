package br.gov.mec.aghu.casca.perfil.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author Cristiano Quadros
 * 
 */
public class PerfilController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -7041032227095505972L;
	private final String PAGE_CADASTRAR_PERFIL = "cadastrarPerfil";
	private final String PAGE_PESQUISAR_PERFIL = "pesquisarPerfil";
	private final String PAGE_HISTORICO_PERFIL = "historicoPerfil";
	private final String PAGE_ASSOCIAR_PERMISSAO = "manterPerfilPermissao";

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private Perfil perfil;
	private String nome;
	private String descricao;
	private List<Permissao> permissoesSelecionadas;
	private List<Permissao> permissoesExcluidas;
	private Permissao permissao;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		nome=null;
		descricao=null;
	}
	

	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarPerfisCount(nome, descricao);
	}

	@Override
	public List<Perfil> recuperarListaPaginada(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarPerfis(nome, descricao, firstResult, maxResult,	asc);
	}	
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	
	
	public void limparPesquisa() {
		nome=null;
		descricao=null;
		this.dataModel.limparPesquisa();
	}		
	
	
	public String novo() {
		perfil = new Perfil();
		
		return PAGE_CADASTRAR_PERFIL;
	}	
	
	
	public String editar() {
		return PAGE_CADASTRAR_PERFIL;
	}
	
	
	public String cancelar() {
		permissao = null;
		return PAGE_PESQUISAR_PERFIL;
	}		
	

	public String associarPermissoesPerfil() {
		begin(conversation);
		
		permissoesSelecionadas = cascaFacade.obterPermissoesPorPerfil(perfil);
		permissoesExcluidas=new ArrayList<>();
		return PAGE_ASSOCIAR_PERMISSAO;
	}		
	
	
	public String historicoPerfil() {
		return PAGE_HISTORICO_PERFIL;
	}		
		
	
	/**
	 * Realiza a chamada para incluir/alterar de um determinado perfil.
	 */
	public String salvar() {
		try {
			boolean novo=perfil.getId()==null;
			
			cascaFacade.salvarPerfil(perfil);
			if (novo) {
				apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_CRIACAO_PERFIL");
			} else {
				apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_EDICAO_PERFIL");			
			}
			perfil = new Perfil();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISAR_PERFIL;	
	}

	
	public void excluir() {
		try {
			cascaFacade.excluirPerfil(perfil.getId());
			apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_EXCLUSAO_PERFIL");
			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		perfil = null;
	}

	public String salvarPermissoes() {
		try {
			cascaFacade.associarPermissoesPerfil(perfil, permissoesSelecionadas, permissoesExcluidas);
			permissoesExcluidas=new ArrayList<>();
			apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_ATUALIZACAO_PERFIL_PERMISSAO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISAR_PERFIL;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public List<Permissao> pesquisarPermissoesSuggestionBox(String element) {
		return this.returnSGWithCount(this.cascaFacade.pesquisarPermissoesSuggestionBox((String) element),pesquisarPermissoesCountSuggestionBox(element));
	}

	public Long pesquisarPermissoesCountSuggestionBox(String element) {
		return this.cascaFacade.pesquisarPermissoesCountSuggestionBox((String) element);
	}
	
	public void adicionar() {
		if (permissao==null) {
			apresentarMsgNegocio("CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO");
		}else{
			if (permissoesSelecionadas.contains(permissao)) {
				apresentarMsgNegocio("CASCA_MENSAGEM_PERMISSAO_EXISTENTE");
			}else{
				permissoesSelecionadas.add(permissao);
				permissao=null;
			}
		}
	}
	
	public void remover(Permissao permissao) {
		if (permissao.getId()!=null){
			permissoesExcluidas.add(permissao);
		}	
		permissoesSelecionadas.remove(permissao);
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Permissao> getPermissoesSelecionadas() {
		return permissoesSelecionadas;
	}

	public void setPermissoesSelecionadas(List<Permissao> permissoesSelecionadas) {
		this.permissoesSelecionadas = permissoesSelecionadas;
	}

	public Permissao getPermissao() {
		return permissao;
	}

	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}
	
}