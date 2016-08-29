package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigEscalaPessoa;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EscalaAlocacaoServicosAssistenciaisPaginatorController extends ActionController implements ActionPaginator {

	private static final String ESCALA_ALOCACAO_SERVICOS_ASSISTENCIAIS_CRUD = "escalaAlocacaoServicosAssistenciaisCRUD";

	@Inject @Paginator
	private DynamicDataModel<SigEscalaPessoa> dataModel;

	private static final Log LOG = LogFactory.getLog(EscalaAlocacaoServicosAssistenciaisPaginatorController.class);

	private static final long serialVersionUID = -8630170010697191887L;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private IPermissionService permissionService;

	private FccCentroCustos centroCusto;
	private Integer seqEscalaPessoas;

	private boolean recarregarLista = false;
	
	private SigEscalaPessoa parametroSelecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarEscalaAlocacaoServicosAssistenciais","editar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "excluirEscalaAlocacaoServicosAssistenciais","excluir"));
		this.begin(conversation);
		LOG.debug("begin conversation");
	}
	
	public void iniciar() {
	 

		if (this.isRecarregarLista()) {
			this.reiniciarPaginator();
			this.setRecarregarLista(false);
		}
	
	}
	
	
	@Override
	public List<SigEscalaPessoa> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<SigEscalaPessoa> result = this.custosSigCadastrosBasicosFacade
				.pesquisarEscalaPessoas(firstResult, maxResult, orderProperty,
						asc, this.centroCusto);

		if (result == null) {
			result = new ArrayList<SigEscalaPessoa>();
		}
		return result;
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade
				.pesquisarEscalaPessoasCount(this.centroCusto);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa)
			throws BaseException {
		return centroCustoFacade.pesquisarCentroCustoComStatusDaEspecialidade(paramPesquisa, DominioSituacao.A);
	}

	public void limparCentroCusto() {
		this.setCentroCusto(null);
		this.iniciar();
	}

	public void pesquisarEscalasAssistenciais() {
		reiniciarPaginator();
	}

	public void limpar() {
		this.limparCentroCusto();
		this.setSeqEscalaPessoas(null);
		this.setAtivo(Boolean.FALSE);
	}

	public void excluir() {

		this.seqEscalaPessoas = parametroSelecionado.getSeq();
		if (this.seqEscalaPessoas != null) {
			SigEscalaPessoa escalaPessoas = this.custosSigCadastrosBasicosFacade
					.obterEscalaPessoas(this.seqEscalaPessoas);

			if (escalaPessoas != null) {
				this.custosSigCadastrosBasicosFacade.excluir(escalaPessoas);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EXCLUSAO_ESCALA_ASSISTENCIAL");
				reiniciarPaginator();
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ESCALA_ASSISTENCIAL_INEXISTENTE");
			}
			this.limpar();
			this.dataModel.reiniciarPaginator();
		}
	}

	public String inserir() {
		this.setCentroCusto(null);
		return ESCALA_ALOCACAO_SERVICOS_ASSISTENCIAIS_CRUD;
	}

	public String editar() {
		return ESCALA_ALOCACAO_SERVICOS_ASSISTENCIAIS_CRUD;
	}
	
	public String visualizar(){
		return ESCALA_ALOCACAO_SERVICOS_ASSISTENCIAIS_CRUD;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getSeqEscalaPessoas() {
		return seqEscalaPessoas;
	}

	public void setSeqEscalaPessoas(Integer seqEscalaPessoas) {
		this.seqEscalaPessoas = seqEscalaPessoas;
	}

	public boolean isRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	}
 
	public DynamicDataModel<SigEscalaPessoa> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigEscalaPessoa> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public SigEscalaPessoa getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SigEscalaPessoa parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
