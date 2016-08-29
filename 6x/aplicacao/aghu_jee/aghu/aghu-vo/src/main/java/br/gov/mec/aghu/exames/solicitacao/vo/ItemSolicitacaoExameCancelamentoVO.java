package br.gov.mec.aghu.exames.solicitacao.vo;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

public class ItemSolicitacaoExameCancelamentoVO {
	
	private AelItemSolicitacaoExames aelItemSolicitacaoExames;
	
	/**
	 * Utilizado na tela de Cancelamento de exames de responsabilidade do solicitante ou responsável.
	 * Quando o usuario marcar o item para exclusao.
	 */
	private Boolean excluir;

	public void setAelItemSolicitacaoExames(AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		this.aelItemSolicitacaoExames = aelItemSolicitacaoExames;
	}

	public AelItemSolicitacaoExames getAelItemSolicitacaoExames() {
		return aelItemSolicitacaoExames;
	}

	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}

	public Boolean getExcluir() {
		return excluir;
	}

}
