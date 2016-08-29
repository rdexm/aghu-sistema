package br.gov.mec.aghu.compras.autfornecimento.vo;

import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AutorizacaoFornecimentoEmailAtrasoVO implements BaseBean {
	
	private static final long serialVersionUID = 8320531999093424311L;

	private String labelEnviarFornecedor;
	private String labelEnviarUsuarioLogado;
	private String labelEnviarCopia;
	private String labelAnexarPlanilha;
	private ScoContatoFornecedor contatoFornecedor;
	private Boolean exibirModal;

	public String getLabelEnviarFornecedor() {
		return labelEnviarFornecedor;
	}

	public void setLabelEnviarFornecedor(String labelEnviarFornecedor) {
		this.labelEnviarFornecedor = labelEnviarFornecedor;
	}

	public String getLabelEnviarUsuarioLogado() {
		return labelEnviarUsuarioLogado;
	}

	public void setLabelEnviarUsuarioLogado(String labelEnviarUsuarioLogado) {
		this.labelEnviarUsuarioLogado = labelEnviarUsuarioLogado;
	}

	public String getLabelEnviarCopia() {
		return labelEnviarCopia;
	}

	public void setLabelEnviarCopia(String labelEnviarCopia) {
		this.labelEnviarCopia = labelEnviarCopia;
	}

	public String getLabelAnexarPlanilha() {
		return labelAnexarPlanilha;
	}

	public void setLabelAnexarPlanilha(String labelAnexarPlanilha) {
		this.labelAnexarPlanilha = labelAnexarPlanilha;
	}

	public ScoContatoFornecedor getContatoFornecedor() {
		return contatoFornecedor;
	}

	public void setContatoFornecedor(ScoContatoFornecedor contatoFornecedor) {
		this.contatoFornecedor = contatoFornecedor;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}
}
