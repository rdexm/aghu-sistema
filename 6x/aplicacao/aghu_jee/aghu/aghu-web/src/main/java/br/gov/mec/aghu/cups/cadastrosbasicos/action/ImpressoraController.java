package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.model.cups.ImpServidorCups;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsavel por controlar as acoes da tela de cadastro e edicao de
 * impressora
 * 
 * @author Heliz
 */



public class ImpressoraController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	//private static final Log LOG = LogFactory.getLog(ImpressoraController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 9182823624798228572L;

	private ImpImpressora impImpressora = new ImpImpressora();

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	@Inject
	private ImpressoraPaginatorController impressoraPaginatorController;

	private final String REDIRECIONA_CADASTRAR_IMPRESSORA = "cadastrarImpressora.xhtml";

	private final String REDIRECIONA_PESQUISAR_IMPRESSORA = "pesquisarImpressora.xhtml";

	private Integer idImpressora;

	// variavel para guardar o valor do tipoImpressora para validação quando for
	// alteração
	private DominioTipoImpressoraCups tipoImpressoraAnt;

	/**
	 * teste
	 * 
	 * @return
	 */
	public void pesquisar() {
		impressoraPaginatorController.getDataModel().reiniciarPaginator();
		impressoraPaginatorController.setImpImpressora(impImpressora);
		impressoraPaginatorController.setExibirBotaoIncluirImpressora(true);
		impressoraPaginatorController.getDataModel().setPesquisaAtiva(true);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void limparPesquisa() {
		impImpressora = new ImpImpressora();
		impressoraPaginatorController.setExibirBotaoIncluirImpressora(false);
		impressoraPaginatorController.getDataModel().setPesquisaAtiva(false);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String cadastrarNovo() {
		limpaImpressora();
		return REDIRECIONA_CADASTRAR_IMPRESSORA;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String editar() {
		try {

			impImpressora = cadastrosBasicosCupsFacade
					.obterImpressora(idImpressora);
			// guarda o valor do tipoImpressora para validação quando for
			// alteração
			setTipoImpressoraAnt(impImpressora.getTipoImpressora());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return REDIRECIONA_CADASTRAR_IMPRESSORA;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String gravar() {

		try {

			// impImpressora.setImpRedireciona(impressora);

			boolean indicaEdicao = impImpressora.getId() == null;

			cadastrosBasicosCupsFacade.gravarImpressora(impImpressora,
					tipoImpressoraAnt);

			if (indicaEdicao) {
				apresentarMsgNegocio(Severity.INFO,
						"IMPRESSORA_INCLUIDA_COM_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"IMPRESSORA_ALTERADA_COM_SUCESSO");
			}

			impImpressora = new ImpImpressora();
			impressoraPaginatorController.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_IMPRESSORA;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public void confirmarExclusao(Integer id) {
		try {
			cadastrosBasicosCupsFacade.excluirImpressora(id);
			apresentarMsgNegocio(Severity.INFO,
					"SUCESSO_EXCLUSAO_IMPRESSORA");
			impressoraPaginatorController.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		limpaImpressora();
	}

	/**
	 * Cancela uma determinada acao e retorna para a pagina de pesquisa.
	 * 
	 * @return
	 */
	public String cancelar() {
		limpaImpressora();
		impressoraPaginatorController.getDataModel().reiniciarPaginator();
		return REDIRECIONA_PESQUISAR_IMPRESSORA;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private void limpaImpressora() {
		impImpressora = new ImpImpressora();
		idImpressora = null;
	}

	// Get's and Set's
	public ImpressoraPaginatorController getImpressoraPaginatorController() {
		return impressoraPaginatorController;
	}

	public void setImpressoraPaginatorController(
			ImpressoraPaginatorController impressoraPaginatorController) {
		this.impressoraPaginatorController = impressoraPaginatorController;
	}

	public ImpImpressora getImpImpressora() {
		return impImpressora;
	}

	public void setImpImpressora(ImpImpressora impImpressora) {
		this.impImpressora = impImpressora;
	}

	public Integer getIdImpressora() {
		return idImpressora;
	}

	public void setIdImpressora(Integer idImpressora) {
		this.idImpressora = idImpressora;
	}

	public List<ImpImpressora> pesquisarImpressoras(Object parametro) {
		return cadastrosBasicosCupsFacade.pesquisarImpressoraPorFila(parametro);
	}

	public List<ImpServidorCups> pesquisarServidorCups(String paramPesquisa) {
		return cadastrosBasicosCupsFacade.pesquisarServidorCups(paramPesquisa);
	}

	/**
	 * Metodo que verifica se deve sugerir uma impressora de acordo com o tipo
	 * de impressora selecionado
	 */

	public List<ImpImpressora> sugerirImpressora(String object) {
		String strPesquisa = (String) object;
		List<ImpImpressora> lista = null; //

		if (impImpressora.getTipoImpressora() != null) {//
			lista = this.cadastrosBasicosCupsFacade
					.obterImpressoraRedirecionamento(impImpressora, strPesquisa);//
		}

		return lista;
	}

	public DominioTipoImpressoraCups getTipoImpressoraAnt() {
		return tipoImpressoraAnt;
	}

	public void setTipoImpressoraAnt(DominioTipoImpressoraCups tipoImpressoraAnt) {
		this.tipoImpressoraAnt = tipoImpressoraAnt;
	}
}
