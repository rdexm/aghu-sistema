package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class ClasseImpressaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	//private static final Log LOG = LogFactory.getLog(ClasseImpressaoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2957465220213394287L;

	private ImpClasseImpressao impClasseImpressao = new ImpClasseImpressao();

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	@Inject
	private ClasseImpressaoPaginatorController classeImpressaoPaginatorController;

	private final String REDIRECIONA_CADASTRAR_CLASSE_IMPRESSAO = "cadastrarClasseImpressao.xhtml";

	private final String REDIRECIONA_PESQUISAR_CLASSE_IMPRESSAO = "pesquisarClasseImpressao.xhtml";

	private Integer idClasseImpressao;

	/**
	 * teste
	 * 
	 * @return
	 */
	public void pesquisar() {
		classeImpressaoPaginatorController.getDataModel().reiniciarPaginator();
		classeImpressaoPaginatorController
				.setImpClasseImpressao(impClasseImpressao);
		classeImpressaoPaginatorController
				.setExibirBotaoIncluirClasseImpressao(true);
		classeImpressaoPaginatorController.getDataModel().setPesquisaAtiva(true);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void limparPesquisa() {
		impClasseImpressao = new ImpClasseImpressao();
		classeImpressaoPaginatorController
				.setExibirBotaoIncluirClasseImpressao(false);
		classeImpressaoPaginatorController.getDataModel().setPesquisaAtiva(false);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String cadastrarNovo() {
		limpaClasseImpressao();
		classeImpressaoPaginatorController
				.setExibirBotaoIncluirClasseImpressao(true);
		return REDIRECIONA_CADASTRAR_CLASSE_IMPRESSAO;
	}

	public String editar() {
		try {
			impClasseImpressao = cadastrosBasicosCupsFacade
					.obterClasseImpressao(idClasseImpressao);
		} catch (ApplicationBusinessException e) {
			//apresentarExcecaoNegocio(e);
			this.apresentarMsgNegocio(Severity.ERROR, "CLASSE_IMPRESSAO_EXCLUIDA");
			return null;
		}
		return REDIRECIONA_CADASTRAR_CLASSE_IMPRESSAO;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String gravar() {

		try {

			boolean indicaEdicao = impClasseImpressao.getId() != null;

			cadastrosBasicosCupsFacade
					.gravarClasseImpressao(impClasseImpressao);

			if (indicaEdicao) {
				apresentarMsgNegocio(Severity.INFO,
						"CLASSE_IMPRESSAO_ALTERADA_COM_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"CLASSE_IMPRESSAO_INCLUIDO_COM_SUCESSO");
			}

			impClasseImpressao = new ImpClasseImpressao();
			classeImpressaoPaginatorController.getDataModel().reiniciarPaginator();
			classeImpressaoPaginatorController
					.setExibirBotaoIncluirClasseImpressao(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_CLASSE_IMPRESSAO;

	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void confirmarExclusao(Integer id) {
		try {
			cadastrosBasicosCupsFacade
					.excluirClasseImpressao(id);
			apresentarMsgNegocio(Severity.INFO,
					"SUCESSO_EXCLUSAO_CLASSE_IMPRESSAO");
			classeImpressaoPaginatorController.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		limpaClasseImpressao();
	}

	/**
	 * Cancela uma determinada acao e retorna para a pagina de pesquisa.
	 * 
	 * @return
	 */
	//@End
	public String cancelar() {
		limpaClasseImpressao();
		return REDIRECIONA_PESQUISAR_CLASSE_IMPRESSAO;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private void limpaClasseImpressao() {
		impClasseImpressao = new ImpClasseImpressao();
		idClasseImpressao = null;
	}

	// Get's and Set's
	// ===============
	public ClasseImpressaoPaginatorController getClasseImpressaoPaginatorController() {
		return classeImpressaoPaginatorController;
	}

	public void setClasseImpressaoPaginatorController(
			ClasseImpressaoPaginatorController classeImpressaoPaginatorController) {
		this.classeImpressaoPaginatorController = classeImpressaoPaginatorController;
	}

	public ImpClasseImpressao getImpClasseImpressao() {
		return impClasseImpressao;
	}

	public void setImpClasseImpressao(ImpClasseImpressao impClasseImpressao) {
		this.impClasseImpressao = impClasseImpressao;
	}

	public Integer getIdClasseImpressao() {
		return idClasseImpressao;
	}

	public void setIdClasseImpressao(Integer idClasseImpressao) {
		this.idClasseImpressao = idClasseImpressao;
	}

}
