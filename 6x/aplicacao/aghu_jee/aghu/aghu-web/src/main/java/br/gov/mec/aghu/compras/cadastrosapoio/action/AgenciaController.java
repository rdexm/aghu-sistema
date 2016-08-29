package br.gov.mec.aghu.compras.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpAgenciaBancoId;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class AgenciaController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -8526725440449647355L;

	private static final Log LOG = LogFactory.getLog(AgenciaController.class);
	private static final String PAGE_PESQUISAR_AGENCIA = "pesquisarAgencia"; 	
	private static final String PAGE_MANTER_AGENCIA = "manterAgencia";	

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private FcpBanco banco;
	private FcpAgenciaBanco agencia;
	private boolean alteracao;
	
	@Inject @Paginator
	private DynamicDataModel<FcpAgenciaBanco> dataModel;
	
	@EJB
	private IPermissionService permissionService;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);	
	}
	
	public void inicio(){
		agencia = new FcpAgenciaBanco();
		final Boolean permissao = permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "cadastrarBanco", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}

	private enum AgenciaMessages implements BusinessExceptionCode {
		AGENCIA_INCLUIDO_COM_SUCESSO,
		AGENCIA_ALTERADO_COM_SUCESSO,
		AGENCIA_EXCLUIDO_COM_SUCESSO;
	}
	
	/**
	 * Suggestion Box
	 * Método responsável por pesquisar a lista de bancos
	 * @return lista de bancos
	 */
	public List<FcpBanco> pesquisarBanco(String paramPesquisa) throws BaseException {	
		try {
			return this.returnSGWithCount(comprasCadastrosBasicosFacade.pesquisarSuggestionBox(paramPesquisa == null ? null : paramPesquisa ), pesquisarBancoCount(paramPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/**
	 * Suggestion Box
	 * Método responsável por recuperar a quantidade de bancos
	 * @return quantidade de bancos
	 */	
	private Long pesquisarBancoCount(String paramPesquisa) throws BaseException {
		try {
			return comprasCadastrosBasicosFacade.pesquisarSuggestionBoxCount(paramPesquisa == null ? null : paramPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;  
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FcpAgenciaBanco> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		try {
			return comprasCadastrosBasicosFacade.pesquisarListaAgencia(firstResult, maxResult, orderProperty, asc, banco.getCodigo(), null);	
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
			apresentarExcecaoNegocio(e);
		}
		return null;
	}


	@Override
	public Long recuperarCount() {
		try {
			return comprasCadastrosBasicosFacade.pesquisarCountListaAgencia(banco.getCodigo(), null);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/**
	  * Método responsável por inserir ou atualizar agencia
	  * @return
	  * @throws ApplicationBusinessException
	  */
	public String gravar() {
		try {
			agencia.getId().setBcoCodigo(banco.getCodigo());
			
			if (!alteracao) {
				comprasCadastrosBasicosFacade.verificarAgenciaBancariaComMesmoCodigo(agencia);
			}			
			
			comprasCadastrosBasicosFacade.persistirAgencia(agencia);
			apresentarMsgNegocio(Severity.INFO,
					alteracao ? AgenciaMessages.AGENCIA_ALTERADO_COM_SUCESSO.toString()
							  : AgenciaMessages.AGENCIA_INCLUIDO_COM_SUCESSO.toString());

		} catch (BaseException e) {
			LOG.error("Exceção capturada ao inserir ou atualizar: ", e);
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return PAGE_PESQUISAR_AGENCIA;
	}
	
	/**
	 * Método responsável por remover agencia
	 * @param codigoAgencia
	 * @throws ApplicationBusinessException
	 */
	public void remover() {
		try { 
			comprasCadastrosBasicosFacade.excluirAgencia(agencia);
			apresentarMsgNegocio(Severity.INFO, AgenciaMessages.AGENCIA_EXCLUIDO_COM_SUCESSO.toString());
		} catch (BaseException e) {		
			LOG.error("Exceção capturada ao remover: ", e);
			apresentarExcecaoNegocio(e);
		}				
	}
	
	/**
	 * @return Página de inserção de agencia.
	 */
	public String cadastrar() {
		alteracao = false;
		agencia = new FcpAgenciaBanco();
		agencia.setId(new FcpAgenciaBancoId());
		return PAGE_MANTER_AGENCIA;
	}
	
	/**
	 * @return Página de alteração de agencia.
	 * @throws BaseException 
	 */
	public String editar() throws BaseException {
		alteracao = true;
		return PAGE_MANTER_AGENCIA;
	}
	
	/**
	 * @return Página de pesquisa de agencia.
	 */
	public String cancelar() {
		return PAGE_PESQUISAR_AGENCIA;
	}
		
	/**
	 * Método responsável por limpar os filtros da tela
	 */
	public String limpar() {
		dataModel.limparPesquisa();
		banco = null;
		agencia = new FcpAgenciaBanco();
		return PAGE_PESQUISAR_AGENCIA;
	}

	public FcpBanco getBanco() {
		return banco;
	}

	public void setBanco(FcpBanco banco) {
		this.banco = banco;
	}

	public FcpAgenciaBanco getAgencia() {
		return agencia;
	}

	public void setAgencia(FcpAgenciaBanco agencia) {
		this.agencia = agencia;
	}

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}

	public DynamicDataModel<FcpAgenciaBanco> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FcpAgenciaBanco> dataModel) {
		this.dataModel = dataModel;
	}

}