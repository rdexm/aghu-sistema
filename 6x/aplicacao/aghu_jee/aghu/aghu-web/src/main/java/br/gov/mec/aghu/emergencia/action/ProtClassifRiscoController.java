package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de protocolos e classificação de risco
 * 
 * @author luismoura
 * 
 */
public class ProtClassifRiscoController extends ActionController {
	private static final long serialVersionUID = 5696509444357451854L;

	// ----- PAGINAS
	private final String PAGE_FLX_PROT_CLASSIF_RISCO = "fluxogramaProtClassifRiscoCRUD";

	// ----- FAÇADES
	@EJB
	private IEmergenciaFacade emergenciaFacade;

	// ----- FILTRO
	private String descricao;
	private DominioSituacao indSituacao;

	// ----- LISTAGEM
	private List<MamProtClassifRisco> dataModel = new ArrayList<MamProtClassifRisco>();
	private boolean pesquisaAtiva;

	// ----- EDIÇÃO
	private MamProtClassifRisco mamProtClassifRisco;

	// ----- NOVO
	private String cadDescricao;
	private Boolean cadIndSituacao = true;
	private Boolean cadIndPermiteChecagem = false;

	// ----- CONTOLE DE TELA
	private boolean permissaoManter = false;
	
	//selection
	private MamProtClassifRisco selecao;


	@PostConstruct
	public void init() {
		begin(conversation);
		this.permissaoManter = this.usuarioTemPermissao("manterProtocolosClassificacaoRisco", "gravar");
	}

	/**
	 * Ação do botão PESQUISAR
	 */
	public void pesquisar() {
		this.pesquisaAtiva = true;
		this.dataModel = emergenciaFacade.pesquisarProtClassifRisco(descricao, indSituacao);
		this.mamProtClassifRisco = null;
	}

	/**
	 * Ação do botão LIMPAR
	 */
	public void limparPesquisa() {
		this.pesquisaAtiva = false;
		this.descricao = null;
		this.indSituacao = null;
		this.dataModel.clear();
		this.mamProtClassifRisco = null;
		this.selecao = null;
		this.limparNovo();
	}

	/**
	 * Ação do segundo botão ATIVAR/INATIVAR
	 */
	public void ativarInativarProtClassifRisco() {
		try {
			if (mamProtClassifRisco != null) {

				// Chama RN02 para ativar/desativar
				this.emergenciaFacade.ativarInativarMamProtClassifRisco(mamProtClassifRisco);

				// Refaz a pesquisa
				this.pesquisar();

				// Apresenta mensagem de sucesso
				super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATIVAR_DESATIVAR_PROTOCOLO_RISCO");
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão ATIVAR/INATIVAR
	 */
	public void permitirBloquearChecagemProtClassifRisco() {
		try {
			if (mamProtClassifRisco != null) {

				// Chama RN02 para ativar/desativar
				this.emergenciaFacade.permitirBloquearChecagemMamProtClassifRisco(mamProtClassifRisco);

				// Refaz a pesquisa
				this.pesquisar();

				// Apresenta mensagem de sucesso
				super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CHECAGEM");
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão FLUXOGRAMA
	 * 
	 * @return
	 */
	public String irFluxograma() {
		return PAGE_FLX_PROT_CLASSIF_RISCO;
	}

	/**
	 * Ação do botão EXCLUIR
	 */
	public void excluir() {
		try {
			if (mamProtClassifRisco != null) {

				// Chama RN04 para excluir
				this.emergenciaFacade.excluirMamProtClassifRisco(mamProtClassifRisco);

				// Refaz a pesquisa
				this.pesquisar();

				// Apresenta mensagem de sucesso
				super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PROTOCOLO");
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão ADICIONAR
	 */
	public void adicionarProtClassifRisco() {
		try {
			// Monta o objeto
			mamProtClassifRisco = new MamProtClassifRisco();
			mamProtClassifRisco.setDescricao(this.cadDescricao);
			mamProtClassifRisco.setIndSituacao(DominioSituacao.getInstance(this.getCadIndSituacao()));
			mamProtClassifRisco.setIndPermiteChecagem(DominioSituacao.getInstance(this.getCadIndPermiteChecagem()));
			mamProtClassifRisco.setIndBloqueado(DominioSituacao.I);

			// Chama RN01 para persistir
			this.emergenciaFacade.persistirMamProtClassifRisco(mamProtClassifRisco);

			// Limpa os campos
			this.limparNovo();

			// Refaz a pesquisa
			this.pesquisar();

			// Apresenta mensagem de sucesso
			super.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_PROTOCOLO_RISCO");

		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Limpa os campos de 'novo registro'
	 */
	private void limparNovo() {
		this.mamProtClassifRisco = null;
		this.cadDescricao = null;
		this.cadIndSituacao = true;
		this.cadIndPermiteChecagem = false;

	}

	/**
	 * Cria um Boolean à partir de um DominioSituacao
	 * 
	 * @param dominioSituacao
	 * @return
	 */
	public Boolean getBoolDominioSituacao(DominioSituacao dominioSituacao) {
		return dominioSituacao != null && dominioSituacao.equals(DominioSituacao.A);
	}

	public boolean usuarioTemPermissao(String componente, String metodo) {
		return getPermissionService().usuarioTemPermissao(this.obterLoginUsuarioLogado(), componente, metodo);
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	// ----- GETS e SETS

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public List<MamProtClassifRisco> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<MamProtClassifRisco> dataModel) {
		this.dataModel = dataModel;
	}

	public MamProtClassifRisco getMamProtClassifRisco() {
		return mamProtClassifRisco;
	}

	public void setMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) {
		this.mamProtClassifRisco = mamProtClassifRisco;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public String getCadDescricao() {
		return cadDescricao;
	}

	public void setCadDescricao(String cadDescricao) {
		this.cadDescricao = cadDescricao;
	}

	public Boolean getCadIndSituacao() {
		return cadIndSituacao;
	}

	public void setCadIndSituacao(Boolean cadIndSituacao) {
		this.cadIndSituacao = cadIndSituacao;
	}

	public Boolean getCadIndPermiteChecagem() {
		return cadIndPermiteChecagem;
	}

	public void setCadIndPermiteChecagem(Boolean cadIndPermiteChecagem) {
		this.cadIndPermiteChecagem = cadIndPermiteChecagem;
	}

	public boolean isPermissaoManter() {
		return permissaoManter;
	}

	public void setPermissaoManter(boolean permissaoManter) {
		this.permissaoManter = permissaoManter;
	}

	public MamProtClassifRisco getSelecao() {
		return selecao;
	}

	public void setSelecao(MamProtClassifRisco selecao) {
		this.selecao = selecao;
	}
}
