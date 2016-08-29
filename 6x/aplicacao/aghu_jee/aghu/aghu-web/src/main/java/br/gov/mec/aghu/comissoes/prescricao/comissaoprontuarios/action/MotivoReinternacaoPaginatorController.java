package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business.IComissaoProntuariosPrescricaoComissoesFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivoReinternacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8999756464605979348L;

	@EJB
	private IComissaoProntuariosPrescricaoComissoesFacade comissaoProntuariosPrescricaoComissoesFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MpmMotivoReinternacao> dataModel;
	
	private static final String MOTIVO_REINTERNACAO_CRUD = "motivoReinternacaoCRUD";
	
	@Inject
	private MotivoReinternacaoController motivoReinternacaoController;
	
	private MpmMotivoReinternacao selecionado;

	// FILTRO
	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return this.comissaoProntuariosPrescricaoComissoesFacade.pesquisarMotivoReinternacaoCount(new MpmMotivoReinternacao(seq, descricao, indSituacao));
	}

	@Override
	public List<MpmMotivoReinternacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.comissaoProntuariosPrescricaoComissoesFacade.pesquisarMotivoReinternacao(firstResult, maxResult,
				MpmMotivoReinternacao.Fields.SEQ.toString(), true, new MpmMotivoReinternacao(seq, descricao,indSituacao));
	}

	public String prepararInclusao(){
		motivoReinternacaoController.iniciar();
		return MOTIVO_REINTERNACAO_CRUD;
	}
	
	public String prepararEdicao(){
		motivoReinternacaoController.iniciarEdicao(selecionado);
		return MOTIVO_REINTERNACAO_CRUD;
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de sinônimo de medicamento
	 */
//	@Restrict("#{s:hasPermission('motivoReinternacao','excluir')}")
	public void excluir(){
		try {

			dataModel.reiniciarPaginator();
			
			this.comissaoProntuariosPrescricaoComissoesFacade.removerMotivoReinternacao(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MOTIVO_REINTERNACAO", selecionado.getDescricao());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
//	@Restrict("#{s:hasPermission('motivoReinternacao','pesquisar')}")
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.seq = null;
		this.descricao = null;
		this.indSituacao = null;
		dataModel.limparPesquisa();
	}

	public Integer getSeq() {
		return seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public DynamicDataModel<MpmMotivoReinternacao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmMotivoReinternacao> dataModel) {
	 this.dataModel = dataModel;
	}

	public MpmMotivoReinternacao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MpmMotivoReinternacao selecionado) {
		this.selecionado = selecionado;
	}
}