package br.gov.mec.aghu.compras.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class BancoController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -8526725440449647355L;
	
	// Logger
	private static final Log LOG = LogFactory.getLog(BancoController.class);
	// Constante de página cadastro de banco
	private static final String PAGE_PESQUISAR_BANCO = "pesquisarBanco";	
	// Constante de página manter cadastro banco
	private static final String PAGE_MANTER_BANCO = "manterBanco";	
	
	// Serviço
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	// Modelo Consulta
	private FcpBanco fcpBancoVO = new FcpBanco();
	// Modelo Operacional
	private FcpBanco fcpBancoManutencaoVO = new FcpBanco();
	
	// Controle de inclusão ou alteração de banco
	private boolean alteracao;
	
	// Lista de consulta
	@Inject @Paginator
	private DynamicDataModel<FcpBanco> dataModel;
	
	// Listar as permissões de usuário
	@EJB
	private IPermissionService permissionService;
	
	//Exibição do botão NOVO
	private boolean exibirBotaoNovo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);	
		fcpBancoManutencaoVO = new FcpBanco();
		
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastrarBanco", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
		
		exibirBotaoNovo = false;
	}
	
	/**
	 * Exibir a página de pesquisa de bancos.
	 * @return
	 */
	public String iniciar() {
		return PAGE_PESQUISAR_BANCO;
	}
	
	/**
	 * Mensagems do sistema
	 */		
	private enum BancoMessages implements BusinessExceptionCode {
		BANCO_INCLUIDO_COM_SUCESSO,
		BANCO_ALTERADO_COM_SUCESSO,
		BANCO_EXCLUIDO_COM_SUCESSO,
		BANCO_COM_ESTE_CODIGO_JA_CADASTRADO,
		NAO_E_POSSIVEL_EXCLUIR_BANCO_COM_AGENCIAS_BANCARIAS_ASSOCIADAS,
		ERRO_INSERCAO,
		ERRO_EXCLUSAO,
		ERRO_ALTERACAO,
		ERRO_PESQUISA;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		
		exibirBotaoNovo = true;
	}
	
	/**
	 * Método responsável por pesquisar a lista de bancos
	 * @return lista de bancos
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FcpBanco> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<FcpBanco> fcpBanco = new ArrayList<FcpBanco>();
		try {
			fcpBanco = this.comprasCadastrosBasicosFacade.pesquisarListaBanco(firstResult, maxResult, orderProperty, asc, this.fcpBancoVO);	
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
			apresentarMsgNegocio(Severity.INFO, BancoMessages.ERRO_PESQUISA.toString());
		}
		return fcpBanco;
	}

	/**
	 * Método responsável por recuperar a quantidade de bancos
	 * @return quantidade de bancos
	 */
	@Override
	public Long recuperarCount() {
		Long countFcpBanco = null;
		try {
			countFcpBanco = this.comprasCadastrosBasicosFacade.pesquisarCountListaBanco(this.fcpBancoVO);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
			apresentarMsgNegocio(Severity.INFO, BancoMessages.ERRO_PESQUISA.toString());
		}
		return countFcpBanco;
	}
	
	/**
	  * Método responsável por inserir ou atualizar banco
	  * @return
	  * @throws ApplicationBusinessException
	  */
	public String gravar() throws BaseException {
		try {
			if (this.alteracao) {
				// Alterar código de recolhimento
				this.comprasCadastrosBasicosFacade.atualizarBanco(this.fcpBancoManutencaoVO);
				// Sucesso
				apresentarMsgNegocio(Severity.INFO, BancoMessages.BANCO_ALTERADO_COM_SUCESSO.toString());
			} else {
				// Inserir banco
				boolean retorno = this.comprasCadastrosBasicosFacade.inserirBanco(this.fcpBancoManutencaoVO);
				if(retorno) {
					// Sucesso
					apresentarMsgNegocio(Severity.INFO, BancoMessages.BANCO_INCLUIDO_COM_SUCESSO.toString());
				} else {
					// Registro duplicado
					apresentarMsgNegocio(Severity.WARN, BancoMessages.BANCO_COM_ESTE_CODIGO_JA_CADASTRADO.toString());
				}				
			}
		} catch (ApplicationBusinessException e) {		
			LOG.error("Exceção capturada ao inserir ou atualizar: ", e);
			apresentarMsgNegocio(Severity.ERROR, (alteracao ? BancoMessages.ERRO_ALTERACAO.toString() : BancoMessages.ERRO_INSERCAO.toString()));
		}	
		return PAGE_PESQUISAR_BANCO;
	}
	
	/**
	 * Método responsável por remover banco
	 * @param codigoBanco
	 * @throws ApplicationBusinessException
	 */
	public void remover() throws BaseException {	
		try { 
			// Excluir Banco
			boolean retorno = this.comprasCadastrosBasicosFacade.excluirBanco(this.fcpBancoManutencaoVO);	
			if(retorno) {
				// Sucesso
				apresentarMsgNegocio(Severity.INFO, BancoMessages.BANCO_EXCLUIDO_COM_SUCESSO.toString());
			} else {
				// Possui dependência
				apresentarMsgNegocio(Severity.WARN, BancoMessages.NAO_E_POSSIVEL_EXCLUIR_BANCO_COM_AGENCIAS_BANCARIAS_ASSOCIADAS.toString());
			}	
		} catch (ApplicationBusinessException e) {		
			LOG.error("Exceção capturada ao remover: ", e);
			apresentarMsgNegocio(Severity.ERROR, BancoMessages.ERRO_EXCLUSAO.toString());
		}			
	}
	
	/**
	 * @return Página de inserção de banco.
	 */
	public String cadastrar() {
		this.alteracao = false;
		this.fcpBancoManutencaoVO = new FcpBanco();
		return PAGE_MANTER_BANCO;
	}
	
	/**
	 * @return Página de alteração de banco.
	 */
	public String editar() {
		this.alteracao = true;
		return PAGE_MANTER_BANCO;
	}
	
	/**
	 * @return Página de pesquisa de banco.
	 */
	public String cancelar() {
		return PAGE_PESQUISAR_BANCO;
	}
		
	/**
	 * Método responsável por limpar os filtros da tela
	 */
	public String limpar() {
		this.fcpBancoVO = new FcpBanco();
		this.dataModel.limparPesquisa();
		
		fcpBancoManutencaoVO = new FcpBanco();
		fcpBancoVO = new FcpBanco();
		
		exibirBotaoNovo = false;
		
		return PAGE_PESQUISAR_BANCO;
	}
	
	/**
	 * @return the alteracao
	 */
	public boolean isAlteracao() {
		return alteracao;
	}

	/**
	 * @param alteracao the alteracao to set
	 */
	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}
	
	/**
	 * @return the comprasCadastrosBasicosFacade
	 */
	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}
	
	/**
	 * @param comprasCadastrosBasicosFacade the comprasCadastrosBasicosFacade to set
	 */
	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	/**
	 * @return the fcpBancoVO
	 */
	public FcpBanco getFcpBancoVO() {
		return fcpBancoVO;
	}

	/**
	 * @param fcpBancoVO the fcpBancoVO to set
	 */
	public void setFcpBancoVO(FcpBanco fcpBancoVO) {
		this.fcpBancoVO = fcpBancoVO;
	}

	/**
	 * @return the fcpBancoManutencaoVO
	 */
	public FcpBanco getFcpBancoManutencaoVO() {
		return fcpBancoManutencaoVO;
	}

	/**
	 * @param fcpBancoManutencaoVO the fcpBancoManutencaoVO to set
	 */
	public void setFcpBancoManutencaoVO(FcpBanco fcpBancoManutencaoVO) {
		this.fcpBancoManutencaoVO = fcpBancoManutencaoVO;
	}

	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<FcpBanco> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<FcpBanco> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * @return the exibirBotaoNovo
	 */
	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	/**
	 * @param exibirBotaoNovo the exibirBotaoNovo to set
	 */
	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
}