package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.List;

public class ValidaConfirmacaoRecebimentoVO implements Serializable {

	private static final long serialVersionUID = -7559840369353444128L;

	private List<ItemRecebimentoProvisorioVO> listaItensComPendencia;
	private Boolean valorForaPercentualVariacao;
	private boolean valorForaPercentualVariacaoItens;
	private String mensagemModal;
	private boolean validarEsl;

	public ValidaConfirmacaoRecebimentoVO() {

	}

	public List<ItemRecebimentoProvisorioVO> getListaItensComPendencia() {
		return listaItensComPendencia;
	}

	public void setListaItensComPendencia(List<ItemRecebimentoProvisorioVO> listaItensComPendencia) {
		this.listaItensComPendencia = listaItensComPendencia;
	}

	public Boolean getValorForaPercentualVariacao() {
		return valorForaPercentualVariacao;
	}

	public void setValorForaPercentualVariacao(Boolean valorForaPercentualVariacao) {
		this.valorForaPercentualVariacao = valorForaPercentualVariacao;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public boolean isValorForaPercentualVariacaoItens() {
		return valorForaPercentualVariacaoItens;
	}

	public void setValorForaPercentualVariacaoItens(boolean valorForaPercentualVariacaoItens) {
		this.valorForaPercentualVariacaoItens = valorForaPercentualVariacaoItens;
	}

	public boolean isValidarEsl() {
		return validarEsl;
	}

	public void setValidarEsl(boolean validarEsl) {
		this.validarEsl = validarEsl;
	}

}
