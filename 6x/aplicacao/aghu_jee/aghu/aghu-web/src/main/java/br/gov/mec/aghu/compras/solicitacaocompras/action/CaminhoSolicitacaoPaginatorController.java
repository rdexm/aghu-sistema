package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CaminhoSolicitacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1877086062229123300L;

	private static final String CAMINHOS_CRUD = "caminhosCRUD";

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private SecurityController securityController;	

	
    private ScoPontoParadaSolicitacao origemParada;
	private ScoPontoParadaSolicitacao destinoParada;
	
	// Campos para exclusão 
	private Short ppsCodigoInicio;
	private Short ppsCodigo;
	
	@Inject @Paginator
	private DynamicDataModel<ScoCaminhoSolicitacao> dataModel;
	
	private ScoCaminhoSolicitacao selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioCompras,gravar") || securityController.usuarioTemPermissao("cadastrarAdmCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}
	
	@Override
	public Long recuperarCount() {
		return solicitacaoComprasFacade.pesquisarCaminhoSolicitacaoCount(origemParada, destinoParada);
	}

	@Override
	public List<ScoCaminhoSolicitacao> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return solicitacaoComprasFacade.pesquisarCaminhoSolicitacao(firstResult, maxResult,	orderProperty, asc, origemParada, destinoParada);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String visualizar() {
		return CAMINHOS_CRUD;
	}

	public String inserir() {
		return CAMINHOS_CRUD;
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.origemParada = null;
		this.destinoParada = null;
	}
	
	//Método para carregar suggestion Origem e Destino
	public List<ScoPontoParadaSolicitacao> pesquisarOrigDestParada(String pontoParadaSolic) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao((String)pontoParadaSolic);
	}
	
	public void excluir(){
		try{
			this.solicitacaoComprasFacade.excluirCaminhoSolicitacao(selecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CAMINHO_SOLIC_DELET_SUCESSO");		
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public ScoPontoParadaSolicitacao getOrigemParada() {
		return origemParada;
	}

	public void setOrigemParada(ScoPontoParadaSolicitacao origemParada) {
		this.origemParada = origemParada;
	}

	public ScoPontoParadaSolicitacao getDestinoParada() {
		return destinoParada;
	}

	public void setDestinoParada(ScoPontoParadaSolicitacao destinoParada) {
		this.destinoParada = destinoParada;
	}

	public void setPpsCodigoInicio(Short ppsCodigoInicio) {
		this.ppsCodigoInicio = ppsCodigoInicio;
	}

	public Short getPpsCodigoInicio() {
		return ppsCodigoInicio;
	}

	public void setPpsCodigo(Short ppsCodigo) {
		this.ppsCodigo = ppsCodigo;
	}

	public Short getPpsCodigo() {
		return ppsCodigo;
	} 


	public DynamicDataModel<ScoCaminhoSolicitacao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoCaminhoSolicitacao> dataModel) {
	 this.dataModel = dataModel;
	}

	public ScoCaminhoSolicitacao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoCaminhoSolicitacao selecionado) {
		this.selecionado = selecionado;
	}
}
