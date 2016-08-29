package br.gov.mec.aghu.casca.usuario.action;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.model.UsuarioJn;
import br.gov.mec.aghu.casca.vo.FiltroUsuarioJnVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class HistoricoUsuarioPaginatorController extends ActionController
		implements ActionPaginator {
	private static final long serialVersionUID = 484829647024871557L;
	
	private final String REDIRECIONA_CADASTRO_USUARIO = "cadastrarUsuario";

	@EJB
	private ICascaFacade cascaFacade;

	private Usuario usuario;

	@Inject @Paginator
	private DynamicDataModel<UsuarioJn> dataModel;

	// Filtros
	private String login;
	private DominioOperacoesJournal operacao;
	private Date dataInicio;
	

	private Date dataFim;
	private String alteradoPor;

	public void inicio() {
		login = usuario.getLogin();
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return cascaFacade
				.pesquisarHistoricoPorUsuarioCount(new FiltroUsuarioJnVO(
						usuario.getId(), login, operacao, dataInicio, dataFim,
						alteradoPor));
	}

	@Override
	public List<UsuarioJn> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarHistoricoPorUsuario(firstResult, maxResult,
				orderProperty, asc, new FiltroUsuarioJnVO(usuario.getId(),
						login, operacao, dataInicio, dataFim, alteradoPor));
	}

	/**
	 * Realiza a chamada para a pesquisa.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true);
	}

	/**
	 * Limpa os filtros.
	 */
	public void limpar() {
		this.operacao = null;
		this.dataInicio = null;
		this.dataFim = null;
		this.alteradoPor = null;
		if (this.usuario == null) {
			this.login = null;
			this.dataModel.setPesquisaAtiva(false);
		} else {
			this.dataModel.reiniciarPaginator();
		}
	}
	
	public String voltar(){
		return REDIRECIONA_CADASTRO_USUARIO;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}	
	
	public DynamicDataModel<UsuarioJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<UsuarioJn> dataModel) {
		this.dataModel = dataModel;
	}
}
