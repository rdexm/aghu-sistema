package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class TipoDocumentoPagamentoController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 660152433124905917L;
	
	// Logger
	private static final Log LOG = LogFactory.getLog(TipoDocumentoPagamentoController.class);
	// Constante de página cadastro de tipo documento pagamento
	private static final String PAGE_PESQUISAR_TIPO_DOCUMENTO_PAGAMENTO = "pesquisarTipoDocumentoPagamento";	
	// Constante de página manter cadastro de tipo documento pagamento
	private static final String PAGE_MANTER_TIPO_DOCUMENTO_PAGAMENTO = "manterTipoDocumentoPagamento";	
	
	// Serviço
	@EJB
	private IComprasFacade comprasFacade;
	
	// Modelo Consulta
	private FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoVO;
	// Modelo Operacional
	private FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoManutencaoVO;
	
	// Controle de inclusão ou alteração de tipo documento pagamento
	private boolean alteracao;
	
	// Lista de consulta
	@Inject @Paginator
	private DynamicDataModel<FcpTipoDocumentoPagamento> dataModel;
	
	// Listar as permissões de usuário
	@EJB
	private IPermissionService permissionService;
	
	//Exibição do botão novo
	private boolean exibirBotaoNovo;
	
	//Ativar ou Inativar um fcpTipoDocumentoVO
	private boolean ativo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.fcpTipoDocumentoPagamentoVO = new FcpTipoDocumentoPagamento();
		this.fcpTipoDocumentoPagamentoManutencaoVO = new FcpTipoDocumentoPagamento();
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastrarTipoDocPagamento", "executar");
		this.getDataModel().setUserEditPermission(permissao);
		this.exibirBotaoNovo = false;
		this.ativo = false;
	}
	
	/**
	 * Exibir a página de pesquisa de tipo documento pagamentos.
	 * @return
	 */
	public String iniciar() {
		return PAGE_PESQUISAR_TIPO_DOCUMENTO_PAGAMENTO;
	}
	
	/**
	 * Mensagems do sistema
	 */		
	private enum TipoDocumentoPagamentoMessages implements BusinessExceptionCode {
		TIPO_DOC_PGTO_CADASTRADO_SUCESSO,
		TIPO_DOC_PGTO_ALTERADO_SUCESSO
	}
	
	/**
	 * Metodo utilizado para pesquisar uma lista de tipoDocumentoPagamento e popular o dataServe 
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	/**
	 * Método responsável por pesquisar a lista de tipo documento pagamentos
	 * @return lista de tipo documento pagamentos
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FcpTipoDocumentoPagamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.comprasFacade.pesquisarTiposDocumentoPagamento(firstResult, maxResult, orderProperty, asc, this.fcpTipoDocumentoPagamentoVO);
	}

	/**
	 * Método responsável por recuperar a quantidade de tipo documento pagamentos
	 * @return quantidade de tipo documento pagamentos
	 */
	@Override
	public Long recuperarCount() {
		return this.comprasFacade.pesquisarCountTipoDocumentoPagamento(this.fcpTipoDocumentoPagamentoVO);
	}
	
	/**
	  * Método responsável por inserir ou atualizar tipo documento pagamento
	  * @return
	  * @throws ApplicationBusinessException
	  */
	public String gravar() throws ApplicationBusinessException {
		try {
			
			//verifica o campo ativo para poder setar o tipo de situação
			if (ativo) {
				this.fcpTipoDocumentoPagamentoManutencaoVO.setIndSituacao(DominioSituacao.A);
			} else {
				this.fcpTipoDocumentoPagamentoManutencaoVO.setIndSituacao(DominioSituacao.I);
			}
			
			if (this.alteracao) {
				// Alterar
				this.comprasFacade.atualizarTipoDocumentoPagamento(this.fcpTipoDocumentoPagamentoManutencaoVO);
				apresentarMsgNegocio(Severity.INFO, TipoDocumentoPagamentoMessages.TIPO_DOC_PGTO_ALTERADO_SUCESSO.toString());
			} else {
				// Inserir
				this.comprasFacade.inserirTipoDocumentoPagamento(this.fcpTipoDocumentoPagamentoManutencaoVO);
				apresentarMsgNegocio(Severity.INFO, TipoDocumentoPagamentoMessages.TIPO_DOC_PGTO_CADASTRADO_SUCESSO.toString());			
			}
		} catch (ApplicationBusinessException e) {		
			LOG.error("Exceção capturada ao inserir ou atualizar: ", e);
			apresentarMsgNegocio(Severity.ERROR, e.getMessage(), e.getParameters());
		}	
		return PAGE_PESQUISAR_TIPO_DOCUMENTO_PAGAMENTO;
	}
	
	/**
	 * @return Página de inserção de tipo documento pagamento
	 */
	public String cadastrar() {
		this.alteracao = false;
		this.ativo = true;
		
		this.fcpTipoDocumentoPagamentoManutencaoVO = new FcpTipoDocumentoPagamento();
		this.fcpTipoDocumentoPagamentoManutencaoVO.setIndSituacao(DominioSituacao.A);
		
		return PAGE_MANTER_TIPO_DOCUMENTO_PAGAMENTO;
	}
	
	/**
	 * @return Página de alteração de tipo documento pagamento
	 */
	public String editar() {
		this.alteracao = true;
		
		this.ativo = this.fcpTipoDocumentoPagamentoManutencaoVO.getIndSituacao().isAtivo();
		
		return PAGE_MANTER_TIPO_DOCUMENTO_PAGAMENTO;
	}
	
	/**
	 * @return Página de pesquisa de tipo documento pagamento
	 */
	public String cancelar() {
		return PAGE_PESQUISAR_TIPO_DOCUMENTO_PAGAMENTO;
	}
		
	/**
	 * Método responsável por limpar os filtros da tela
	 */
	public String limpar() {
		this.fcpTipoDocumentoPagamentoVO = new FcpTipoDocumentoPagamento();
		this.dataModel.limparPesquisa();
		exibirBotaoNovo = false;
		return PAGE_PESQUISAR_TIPO_DOCUMENTO_PAGAMENTO;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public FcpTipoDocumentoPagamento getFcpTipoDocumentoPagamentoVO() {
		return fcpTipoDocumentoPagamentoVO;
	}

	public void setFcpTipoDocumentoPagamentoVO(
			FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoVO) {
		this.fcpTipoDocumentoPagamentoVO = fcpTipoDocumentoPagamentoVO;
	}

	public FcpTipoDocumentoPagamento getFcpTipoDocumentoPagamentoManutencaoVO() {
		return fcpTipoDocumentoPagamentoManutencaoVO;
	}

	public void setFcpTipoDocumentoPagamentoManutencaoVO(
			FcpTipoDocumentoPagamento fcpTipoDocumentoPagamentoManutencaoVO) {
		this.fcpTipoDocumentoPagamentoManutencaoVO = fcpTipoDocumentoPagamentoManutencaoVO;
	}

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}

	public DynamicDataModel<FcpTipoDocumentoPagamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FcpTipoDocumentoPagamento> dataModel) {
		this.dataModel = dataModel;
	}

	public IPermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(IPermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}