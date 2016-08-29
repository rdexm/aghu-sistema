package br.gov.mec.aghu.compras.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpRetencaoAliquotaId;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class TributoController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 6340589476825937393L;
	// Logger
	private static final Log LOG = LogFactory.getLog(TributoController.class);
	// Constante de página cadastro de recolhimento
	private static final String PAGE_CADASTRO_RECOLHIMENTO = "tributoList";	
	// Constante de página manter cadastro recolhimento
	private static final String PAGE_MANTER_CADASTRO_RECOLHIMENTO = "tributoCRUD";	
	
	// Serviço
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	
	// Modelo Consulta
	private FcpRetencaoAliquota fcpRetencaoAliquotaVO;
	// Modelo Operacional
	private FcpRetencaoAliquota fcpRetencaoAliquotaManutencaoVO;
	
	// Controle de inclusão ou alteração de código recolhimento
	private boolean alteracao;
	
	//exibe o botao Novo
	private boolean exibirBotaoNovo;
	
	// Lista de consulta
	@Inject @Paginator
	private DynamicDataModel<FcpRetencaoAliquota> dataModel;
	// Lista de Tipos de Tributos
	
	private List listaTipoTributos;
	
	// Atributo para verificar se o campo numero é readOnly na pagina de cadastro
	private boolean desabilitarCampoNumero;
	
	// Atributo para  verificar se o campo imposto é readonly na pagina de alteração
	private boolean desabilitarCampoImposto;
	
	private FcpRetencaoTributo fcpRetencaoTributo;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);

		exibirBotaoNovo = false;
		fcpRetencaoAliquotaManutencaoVO = new FcpRetencaoAliquota();
		fcpRetencaoAliquotaVO = new FcpRetencaoAliquota();
		fcpRetencaoAliquotaVO.setId(new FcpRetencaoAliquotaId());
		desabilitarCampoNumero = false;
		desabilitarCampoImposto = false;

		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastrarTributos", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}
	
	/**
	 * Exceptions do controller
	 */
	private enum TributoMessages implements BusinessExceptionCode {
		TRIBUTO_EXISTENTE,
		TRIBUTO_JA_UTILIZADO,
		ERRO_REMOVER_TRIBUTO,
		ERRO_GRAVAR_TRIBUTO,
		ERRO_EDITAR_TRIBUTO,
		ERRO_PESQUISA_TRIBUTO,
		TRIBUTO_EXCLUIDO_SUCESSO,
		TRIBUTO_INSERIDO_SUCESSO,
		TRIBUTO_ALTERADO_SUCESSO;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	/**
	 * Método responsável por pesquisar a lista de tributos
	 * @return lista código de recolhimento
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FcpRetencaoAliquota> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<FcpRetencaoAliquota> fcpRetencaoAliquota = new ArrayList<FcpRetencaoAliquota>();
		try {
			fcpRetencaoAliquota = this.comprasCadastrosBasicosFacade.pesquisarListaTributo(firstResult, maxResult, orderProperty, asc, this.fcpRetencaoTributo);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
		}
		return fcpRetencaoAliquota;
	}

	@Override
	public Long recuperarCount() {
		Long countFcpTributo = null;
		try {
			countFcpTributo = this.comprasCadastrosBasicosFacade.pesquisarCountTributo(this.fcpRetencaoTributo);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
		}
		return countFcpTributo;
	}
	
	/**
	  * Método responsábel por inserir ou atualizar tributo
	  * @return
	 * @throws BaseException, ApplicationBusinessException 
	  */
	 public String gravar() throws BaseException, ApplicationBusinessException {
		 
		 try {
			 
			 if(alteracao) {
				 // Alterar código de recolhimento
				 fcpRetencaoAliquotaManutencaoVO.setAlteradoEm(new Date());
				 
				 fcpRetencaoAliquotaManutencaoVO.setRapServidores(registroColaboradorFacade.obterServidorPessoa(servidorLogadoFacade.obterServidorLogado()));
				 
				 this.comprasCadastrosBasicosFacade.atualizarRetencaoAliquota(this.fcpRetencaoAliquotaManutencaoVO);
				 // Sucesso
				 apresentarMsgNegocio(Severity.INFO, TributoMessages.TRIBUTO_ALTERADO_SUCESSO.toString());
				 fcpRetencaoAliquotaManutencaoVO = new FcpRetencaoAliquota();
				 
			 } else {
				 
				 // Inserir código de recolhimento
				 //Inserindo uma sequence para o campo Numero
				 
				 this.comprasCadastrosBasicosFacade.inserirRetencaoAliquota(fcpRetencaoAliquotaManutencaoVO);
				 
				 // Sucesso
				 apresentarMsgNegocio(Severity.INFO, TributoMessages.TRIBUTO_INSERIDO_SUCESSO.toString());
				 fcpRetencaoAliquotaManutencaoVO = new FcpRetencaoAliquota();
			}
			 
		 } catch (ApplicationBusinessException e) {
		  
			 if (e.getMessage().compareToIgnoreCase("Data inicial deve ser menor que a data final.") == 0) {
			  
				 apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				 return null;
			 }
		  
		LOG.error("Exceção capturada ao inserir ou alterar registro: ", e);
	  }
	  
	  return PAGE_CADASTRO_RECOLHIMENTO;
	 }
	
	/**
	 * Método responsável por remover Tributo (Aliquota Tributo)
	 * @param fcpRetencaoAliquotaManutencaoVO
	 * @throws ApplicationBusinessException 
	 */
	public void remover() throws ApplicationBusinessException {
		
		try { 
			
			this.comprasCadastrosBasicosFacade.excluirRetencaoAliquota(fcpRetencaoAliquotaManutencaoVO);
			
		} catch (ApplicationBusinessException e) {
			
			if (e.getMessage().compareToIgnoreCase("Tributo está sendo utilizado no sistema e não pode ser excluído.") == 0) {
				
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				return;
			
			} else {
				
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}
		}
		// Sucesso
		apresentarMsgNegocio(Severity.INFO, TributoMessages.TRIBUTO_EXCLUIDO_SUCESSO.toString());
	}
	
	/**
	 * @return Página de inserção de código de recolhimento.
	 * @throws BaseException 
	 */
	public String cadastrar() throws BaseException {
		
		this.alteracao = false;
		
		//Instanciando os objetos que devem aparecer na view para inserir dados
		this.fcpRetencaoAliquotaManutencaoVO = new FcpRetencaoAliquota();
		FcpRetencaoAliquotaId fcpRetencaoAliquotaId = new FcpRetencaoAliquotaId();
		
		fcpRetencaoAliquotaId.setFriCodigo(fcpRetencaoTributo.getCodigo());
		
		//setando data do sistema no campo criadoEm e alteradoEm
		Date date = new Date();
		fcpRetencaoAliquotaManutencaoVO.setCriadoEm(date);
		fcpRetencaoAliquotaManutencaoVO.setAlteradoEm(date);
		
		// setando o rapServidor de acordo com o usuario logado
		fcpRetencaoAliquotaManutencaoVO.setRapServidores(registroColaboradorFacade.obterServidorPessoa(servidorLogadoFacade.obterServidorLogado()));
		
		this.fcpRetencaoAliquotaManutencaoVO.setId(fcpRetencaoAliquotaId);
		
		desabilitarCampoNumero = true;
		desabilitarCampoImposto = false;
		
		return PAGE_MANTER_CADASTRO_RECOLHIMENTO;
	}
	
	/**
	 * @return Página de alteração de código de recolhimento.
	 */
	public String editar() {
		
		this.alteracao = true;
		desabilitarCampoImposto = true;
		return PAGE_MANTER_CADASTRO_RECOLHIMENTO;
	}
	
	/**
	 * @return Página de pesquisa de código de recolhimento.
	 */
	public String cancelar() {
		desabilitarCampoImposto = false;
		desabilitarCampoNumero = false;
		return PAGE_CADASTRO_RECOLHIMENTO;
	}
		
	public List<FcpRetencaoTributo> pesquisarRecolhimentoPorCodigoOuDescricao(final String paramPesquisa) throws BaseException {
		
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarRecolhimentoPorCodigoOuDescricao((String) paramPesquisa),pesquisarRecolhimentoPorCodigoOuDescricaoCount(paramPesquisa));
	}
	
	public Long pesquisarRecolhimentoPorCodigoOuDescricaoCount(final String paramPesquisa) throws BaseException {
		  return comprasCadastrosBasicosFacade.pesquisarRecolhimentoPorCodigoOuDescricaoCount((String) paramPesquisa);
	}
	
	/**
	 * Método responsável por limpar os filtros da tela
	 */
	public void limpar() {
		this.fcpRetencaoTributo = null;
		this.fcpRetencaoAliquotaVO = new FcpRetencaoAliquota();
		fcpRetencaoAliquotaVO.setId(new FcpRetencaoAliquotaId());
		this.dataModel.limparPesquisa();
		exibirBotaoNovo = false;
	}
	
	/**
	 * @return the comprasCadastrosBasicosFacade
	 */
	public IComprasCadastrosBasicosFacade getcomprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	/**
	 * @param comprasCadastrosBasicosFacade the comprasCadastrosBasicosFacade to set
	 */
	public void setcomprasCadastrosBasicosFacade(IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<FcpRetencaoAliquota> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<FcpRetencaoAliquota> dataModel) {
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

	/**
	 * @return the fcpRetencaoAliquotaVO
	 */
	public FcpRetencaoAliquota getFcpRetencaoAliquotaVO() {
		return fcpRetencaoAliquotaVO;
	}

	/**
	 * @param fcpRetencaoAliquotaVO the fcpRetencaoAliquotaVO to set
	 */
	public void setFcpRetencaoAliquotaVO(FcpRetencaoAliquota fcpRetencaoAliquotaVO) {
		this.fcpRetencaoAliquotaVO = fcpRetencaoAliquotaVO;
	}

	/**
	 * @return the fcpRetencaoAliquotaManutencaoVO
	 */
	public FcpRetencaoAliquota getFcpRetencaoAliquotaManutencaoVO() {
		return fcpRetencaoAliquotaManutencaoVO;
	}

	/**
	 * @param fcpRetencaoAliquotaManutencaoVO the fcpRetencaoAliquotaManutencaoVO to set
	 */
	public void setFcpRetencaoAliquotaManutencaoVO(
			FcpRetencaoAliquota fcpRetencaoAliquotaManutencaoVO) {
		this.fcpRetencaoAliquotaManutencaoVO = fcpRetencaoAliquotaManutencaoVO;
	}
	
	/**
	 * @param the desabilitarCampoNumero
	 */
	public boolean isDesabilitarCampoNumero() {
		return desabilitarCampoNumero;
	}
	
	/**
	 * @param desabilitarCampoNumero the desabilitarCampoNumero to set
	 */
	public void setDesabilitarCampoNumero(boolean desabilitarCampoNumero) {
		this.desabilitarCampoNumero = desabilitarCampoNumero;
	}

	public boolean isDesabilitarCampoImposto() {
		return desabilitarCampoImposto;
	}

	public void setDesabilitarCampoImposto(boolean desabilitarCampoImposto) {
		this.desabilitarCampoImposto = desabilitarCampoImposto;
	}
	
	/**
	 * @return the fcpRetencaoTributo
	 */
	public FcpRetencaoTributo getFcpRetencaoTributo() {
		return fcpRetencaoTributo;
	}

	/**
	 * @param fcpRetencaoTributo the fcpRetencaoTributo to set
	 */
	public void setFcpRetencaoTributo(FcpRetencaoTributo fcpRetencaoTributo) {
		this.fcpRetencaoTributo = fcpRetencaoTributo;
	}
	
}
