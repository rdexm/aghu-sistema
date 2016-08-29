package br.gov.mec.aghu.casca.perfilusuario.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.PerfisUsuariosJn;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.vo.FiltroPerfisUsuariosJnVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class HistoricoPerfisUsuariosPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 484829647024871557L;

	private final String REDIRECIONA_CADASTRAR_PERFIL_USUARIO = "manterPerfilUsuario";	
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<PerfisUsuariosJn> dataModel;

	//Filtros
	private Usuario usuario;	
	private String login;
	private String nomePerfil;
	private DominioOperacoesJournal operacao;
	private Date dataInicio;
	private Date dataFim;	
	private String alteradoPor;

	@PostConstruct
	public void init() {
		begin(conversation);
		if (usuario == null) {
			setUsuario(cascaFacade.obterUsuario(obterLoginUsuarioLogado()));
		}
	}

	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarHistoricoPorPerfisUsuariosCount(
				new FiltroPerfisUsuariosJnVO(usuario.getId(), login, null, nomePerfil, operacao, dataInicio, dataFim, alteradoPor));
	}

	@Override
	public List<PerfisUsuariosJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarHistoricoPorPerfisUsuarios(firstResult,	maxResult, orderProperty, asc, 
				new FiltroPerfisUsuariosJnVO(usuario.getId(), login, null, nomePerfil, operacao, dataInicio, dataFim, alteradoPor));
	}
	
	/**
	 * Realiza a chamada para a pesquisa.
	 */
	public void pesquisar() {
	 

	 

		dataModel.reiniciarPaginator();
	
	}
	
	
	/**
	 * Limpa os filtros.
	 */
	public void limpar() {
		this.operacao = null;
		this.dataInicio = null;
		this.dataFim = null;
		this.alteradoPor = null;
		this.nomePerfil = null;
	}

	
	public String voltar(){
		return REDIRECIONA_CADASTRAR_PERFIL_USUARIO;
	}
	
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNomePerfil() {
		return nomePerfil;
	}

	public void setNomePerfil(String nomePerfil) {
		this.nomePerfil = nomePerfil;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	public DynamicDataModel<PerfisUsuariosJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PerfisUsuariosJn> dataModel) {
		this.dataModel = dataModel;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.login=usuario.getLogin();
		this.usuario = usuario;
	}
}