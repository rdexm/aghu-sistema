package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaTiposSessaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 749484173808403753L;

	private static final String PAGE_CADASTRA_TIPOS_SESSAO = "procedimentoterapeutico-cadastraTiposSessao";
	private static final String PAGE_VISUALIZAR_HISTORICO_SESSAO = "procedimentoterapeutico-visualizaHistoricoTipoSessao";
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject @Paginator
	private DynamicDataModel<MptTipoSessao> dataModel;
	

	@Inject
	private CadastraTiposSessaoController cadastraTiposSessaoController;
	
	@Inject
	private VisualizaHistoricoTipoSessaoPaginatorController visualizaHistoricoTipoSessaoPaginatorController;
	
	private String descricao;
	private AghUnidadesFuncionais unidadeFuncional;
	private DominioSituacao situacao;
	private MptTipoSessao itemSelecionado;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorCaracteristica(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorCaracteristica(strPesquisa, null),
				this.aghuFacade.listarUnidadesFuncionaisPorCaracteristicaCount(strPesquisa, null));
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		Short unfSeq = this.unidadeFuncional != null ? this.unidadeFuncional.getSeq() : null;
		return this.procedimentoTerapeuticoFacade.listarMptTipoSessaoCount(this.descricao, unfSeq, this.situacao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MptTipoSessao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short unfSeq = this.unidadeFuncional != null ? this.unidadeFuncional.getSeq() : null;
		return this.procedimentoTerapeuticoFacade.listarMptTipoSessao(this.descricao, unfSeq, this.situacao,
				firstResult, maxResult, orderProperty, asc);
	}
	
	public String obterDescricaoTruncada(String descricao) {
		if (descricao.length() > 30) {
			return StringUtils.substring(descricao, 0, 30).concat("...");
		}
		return descricao;
	}
	
	public String obterUnidadeTruncada(AghUnidadesFuncionais unidade) {
		String descricaoCompleta = unidade.getUnidadeDescricao();
		if (descricaoCompleta.length() > 30) {
			return StringUtils.substring(descricaoCompleta, 0, 30).concat("...");
		}
		return descricaoCompleta;
	}

	public void excluir() {
		try {
			if (this.itemSelecionado != null) {
				String descricao = this.itemSelecionado.getDescricao();
				this.procedimentoTerapeuticoFacade.excluirMptTipoSessao(this.itemSelecionado);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TIPO_SESSAO",
						descricao);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparCampos() {
		this.descricao = null;
		this.unidadeFuncional = null;
		this.situacao = null;
		this.dataModel.limparPesquisa();
	}

	public String inserir() {
		this.cadastraTiposSessaoController.setEdicaoAtiva(false);
		return PAGE_CADASTRA_TIPOS_SESSAO;
	}

	public String editar() {
		this.cadastraTiposSessaoController.setEdicaoAtiva(true);
		return PAGE_CADASTRA_TIPOS_SESSAO;
	}
	
	public String historicoSessao(){
		return PAGE_VISUALIZAR_HISTORICO_SESSAO;
	}

	public String historicoSessaoTeste(Short sessaoSeq){
		visualizaHistoricoTipoSessaoPaginatorController.setSessaoSeq(sessaoSeq);
		return PAGE_VISUALIZAR_HISTORICO_SESSAO;
	}
	// getters & setters
	
	public DynamicDataModel<MptTipoSessao> getDataModel() {
		return dataModel;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public void setDataModel(DynamicDataModel<MptTipoSessao> dataModel) {
		this.dataModel = dataModel;
	}

	public MptTipoSessao getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MptTipoSessao itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public CadastraTiposSessaoController getCadastraTiposSessaoController() {
		return cadastraTiposSessaoController;
	}


	public void setCadastraTiposSessaoController(
			CadastraTiposSessaoController cadastraTiposSessaoController) {
		this.cadastraTiposSessaoController = cadastraTiposSessaoController;
	}
	public VisualizaHistoricoTipoSessaoPaginatorController getVisualizaHistoricoTipoSessaoPaginatorController() {
		return visualizaHistoricoTipoSessaoPaginatorController;
	}

	public void setVisualizaHistoricoTipoSessaoPaginatorController(
			VisualizaHistoricoTipoSessaoPaginatorController visualizaHistoricoTipoSessaoPaginatorController) {
		this.visualizaHistoricoTipoSessaoPaginatorController = visualizaHistoricoTipoSessaoPaginatorController;
	}
}
