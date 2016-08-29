package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;


public class RnCthcBuscaContaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3015095207748667523L;

	private Integer cthSeq;

	private DominioSituacaoConta indSituacao;

	private String mensagem;
	
	private String tipoMensagem;
	
	private Boolean retorno;

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public DominioSituacaoConta getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoConta indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

	public String getTipoMensagem() {
		return tipoMensagem;
	}

	public void setTipoMensagem(String tipoMensagem) {
		this.tipoMensagem = tipoMensagem;
	}
	

}
