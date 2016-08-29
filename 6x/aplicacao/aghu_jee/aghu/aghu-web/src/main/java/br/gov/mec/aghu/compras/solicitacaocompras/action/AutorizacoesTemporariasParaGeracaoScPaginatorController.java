package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class AutorizacoesTemporariasParaGeracaoScPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 510451735214482173L;
	private static final String AUTORIZACOES_TEMPORARIAS_PARA_GERACAO_SC_CRUD = "autorizacoesTemporariasParaGeracaoScCRUD";
	ScoPontoParadaSolicitacao  scoPontoParadaSolicitacao;
	RapServidores  servidor;
	RapServidores  servidorAutorizado;

	@EJB
	protected ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	protected IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private SecurityController securityController;	
	
	@Inject @Paginator
	private DynamicDataModel<ScoPontoServidor> dataModel;
	
	private ScoPontoServidor selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarPermissoesCompras,gravar") || securityController.usuarioTemPermissao("cadastrarAdmCompras,gravar") ;
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}
	
	public void pesquisarScoPontoServidor() {
		dataModel.reiniciarPaginator();
	}
	
	public String editar(){
		return AUTORIZACOES_TEMPORARIAS_PARA_GERACAO_SC_CRUD;
	}
	
	public String visualizar(){
		return AUTORIZACOES_TEMPORARIAS_PARA_GERACAO_SC_CRUD;
	}

	@Override
	public List<ScoPontoServidor> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		 return solicitacaoComprasFacade.pesquisarScoPontoServidor(scoPontoParadaSolicitacao, servidor, servidorAutorizado ,  firstResult, maxResult , orderProperty, asc);
	}
	
	@Override
	public Long recuperarCount() {
		return solicitacaoComprasFacade.pesquisarScoPontoServidorCount(scoPontoParadaSolicitacao, servidor, servidorAutorizado);
	}

	public List<ScoPontoParadaSolicitacao> listarPontoParadaSolicitacao(Object pesquisa) {
		return comprasCadastrosBasicosFacade.listarPontoParadaSolicitacao(pesquisa);
	}

	public List<ScoPontoParadaSolicitacao> listarPontoParadaPontoServidor(String pesquisa) {
		return comprasCadastrosBasicosFacade.listarPontoParadaPontoServidor(pesquisa);
	}
	
	public List<RapServidores> pesquisarServidorAtivo(final String paramPesquisa) {
		return registroColaboradorFacade.pesquisarServidor(	paramPesquisa );
	}
	
	public List<RapServidores> pesquisarServidorAtivoPontoServidor(final String paramPesquisa) {
		return registroColaboradorFacade.pesquisarServidorPontoServidor(	paramPesquisa );
	}
	
	public void limparCampos() {
		this.scoPontoParadaSolicitacao = null;
		this.servidor = null;
		this.servidorAutorizado = null;
		dataModel.limparPesquisa();
	}
	
	public ScoPontoParadaSolicitacao getScoPontoParadaSolicitacao() {
		return scoPontoParadaSolicitacao;
	}

	public void setScoPontoParadaSolicitacao(
			ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {
		this.scoPontoParadaSolicitacao = scoPontoParadaSolicitacao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidorAutorizado() {
		return servidorAutorizado;
	}

	public void setServidorAutorizado(RapServidores servidorAutorizado) {
		this.servidorAutorizado = servidorAutorizado;
	}

	public DynamicDataModel<ScoPontoServidor> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoPontoServidor> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoPontoServidor getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoPontoServidor selecionado) {
		this.selecionado = selecionado;
	}
}
