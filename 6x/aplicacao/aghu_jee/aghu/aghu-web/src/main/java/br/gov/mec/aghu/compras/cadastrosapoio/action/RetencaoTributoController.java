package br.gov.mec.aghu.compras.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RetencaoTributoController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 6340589476825937393L;
	// Logger
	private static final Log LOG = LogFactory.getLog(RetencaoTributoController.class);
	// Constante de página cadastro de recolhimento
	private static final String PAGE_CADASTRO_RECOLHIMENTO = "codigoRecolhimento";	
	// Constante de página manter cadastro recolhimento
	private static final String PAGE_MANTER_CADASTRO_RECOLHIMENTO = "manterCodigoRecolhimento";	
	
	// Serviço
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	// Modelo Consulta
	private FcpRetencaoTributo fcpRetencaoTributoVO;
	// Modelo Operacional
	private FcpRetencaoTributo fcpRetencaoTributoManutencaoVO = new FcpRetencaoTributo();
	
	// Controle de inclusão ou alteração de código recolhimento
	private boolean alteracao;
	
	//exibe o botao Novo
	private boolean exibirBotaoNovo;
	
	// Exibe o botao 'Voltar'
	private boolean exibirBotaoVoltar;
	
	// Lista de consulta
	@Inject @Paginator
	private DynamicDataModel<FcpRetencaoTributo> dataModel;

	// Lista de Tipos de Tributos
	private List<DominioTipoTributo> listaTipoTributos;
	
	@EJB
	private IPermissionService permissionService;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		exibirBotaoNovo = false;
		exibirBotaoVoltar = false;
		
		this.fcpRetencaoTributoManutencaoVO = new FcpRetencaoTributo();
		this.fcpRetencaoTributoVO = new FcpRetencaoTributo();
		
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastrarCodigoRecolhimento", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}

	/**
	 * Inicializa a pagina realizando a pesquisa de acordo com o DominioTipoTributo
	 * 
	 * @author djalmars
	 * @param tipoTributo
	 * @return String PAGE_CADASTRO_RECOLHIMENTO
	 */
	public String iniciarPesquisando( DominioTipoTributo tipoTributo ){
		this.fcpRetencaoTributoVO.setTipoTributo(tipoTributo);
		this.pesquisar();
		exibirBotaoVoltar = true;
		return PAGE_CADASTRO_RECOLHIMENTO;
	}

	/**
	 * Método responsável por preparar a tela principal
	 */
	public void iniciar() {
	 

	 

		// Popular a lista de tipos de atributos
		this.listaTipoTributos = this.obterListaTiposTributos();
	
	}
	
	
	/**
	 * Exceptions do controller
	 */
	private enum RetencaoTributoMessages implements BusinessExceptionCode {
		CODIGO_RECOLHIMENTO_EXISTENTE,
		CODIGO_RECOLHIMENTO_JA_UTILIZADO,
		ERRO_REMOVER_CODIGO_RECOLHIMENTO,
		ERRO_GRAVAR_CODIGO_RECOLHIMENTO,
		ERRO_EDITAR_CODIGO_RECOLHIMENTO,
		ERRO_PESQUISA_CODIGO_RECOLHIMENTO,
		CODIGO_RECOLHIMENTO_EXCLUIDO_SUCESSO,
		CODIGO_RECOLHIMENTO_INSERIDO_SUCESSO,
		CODIGO_RECOLHIMENTO_ALTERADO_SUCESSO;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true; 
	}

	/**
	 * Método responsável por pesquisar a lista de código de recolhimento
	 * @return lista código de recolhimento
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FcpRetencaoTributo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<FcpRetencaoTributo> fcpRetencaoTributo = new ArrayList<FcpRetencaoTributo>();
		try {
			fcpRetencaoTributo = this.comprasCadastrosBasicosFacade.pesquisarListaCodigoRecolhimento(firstResult, maxResult, orderProperty, asc, this.fcpRetencaoTributoVO);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
		}
		return fcpRetencaoTributo;
	}

	@Override
	public Long recuperarCount() {
		Long countFcpRetencaoTributo = null;
		try {
			countFcpRetencaoTributo = this.comprasCadastrosBasicosFacade.pesquisarCountCodigoRecolhimento(this.fcpRetencaoTributoVO);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
		}
		return countFcpRetencaoTributo;
	}
	
	/**
	  * Método responsábel por inserir ou atualizar código de recolhimento
	  * @return
	  * @throws ApplicationBusinessException
	  */
	 public String gravar() throws BaseException {
	  try {
	   if(alteracao) {
	    // Alterar código de recolhimento
	    this.comprasCadastrosBasicosFacade.atualizarCodigoRecolhimento(this.fcpRetencaoTributoManutencaoVO);
	    // Sucesso
	    apresentarMsgNegocio(Severity.INFO, RetencaoTributoMessages.CODIGO_RECOLHIMENTO_ALTERADO_SUCESSO.toString());
	   } else {
	    // Verificar se registro já existe cadastrado
	    FcpRetencaoTributo vo = new FcpRetencaoTributo();
	    vo.setCodigo(this.fcpRetencaoTributoManutencaoVO.getCodigo());
	    Long count = this.comprasCadastrosBasicosFacade.pesquisarCountCodigoRecolhimento(vo);
	    if(count == 0) {
	     // Inserir código de recolhimento
	     this.comprasCadastrosBasicosFacade.inserirCodigoRecolhimento(fcpRetencaoTributoManutencaoVO);
	     // Sucesso
	     apresentarMsgNegocio(Severity.INFO, RetencaoTributoMessages.CODIGO_RECOLHIMENTO_INSERIDO_SUCESSO.toString());
	    } else {
	     // Exibe mensagem de erro, registro duplicado
	     apresentarMsgNegocio(Severity.INFO, RetencaoTributoMessages.CODIGO_RECOLHIMENTO_EXISTENTE.toString());
	    }
	   }   
	  } catch (BaseException e) {
	   // Tratar excessão de negócio
	   LOG.error("Exceção capturada ao inserir ou alterar registro: ", e);
	   throw new ApplicationBusinessException(alteracao ? RetencaoTributoMessages.ERRO_EDITAR_CODIGO_RECOLHIMENTO.toString() 
	     : RetencaoTributoMessages.ERRO_GRAVAR_CODIGO_RECOLHIMENTO.toString(), Severity.ERROR);
	  }
	  return PAGE_CADASTRO_RECOLHIMENTO;
	 }
	
	/**
	 * Método responsável por remover código de recolhimento
	 * @param codigoRecolhimentoModel
	 * @throws ApplicationBusinessException
	 */
	public void remover() throws ApplicationBusinessException {
		
		try { 
			// Excluir código de recolhimento
			this.comprasCadastrosBasicosFacade.excluirCodigoRecolhimento(fcpRetencaoTributoManutencaoVO);
			
		} catch (ApplicationBusinessException e) {
			
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			return;
		}
		
		// Sucesso
		apresentarMsgNegocio(Severity.INFO, RetencaoTributoMessages.CODIGO_RECOLHIMENTO_EXCLUIDO_SUCESSO.toString());
	}
	
	/**
	 * @return Página de inserção de código de recolhimento.
	 */
	public String cadastrar() {
		this.alteracao = false;
		this.fcpRetencaoTributoManutencaoVO = new FcpRetencaoTributo();
		return PAGE_MANTER_CADASTRO_RECOLHIMENTO;
	}
	
	/**
	 * @return Página de alteração de código de recolhimento.
	 */
	public String editar() {
		this.alteracao = true;
		return PAGE_MANTER_CADASTRO_RECOLHIMENTO;
	}
	
	/**
	 * @return Página de pesquisa de código de recolhimento.
	 */
	public String cancelar() {
		return PAGE_CADASTRO_RECOLHIMENTO;
	}
		
	/**
	 * Método responsável por limpar os filtros da tela
	 */
	public void limpar() {
		this.fcpRetencaoTributoVO = new FcpRetencaoTributo();
		this.dataModel.limparPesquisa();
		exibirBotaoNovo = false;
	}
	
	/**
	 * Recupera lista de tipos de tributos, dominios discretos
	 * @return lista de tipos de tributos
	 */
	public List<DominioTipoTributo> obterListaTiposTributos() {
		List<DominioTipoTributo> lista = new ArrayList<DominioTipoTributo>();
		lista.add(DominioTipoTributo.F);
		lista.add(DominioTipoTributo.P);
		lista.add(DominioTipoTributo.M);
		return lista;
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
	 * @return the fcpRetencaoTributoVO
	 */
	public FcpRetencaoTributo getFcpRetencaoTributoVO() {
		return fcpRetencaoTributoVO;
	}

	/**
	 * @param fcpRetencaoTributoVO the fcpRetencaoTributoVO to set
	 */
	public void setFcpRetencaoTributoVO(FcpRetencaoTributo fcpRetencaoTributoVO) {
		this.fcpRetencaoTributoVO = fcpRetencaoTributoVO;
	}

	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<FcpRetencaoTributo> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<FcpRetencaoTributo> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * @return the listaTipoTributos
	 */
	public List getListaTipoTributos() {
		return listaTipoTributos;
	}

	/**
	 * @param listaTipoTributos the listaTipoTributos to set
	 */
	public void setListaTipoTributos(List listaTipoTributos) {
		this.listaTipoTributos = listaTipoTributos;
	}

	/**
	 * @return the fcpRetencaoTributoManutencaoVO
	 */
	public FcpRetencaoTributo getFcpRetencaoTributoManutencaoVO() {
		return fcpRetencaoTributoManutencaoVO;
	}

	/**
	 * @param fcpRetencaoTributoManutencaoVO the fcpRetencaoTributoManutencaoVO to set
	 */
	public void setFcpRetencaoTributoManutencaoVO(
			FcpRetencaoTributo fcpRetencaoTributoManutencaoVO) {
		this.fcpRetencaoTributoManutencaoVO = fcpRetencaoTributoManutencaoVO;
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

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}
}
