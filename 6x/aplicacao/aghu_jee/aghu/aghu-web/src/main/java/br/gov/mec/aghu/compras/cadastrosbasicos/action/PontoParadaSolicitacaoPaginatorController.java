package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PontoParadaSolicitacaoPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 1877086062229123300L;

	private static final String PONTO_PARADA_SOLICITACAO_CRUD = "pontoParadaSolicitacaoCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private SecurityController securityController;	

	private ScoPontoParadaSolicitacao pontoParada = new ScoPontoParadaSolicitacao();
	
	@Inject @Paginator
	private DynamicDataModel<ScoPontoParadaSolicitacao> dataModel;
	
	private ScoPontoParadaSolicitacao selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}
	
	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCount(pontoParada);
	}

	@Override
	public List<ScoPontoParadaSolicitacao> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacao(firstResult, maxResult,orderProperty, asc, pontoParada);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setPontoParada(new ScoPontoParadaSolicitacao());
	}
	
	public String inserir() {
		return PONTO_PARADA_SOLICITACAO_CRUD;
	}
	
	public String editar() {
		return PONTO_PARADA_SOLICITACAO_CRUD;
	}
	
	public String visualizar() {
		return PONTO_PARADA_SOLICITACAO_CRUD;
	}

	public void excluir() {
		try {
			comprasCadastrosBasicosFacade.excluirPontoParadaSolicitacao(selecionado.getCodigo());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PONTO_PARADA_SOLIC_DELETE_SUCESSO");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public DynamicDataModel<ScoPontoParadaSolicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ScoPontoParadaSolicitacao> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoPontoParadaSolicitacao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoPontoParadaSolicitacao selecionado) {
		this.selecionado = selecionado;
	}
}