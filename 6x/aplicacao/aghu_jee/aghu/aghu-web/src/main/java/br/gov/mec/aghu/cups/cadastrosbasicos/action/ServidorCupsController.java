package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpServidorCups;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da tela de cadastro e edição de
 * servidor cups
 * 
 * @author Heliz
 */


public class ServidorCupsController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	//private static final Log LOG = LogFactory.getLog(ServidorCupsController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8289770675610523295L;

	private ImpServidorCups impServidorCups = new ImpServidorCups();

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	public ImpServidorCups getImpServidorCups() {
		return impServidorCups;
	}

	public void setImpServidorCups(ImpServidorCups impServidorCups) {
		this.impServidorCups = impServidorCups;
	}

	@Inject
	private ServidorCupsPaginatorController servidorCupsPaginatorController = new ServidorCupsPaginatorController();

	private final String REDIRECIONA_CADASTRAR_SERVIDOR_CUPS = "cadastrarServidorCups.xhtml";

	private final String REDIRECIONA_PESQUISAR_SERVIDOR_CUPS = "pesquisarServidorCups.xhtml";

	private Integer idServidorCups;

	/**
	 * @return
	 */
	public void pesquisar() {
		servidorCupsPaginatorController.getDataModel().reiniciarPaginator();
		servidorCupsPaginatorController.setImpServidorCups(impServidorCups);
		servidorCupsPaginatorController.setExibirBotaoIncluirServidorCups(true);
		servidorCupsPaginatorController.getDataModel().setPesquisaAtiva(true);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void limparPesquisa() {
		impServidorCups = new ImpServidorCups();
		servidorCupsPaginatorController
				.setExibirBotaoIncluirServidorCups(false);
		servidorCupsPaginatorController.getDataModel().setPesquisaAtiva(false);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String cadastrarNovo() {
		limpaServidorCups();
		// servidorCupsPaginatorController.setExibirBotaoIncluirServidorCups(false);
		return REDIRECIONA_CADASTRAR_SERVIDOR_CUPS;
	}

	public String editar() {
		try {
			impServidorCups = cadastrosBasicosCupsFacade
					.obterServidorCups(idServidorCups);
		} catch (ApplicationBusinessException e) {
			//apresentarExcecaoNegocio(e);
			this.apresentarMsgNegocio(Severity.ERROR, "SERVIDOR_CUPS_EXCLUIDO");
			return null;
		}
		return REDIRECIONA_CADASTRAR_SERVIDOR_CUPS;
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
			cadastrosBasicosCupsFacade.validarIp(impServidorCups
					.getIpServidor());

			boolean indicaEdicao = impServidorCups.getId() != null;

			cadastrosBasicosCupsFacade.gravarServidorCups(impServidorCups);

			if (indicaEdicao) {
				apresentarMsgNegocio(Severity.INFO,
						"SERVIDOR_CUPS_ALTERADO_COM_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"SERVIDOR_CUPS_INCLUIDO_COM_SUCESSO");
			}

			impServidorCups = new ImpServidorCups();
			servidorCupsPaginatorController.getDataModel().reiniciarPaginator();
			servidorCupsPaginatorController
					.setExibirBotaoIncluirServidorCups(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_SERVIDOR_CUPS;

	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void confirmarExclusao(Integer id) {
		try {
			cadastrosBasicosCupsFacade.excluirServidorCups(id);
			apresentarMsgNegocio(Severity.INFO,
					"SUCESSO_EXCLUSAO_SERVIDOR_CUPS");
			servidorCupsPaginatorController.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		limpaServidorCups();
	}

	/**
	 * Cancela uma determinada acao e retorna para a pagina de pesquisa.
	 * 
	 * @return
	 */
	//@End
	public String cancelar() {
		limpaServidorCups();
		servidorCupsPaginatorController.getDataModel().reiniciarPaginator();
		return REDIRECIONA_PESQUISAR_SERVIDOR_CUPS;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private void limpaServidorCups() {
		impServidorCups = new ImpServidorCups();
		idServidorCups = null;
	}

	// Get's and Set's
	public Integer getIdServidorCups() {
		return idServidorCups;
	}

	public void setIdServidorCups(Integer idServidorCups) {
		this.idServidorCups = idServidorCups;
	}

	public ServidorCupsPaginatorController getServidorCupsPaginatorController() {
		return servidorCupsPaginatorController;
	}

	public void setServidorCupsPaginatorController(
			ServidorCupsPaginatorController servidorCupsPaginatorController) {
		this.servidorCupsPaginatorController = servidorCupsPaginatorController;
	}

}
