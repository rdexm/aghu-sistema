package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioJustificativa;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamTipoSolProcedimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class ManterTipoSolicitacaoProcedimentosPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MamTipoSolProcedimento> dataModel;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6816149777877487287L;
	
	private static final String INCLUIR_TIPO_SOLICITACAO_PROCEDIMENTO = "ambulatorio-incluirTipoSolicitacaoProcedimentos";
	
	private static final String EDITAR_TIPO_SOLICITACAO_PROCEDIMENTO = "ambulatorio-incluirTipoSolicitacaoProcedimentos";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private boolean exibirBotaoIncluirTipoSolicitacaooProcedimentos;
	
	private Short seq;
	private String descricao;
	private DominioSituacao situacao;
	private DominioJustificativa dominioJustificativa;
	
	private MamTipoSolProcedimento tipoSolicitacaoProcedimento = new MamTipoSolProcedimento();
	private MamTipoSolProcedimento parametroSelecionado;
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirTipoSolicitacaooProcedimentos = true;
	}
	
	public void limparPesquisa() {
		this.seq = null;
		this.descricao = null;
		this.situacao = null;
		this.dominioJustificativa = null;
		this.exibirBotaoIncluirTipoSolicitacaooProcedimentos = false;
		this.tipoSolicitacaoProcedimento = new MamTipoSolProcedimento();
		this.dataModel.setPesquisaAtiva(false);
		this.dataModel.limparPesquisa();
	}
	
	public String iniciarInclusao() {
		return INCLUIR_TIPO_SOLICITACAO_PROCEDIMENTO;
	}
	
	public String iniciarEdicao(){
		return EDITAR_TIPO_SOLICITACAO_PROCEDIMENTO;
	}
	
	
	public void excluir() {
		try {
			this.ambulatorioFacade.excluirTipoSolicitacaoProcedimentos(parametroSelecionado.getSeq());
			
			this.dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio(br.gov.mec.aghu.core.exception.Severity.INFO, "MSG_EXCLUIDO", getBundle().getString("LABEL_TIPO_SOLICITACAO_PROCEDIMENTOS"));
			this.tipoSolicitacaoProcedimento = new MamTipoSolProcedimento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade.countTipoSolicitacaoProcedimentosPaginado(tipoSolicitacaoProcedimento);
	}
	
	@Override
	public List<MamTipoSolProcedimento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.ambulatorioFacade.pesquisarTipoSolicitacaoProcedimentosPaginado(firstResult, maxResult, orderProperty, asc, tipoSolicitacaoProcedimento);
	}
	
	public boolean isExibirBotaoIncluirTipoSolicitacaooProcedimentos() {
		return exibirBotaoIncluirTipoSolicitacaooProcedimentos;
	}
	
	public void setExibirBotaoIncluirTipoSolicitacaooProcedimentos(boolean exibirBotaoIncluirTipoSolicitacaooProcedimentos) {
		this.exibirBotaoIncluirTipoSolicitacaooProcedimentos = exibirBotaoIncluirTipoSolicitacaooProcedimentos;
	}
	
	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
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
	
	public DominioJustificativa getDominioJustificativa() {
		return dominioJustificativa;
	}
	
	public void setDominioJustificativa(DominioJustificativa dominioJustificativa) {
		this.dominioJustificativa = dominioJustificativa;
	}
	
	public MamTipoSolProcedimento getTipoSolicitacaoProcedimento() {
		return tipoSolicitacaoProcedimento;
	}
	
	public void setTipoSolicitacaoProcedimento(MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		this.tipoSolicitacaoProcedimento = tipoSolicitacaoProcedimento;
	}

	public DynamicDataModel<MamTipoSolProcedimento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamTipoSolProcedimento> dataModel) {
	 this.dataModel = dataModel;
	}

	public MamTipoSolProcedimento getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MamTipoSolProcedimento parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
