package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.ExameCancelarExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class PesquisaCancelarExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -6908464462471282843L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController;

	/* fitro da tela de pesquisa */
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AghUnidadesFuncionais unidadeExecutora;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 

		limparPesquisa();
		unidadeExecutora = unidadeExecutoraController.getUnidadeExecutora();

		// Obtem o USUARIO da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}

	
	}

	public String pesquisar() {
		String retorno = "exames-cancelarExamesAreaExecutora";
		try {
			this.filtro.setAelUnfExecutoraInfo(unidadeExecutora);

			List<PesquisaExamesPacientesResultsVO> examesCancelar = pesquisaExamesFacade.buscaDadosItensSolicitacaoPorSoeSeq(this.filtro.getNumeroSolicitacaoInfo(), this.unidadeExecutora.getSeq());

			if (examesCancelar == null || examesCancelar.size() == 0) {
				throw new ApplicationBusinessException(ExameCancelarExceptionCode.AEL_01195);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			retorno = null;
		}
		return retorno;
	}

	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de exames.
	 */
	public void limparPesquisa() {
		setFiltro(new PesquisaExamesFiltroVO());
		setUnidadeExecutora(null);
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {

			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Metodo para limpeza da suggestion box de unidade executora
	 */
	public void limparAghUnidadesFuncionaisExecutoras() {
		this.unidadeExecutora = null;
	}

	// Metódo para Suggestion Box Unidade executora
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String param) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(param);
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public IdentificarUnidadeExecutoraController getUnidadeExecutoraController() {
		return unidadeExecutoraController;
	}

	public void setUnidadeExecutoraController(IdentificarUnidadeExecutoraController unidadeExecutoraController) {
		this.unidadeExecutoraController = unidadeExecutoraController;
	}
}