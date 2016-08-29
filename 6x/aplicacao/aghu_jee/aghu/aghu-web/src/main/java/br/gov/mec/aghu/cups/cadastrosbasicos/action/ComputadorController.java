package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da tela de cadastro e edição de
 * computador
 * 
 * @author Lilian
 */


public class ComputadorController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	//private static final Log LOG = LogFactory.getLog(ComputadorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2835498438142979768L;

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private ImpComputador impComputador = new ImpComputador();

	private Integer idComputador;

	@Inject
	private ComputadorPaginatorController computadorPaginatorController = new ComputadorPaginatorController();

	private final String REDIRECIONA_CADASTRAR_COMPUTADOR = "cadastrarComputador.xhtml";

	private final String REDIRECIONA_PESQUISAR_COMPUTADOR = "pesquisarComputador.xhtml";

	/**
	 * Cancela uma determinada acao e retorna para a pagina de pesquisa.
	 * 
	 * @return
	 */
	public void pesquisar() {
		computadorPaginatorController.getDataModel().reiniciarPaginator();
		computadorPaginatorController.setImpComputador(impComputador);
		computadorPaginatorController.setExibirBotaoIncluirComputador(true);
		computadorPaginatorController.getDataModel().setPesquisaAtiva(true);
	}

	/**
	 * Limpar pesquisa.
	 */
	public void limparPesquisa() {
		impComputador = new ImpComputador();
		computadorPaginatorController.setExibirBotaoIncluirComputador(false);
		computadorPaginatorController.getDataModel().setPesquisaAtiva(false);
	}

	/**
	 * Cadastrar novo.
	 * 
	 * @return
	 */
	public String cadastrarNovo() {
		limpaComputador();
		computadorPaginatorController.setExibirBotaoIncluirComputador(false);
		return REDIRECIONA_CADASTRAR_COMPUTADOR;
	}

	/**
	 * Editar item.
	 * 
	 * @return
	 */
	public String editar() {
		try {
			impComputador = cadastrosBasicosCupsFacade
					.obterComputador(idComputador);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return null;
		}
		return REDIRECIONA_CADASTRAR_COMPUTADOR;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String gravar() {

		try {
			// Validando IP.
			// TODO: Pode-se melhorar, criando Validator e Converter.
			cadastrosBasicosCupsFacade.validarIp(impComputador
					.getIpComputador());

			boolean indicaEdicao = impComputador.getSeq() != null;

			cadastrosBasicosCupsFacade.gravarComputador(impComputador);

			if (indicaEdicao) {
				apresentarMsgNegocio(Severity.INFO,
						"COMPUTADOR_ALTERADO_COM_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"COMPUTADOR_INCLUIDO_COM_SUCESSO");
			}

			impComputador = new ImpComputador();
			computadorPaginatorController.getDataModel().reiniciarPaginator();
			computadorPaginatorController
					.setExibirBotaoIncluirComputador(false);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_COMPUTADOR;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void confirmarExclusao(Integer seq) {
		try {
			cadastrosBasicosCupsFacade.excluirComputador(seq);
			apresentarMsgNegocio(Severity.INFO,
					"SUCESSO_EXCLUSAO_COMPUTADOR");
			computadorPaginatorController.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		limpaComputador();
	}

	/**
	 * Cancela uma determinada acao e retorna para a pagina de pesquisa.
	 * 
	 * @return
	 */
	//@End
	public String cancelar() {
		computadorPaginatorController.setExibirBotaoIncluirComputador(true);
		limpaComputador();
		return REDIRECIONA_PESQUISAR_COMPUTADOR;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private void limpaComputador() {
		impComputador = new ImpComputador();
		idComputador = null;
	}

	// Get's and Set's
	// ===============
	public ComputadorPaginatorController getComputadorPaginatorController() {
		return computadorPaginatorController;
	}

	public void setComputadorPaginatorController(
			ComputadorPaginatorController computadorPaginatorController) {
		this.computadorPaginatorController = computadorPaginatorController;
	}

	public ImpComputador getImpComputador() {
		return impComputador;
	}

	public void setImpComputador(ImpComputador impComputador) {
		this.impComputador = impComputador;
	}

	public Integer getIdComputador() {
		return idComputador;
	}

	public void setIdComputador(Integer idComputador) {
		this.idComputador = idComputador;
	}

}
