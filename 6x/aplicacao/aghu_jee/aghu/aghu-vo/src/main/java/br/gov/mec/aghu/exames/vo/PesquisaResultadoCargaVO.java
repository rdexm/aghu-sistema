package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaResultadoCargaVO implements BaseBean {
	
	private static final long serialVersionUID = -5995662557216812781L;

	private Date dataHora;
	private String tipoComunicacao;
	private Integer solicitacao;
	private Short amostra;
	private String observacao;

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getTipoComunicacao() {
		return tipoComunicacao;
	}

	public void setTipoComunicacao(String tipoComunicacao) {
		this.tipoComunicacao = tipoComunicacao;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Short getAmostra() {
		return amostra;
	}

	public void setAmostra(Short amostra) {
		this.amostra = amostra;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}