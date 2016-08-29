package br.gov.mec.aghu.casca.usuario.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


public class UsuarioController extends ActionController {

	private static final long serialVersionUID = 5453579500936197327L;

	private static final String IMPORTAR_USUARIO = "importarUsuario";
	private static final String REDIRECIONA_PESQUISAR_USUARIO = "pesquisarUsuarios";
	private static final String REDIRECIONA_HISTORICO_USUARIO = "historicoUsuario";
	private static final String REDIRECIONA_HISTORICO_PERFIL_USUARIO = "historicoPerfisUsuarios";
	

	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private HistoricoUsuarioPaginatorController historicoUsuarioPaginatorController;
	
	private Usuario usuario;
	private boolean perfilUsuarioLogado;

	/** Lista de todos os perfis */
	private List<PerfisUsuarios> perfisSelecionados;	
	private Perfil perfil;
	private boolean importandoUsuario;
	
	public void iniciarTelaPerfil(){
	 

	 

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado(); 
		perfilUsuarioLogado = servidorLogado.getUsuario().equalsIgnoreCase(usuario.getLogin());
	
	}
	
	


	/**
	 * Realiza a chamada para incluir/alterar um usuario.
	 */
	public String salvar() {
		try {
			boolean indicaEdicao = usuario.getId() != null;
			cascaFacade.salvarUsuario(usuario);

			if (indicaEdicao) {
				this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_EDICAO_USUARIO");			
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_CRIACAO_USUARIO");			
			}
			usuario = new Usuario();
			this.carregaTempoSessaoDefault(usuario);
			this.setImportandoUsuario(false);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return REDIRECIONA_PESQUISAR_USUARIO;
	}

	
	
	public void incluirUsuario(Usuario usuario) {
		this.usuario = usuario;
		salvar();
	}

	public void iniciarInclusaoUsuarioImportado(){
		this.carregaTempoSessaoDefault(usuario);
		this.setImportandoUsuario(true);
	}
	/**
	 * Cancela uma determinada acao, e retorna para a pagina de pesquisa.
	 */
	public String cancelar() {
		perfilUsuarioLogado = false;
		perfisSelecionados = null;
		this.perfil = null;
		usuario = new Usuario();
		this.carregaTempoSessaoDefault(usuario);
		if(importandoUsuario){
			this.setImportandoUsuario(false);
			return IMPORTAR_USUARIO;
					
		} else {
			return REDIRECIONA_PESQUISAR_USUARIO;
		}
	}
	/**
	 * Cancela uma determinada acao, e retorna para a pagina de pesquisa.
	 */
	public String cancelarImportacao() {
		usuario = new Usuario();
		this.carregaTempoSessaoDefault(usuario);
		perfisSelecionados = null;
		this.perfil = null;
		return "retornarPesquisaUsuarios";
	}

	public String salvarPerfis() {
		try {
			cascaFacade.associarPerfilUsuario(usuario.getId(), perfisSelecionados);			
			this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_ATUALIZACAO_PERFIL_USUARIO");
		
			if(perfilUsuarioLogado){
				openDialog("modalConfSalvarPerfisUserLogadoWG");
				return null;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cancelar();
	}
	
	public String voltar(){
		return REDIRECIONA_PESQUISAR_USUARIO;
	}

	public List<Perfil> getListaPerfis() {
		return cascaFacade.pesquisarPerfis((String) null);		
	}

	public List<Perfil> pesquisarPerfisSuggestionBox(String stringPesquisa) {
		try {
			return this.returnSGWithCount(cascaFacade.pesquisarPerfisSuggestionBox((String) stringPesquisa),pesquisarPerfilCountSuggestionBox(stringPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public Long pesquisarPerfilCountSuggestionBox(String stringPesquisa) {
		return cascaFacade.pesquisarPerfilCountSuggestionBox((String) stringPesquisa);
	}

	public void adicionarPerfilUsuario() {
		if (getPerfil()==null) {			
			this.apresentarMsgNegocio(Severity.ERROR,"CASCA_MENSAGEM_PERFIL_NAO_INFORMADO");
			
		}else{
			boolean repetido = false;
			for (PerfisUsuarios perfilUsuario : getPerfisSelecionados()){
				if (perfilUsuario.getPerfil().equals(getPerfil())){
					repetido = true;
					break;
				}
			}			
			if (repetido) {
				this.apresentarMsgNegocio(Severity.ERROR,"CASCA_MENSAGEM_PERFIL_EXISTENTE");
			}else{
				PerfisUsuarios perfilUsuario = new PerfisUsuarios();
				perfilUsuario.setDataCriacao(new Date());
				perfilUsuario.setUsuario(usuario);
				perfilUsuario.setPerfil(getPerfil());				
				getPerfisSelecionados().add(perfilUsuario);
				setPerfil(null);
			}
		}
	}

	public List<PerfisUsuarios> getPerfisSelecionados() {
		if (this.perfisSelecionados == null) {
			this.perfisSelecionados = new ArrayList<PerfisUsuarios>();
		}
		return this.perfisSelecionados;
	}
	
	/**
	 * Carrega o tempoSessaoMinutos com valor do parâmetro
	 */
	private void carregaTempoSessaoDefault(Usuario usuario){
		if(usuario != null && usuario.getTempoSessaoMinutos() == null){
			Integer tempoSessao = 30;
			AghParametros parametroTempoSessao = parametroFacade.getAghParametro(AghuParametrosEnum.P_AGHU_TEMPO_SESSAO_MINUTOS);
			if(parametroTempoSessao != null && parametroTempoSessao.getVlrNumerico() != null){
				tempoSessao = parametroTempoSessao.getVlrNumerico().intValue();
			}
			usuario.setTempoSessaoMinutos(tempoSessao);
		}
	}

	public void setPerfisSelecionados(List<PerfisUsuarios> perfisSelecionados) {
		this.perfisSelecionados = perfisSelecionados;
	}

	public void remover(PerfisUsuarios perfilUsuario) {
		getPerfisSelecionados().remove(perfilUsuario);		
	}

	public String historicoUsuario(){
		this.historicoUsuarioPaginatorController.setUsuario(usuario);
		this.historicoUsuarioPaginatorController.inicio();
		return REDIRECIONA_HISTORICO_USUARIO;
	}
	
	public String historicoPerfisUsuarios(){
		return REDIRECIONA_HISTORICO_PERFIL_USUARIO;
	}

	public void carregarPerfisSelecionados() {
		List<PerfisUsuarios> perfisUsuario2 = cascaFacade.listarPerfisPorUsuario(usuario);		
		
		for (PerfisUsuarios perfisUsuarios : perfisUsuario2) {
			if (!getPerfisSelecionados().contains(perfisUsuarios)) {
				getPerfisSelecionados().add(perfisUsuarios);
			}
		}
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isPerfilUsuarioLogado() {
		return perfilUsuarioLogado;
	}

	public void setPerfilUsuarioLogado(boolean perfilUsuarioLogado) {
		this.perfilUsuarioLogado = perfilUsuarioLogado;
	}

	public boolean isImportandoUsuario() {
		return importandoUsuario;
	}

	public void setImportandoUsuario(boolean importandoUsuario) {
		this.importandoUsuario = importandoUsuario;
	} 
}
