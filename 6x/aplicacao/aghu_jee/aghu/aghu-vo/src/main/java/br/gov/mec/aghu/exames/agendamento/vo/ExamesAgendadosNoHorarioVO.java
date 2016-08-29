package br.gov.mec.aghu.exames.agendamento.vo;

import java.util.Date;

public class ExamesAgendadosNoHorarioVO {
	
	private Short hedGaeUnfSeq;
	private Integer hedGaeSeqp;
	private Date hedDthrAgenda;
	private Integer nroSolicitacao;
	private Short item;
	private String situacao;
	private String descricaoSituacao;
	private String exame;
	private Integer amostra;
	private Short etapa;
	
	
	public Short getHedGaeUnfSeq() {
		return hedGaeUnfSeq;
	}
	public void setHedGaeUnfSeq(Short hedGaeUnfSeq) {
		this.hedGaeUnfSeq = hedGaeUnfSeq;
	}
	public Integer getHedGaeSeqp() {
		return hedGaeSeqp;
	}
	public void setHedGaeSeqp(Integer hedGaeSeqp) {
		this.hedGaeSeqp = hedGaeSeqp;
	}
	public Date getHedDthrAgenda() {
		return hedDthrAgenda;
	}
	public void setHedDthrAgenda(Date hedDthrAgenda) {
		this.hedDthrAgenda = hedDthrAgenda;
	}
	public Integer getNroSolicitacao() {
		return nroSolicitacao;
	}
	public void setNroSolicitacao(Integer nroSolicitacao) {
		this.nroSolicitacao = nroSolicitacao;
	}
	public Short getItem() {
		return item;
	}
	public void setItem(Short item) {
		this.item = item;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getDescricaoSituacao() {
		return descricaoSituacao;
	}
	public void setDescricaoSituacao(String descricaoSituacao) {
		this.descricaoSituacao = descricaoSituacao;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	public Integer getAmostra() {
		return amostra;
	}
	public void setAmostra(Integer amostra) {
		this.amostra = amostra;
	}
	public Short getEtapa() {
		return etapa;
	}
	public void setEtapa(Short etapa) {
		this.etapa = etapa;
	}

}
