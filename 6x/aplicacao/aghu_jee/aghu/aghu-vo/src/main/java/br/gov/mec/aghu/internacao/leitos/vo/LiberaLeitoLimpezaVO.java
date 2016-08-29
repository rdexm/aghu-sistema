package br.gov.mec.aghu.internacao.leitos.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class LiberaLeitoLimpezaVO implements BaseBean {

	private static final long serialVersionUID = -859570281595234297L;

	private String ltoId;
	private String andarAlaDescricao;
	private Integer intSeq;
	private String dataBloqueio;
	private String situacao;
	private Date dataBloqueioOrdenacao;

	// GETS AND SETS
	public String getLtoId() {
		return ltoId;
	}

	public void setLtoId(String ltoId) {
		this.ltoId = ltoId;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public String getDataBloqueio() {
		return dataBloqueio;
	}

	public void setDataBloqueio(String dataBloqueio) {
		this.dataBloqueio = dataBloqueio;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Date getDataBloqueioOrdenacao() {
		return dataBloqueioOrdenacao;
	}

	public void setDataBloqueioOrdenacao(Date dataBloqueioOrdenacao) {
		this.dataBloqueioOrdenacao = dataBloqueioOrdenacao;
	}
}
