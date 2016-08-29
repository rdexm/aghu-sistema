package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoOcupacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final String GRUPO_OCUPACAO_CRUD = "grupoOcupacaoCRUD";
	private static final long serialVersionUID = -1502085412934731570L;
	private static final Log LOG = LogFactory.getLog(GrupoOcupacaoPaginatorController.class);
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject @Paginator
	private DynamicDataModel<SigGrupoOcupacoes> dataModel;
	private SigGrupoOcupacoes parametroSelecionado;

	private String descricao;
	private FccCentroCustos centroCusto;
	private DominioSituacao situacao;

	private Integer seqGrupoOcupacao;
	
	@PostConstruct
	protected void inicializar(){
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarGrupoOcupacao","editar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "excluirGrupoOcupacao", "excluir"));
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public String editar() {
		return GRUPO_OCUPACAO_CRUD;
	}

	public String visualizar() {
		return GRUPO_OCUPACAO_CRUD;
	}

	public String cadastrarGrupoOcupacao() {
		return GRUPO_OCUPACAO_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade.pesquisarGrupoOcupacaoCount(descricao, situacao, centroCusto);
	}

	@Override
	public List<SigGrupoOcupacoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigCadastrosBasicosFacade.pesquisarGrupoOcupacao(firstResult, maxResult, orderProperty, asc, descricao, situacao, centroCusto);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
	}

	public void limparCentroCusto() {
		this.setCentroCusto(null);
	}

	public void pesquisarAtividade() {
		reiniciarPaginator();
	}

	public void limpar() {
		this.setDescricao(null);
		this.setSituacao(null);
		this.setCentroCusto(null);
		this.setAtivo(false);
	}

	public void excluir() {
		try {
			SigGrupoOcupacoes grupoOcupacao = this.custosSigCadastrosBasicosFacade.obterGrupoOcupacao(this.getSeqGrupoOcupacao());
			if (grupoOcupacao != null) {
				this.custosSigCadastrosBasicosFacade.excluirGrupoOcupacao(grupoOcupacao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_GRUPO_OCUPACAO");
				reiniciarPaginator();
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRUPO_OCUPACAO_INEXISTENTE");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// Getters and Setters
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getSeqGrupoOcupacao() {
		return seqGrupoOcupacao;
	}

	public void setSeqGrupoOcupacao(Integer seqGrupoOcupacao) {
		this.seqGrupoOcupacao = seqGrupoOcupacao;
	}
	
	public DynamicDataModel<SigGrupoOcupacoes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigGrupoOcupacoes> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public SigGrupoOcupacoes getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SigGrupoOcupacoes parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
		if(parametroSelecionado  != null){
			this.setSeqGrupoOcupacao(parametroSelecionado.getSeq());
		}
	}
}
