package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTextoPadraoLaudo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTextoPadraoLaudoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6806503339380073715L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private Integer seqTextoPadraoLaudo;

	private String descricaoTextoPadraoLaudo;

	private DominioSituacao situacaoTextoPadraoLaudo;

	private boolean exibirBotaoIncluirTextoPadraoLaudo = false;

	private Integer seqAlterarSituacao;

	private MpmTextoPadraoLaudo textoPadraoLaudo = new MpmTextoPadraoLaudo();

	private boolean editandoTextoPadraoLaudo = false;

	private String cameFrom;

	private boolean dirty;
	
	private MpmTextoPadraoLaudo textoPadraoSelecionado;
	
	@Inject @Paginator
	private DynamicDataModel<MpmTextoPadraoLaudo> dataModel;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		this.dataModel.reiniciarPaginator();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirTextoPadraoLaudo = true;
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.seqTextoPadraoLaudo = null;
		this.descricaoTextoPadraoLaudo = null;
		this.situacaoTextoPadraoLaudo = null;
		this.exibirBotaoIncluirTextoPadraoLaudo = false;
		this.textoPadraoLaudo = new MpmTextoPadraoLaudo();
		this.editandoTextoPadraoLaudo = false;
		this.dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade.listarTextoPadraoLaudoCount(this.seqTextoPadraoLaudo, this.descricaoTextoPadraoLaudo,
				this.situacaoTextoPadraoLaudo);
	}

	@Override
	public List<MpmTextoPadraoLaudo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.prescricaoMedicaFacade.listarTextoPadraoLaudo(firstResult, maxResult,
				MpmTextoPadraoLaudo.Fields.DESCRICAO.toString(), true, this.seqTextoPadraoLaudo, this.descricaoTextoPadraoLaudo,
				this.situacaoTextoPadraoLaudo);
	}

	public void editarTextoPadraoLaudo(Integer seq) {
		this.editandoTextoPadraoLaudo = true;
		this.textoPadraoLaudo = this.prescricaoMedicaFacade.obterMpmTextoPadraoLaudoPorChavePrimaria(seq);
	}

	public void salvarTextoPadraoLaudo() {
		this.setDirty(true);

		try {
			this.prescricaoMedicaFacade.inserirMpmTextoPadraoLaudo(this.textoPadraoLaudo, true);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TEXTO_PADRAO_LAUDO",
					this.textoPadraoLaudo.getDescricao());
			
			this.cancelaEdicaoTextoPadraoLaudo();
			this.setDirty(true);
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	public void alterarTextoPadraoLaudo() {
		this.setDirty(true);

		try {
			this.prescricaoMedicaFacade.atualizarMpmTextoPadraoLaudo(this.textoPadraoLaudo, true);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TEXTO_PADRAO_LAUDO",
					this.textoPadraoLaudo.getDescricao());
			
			this.cancelaEdicaoTextoPadraoLaudo();
			this.setDirty(true);
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelaEdicaoTextoPadraoLaudo() {
		this.textoPadraoLaudo = new MpmTextoPadraoLaudo();
		this.editandoTextoPadraoLaudo = false;
	}

	public void ativarDesativarTextoPadraoLaudo() {
		if (this.seqAlterarSituacao != null) {
			MpmTextoPadraoLaudo textoPadraoLaudo = this.prescricaoMedicaFacade
					.obterMpmTextoPadraoLaudoPorChavePrimaria(seqAlterarSituacao);

			if (textoPadraoLaudo != null) {
				try {
					DominioSituacao situacaoAtual = textoPadraoLaudo.getIndSituacao();

					if (DominioSituacao.A.equals(situacaoAtual)) {
						textoPadraoLaudo.setIndSituacao(DominioSituacao.I);
					} else {
						textoPadraoLaudo.setIndSituacao(DominioSituacao.A);
					}

					this.prescricaoMedicaFacade.atualizarMpmTextoPadraoLaudo(textoPadraoLaudo, true);
					this.cancelaEdicaoTextoPadraoLaudo();
					this.setDirty(true);

					this.dataModel.reiniciarPaginator();
					apresentarMsgNegocio(Severity.INFO,
							"MENSAGEM_ATIVAR_DESATIVAR_TEXTO_PADRAO_LAUDO_SUCESSO",
							(DominioSituacao.A.equals(situacaoAtual) ? "desativado" : "ativado"));
				} catch (BaseException e) {
					e.getMessage();
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}

	public String cancelar() {
		return cameFrom;
	}

	public Integer getSeqTextoPadraoLaudo() {
		return seqTextoPadraoLaudo;
	}

	public void setSeqTextoPadraoLaudo(Integer seqTextoPadraoLaudo) {
		this.seqTextoPadraoLaudo = seqTextoPadraoLaudo;
	}

	public String getDescricaoTextoPadraoLaudo() {
		return descricaoTextoPadraoLaudo;
	}

	public void setDescricaoTextoPadraoLaudo(String descricaoTextoPadraoLaudo) {
		this.descricaoTextoPadraoLaudo = descricaoTextoPadraoLaudo;
	}

	public DominioSituacao getSituacaoTextoPadraoLaudo() {
		return situacaoTextoPadraoLaudo;
	}

	public void setSituacaoTextoPadraoLaudo(DominioSituacao situacaoTextoPadraoLaudo) {
		this.situacaoTextoPadraoLaudo = situacaoTextoPadraoLaudo;
	}

	public boolean isExibirBotaoIncluirTextoPadraoLaudo() {
		return exibirBotaoIncluirTextoPadraoLaudo;
	}

	public void setExibirBotaoIncluirTextoPadraoLaudo(boolean exibirBotaoIncluirTextoPadraoLaudo) {
		this.exibirBotaoIncluirTextoPadraoLaudo = exibirBotaoIncluirTextoPadraoLaudo;
	}

	public Integer getSeqAlterarSituacao() {
		return seqAlterarSituacao;
	}

	public void setSeqAlterarSituacao(Integer seqAlterarSituacao) {
		this.seqAlterarSituacao = seqAlterarSituacao;
	}

	public MpmTextoPadraoLaudo getTextoPadraoLaudo() {
		return textoPadraoLaudo;
	}

	public void setTextoPadraoLaudo(MpmTextoPadraoLaudo textoPadraoLaudo) {
		this.textoPadraoLaudo = textoPadraoLaudo;
	}

	public boolean isEditandoTextoPadraoLaudo() {
		return editandoTextoPadraoLaudo;
	}

	public void setEditandoTextoPadraoLaudo(boolean editandoTextoPadraoLaudo) {
		this.editandoTextoPadraoLaudo = editandoTextoPadraoLaudo;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public DynamicDataModel<MpmTextoPadraoLaudo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmTextoPadraoLaudo> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public MpmTextoPadraoLaudo getTextoPadraoSelecionado() {
		return textoPadraoSelecionado;
	}

	public void setTextoPadraoSelecionado(MpmTextoPadraoLaudo textoPadraoSelecionado) {
		this.textoPadraoSelecionado = textoPadraoSelecionado;
	}

}
