package br.gov.mec.aghu.business;

import java.util.Date;

public class AgendamentoJobVO {
	
	private Integer seq;
	private String nomeTarefa;
	private String nomeGrupo;
	private Date proximoAgendamento;
	private Integer quantTriggers;
	private String jobDataMapAsync;
	
	
	
	public String getNomeCompleto() {
		return this.getNomeGrupo() + "." + this.getNomeTarefa();
	}
	
	
	public String getNomeTarefa() {
		return nomeTarefa;
	}
	public void setNomeTarefa(String nomeTarefa) {
		this.nomeTarefa = nomeTarefa;
	}
	public String getNomeGrupo() {
		return nomeGrupo;
	}
	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}
	public Date getProximoAgendamento() {
		return proximoAgendamento;
	}
	public void setProximoAgendamento(Date proximoAgendamento) {
		this.proximoAgendamento = proximoAgendamento;
	}
	public Integer getQuantTriggers() {
		return quantTriggers;
	}
	public void setQuantTriggers(Integer quantTriggers) {
		this.quantTriggers = quantTriggers;
	}
	public String getJobDataMapAsync() {
		return jobDataMapAsync;
	}
	public void setJobDataMapAsync(String jobDataMapAsync) {
		this.jobDataMapAsync = jobDataMapAsync;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public Integer getSeq() {
		return seq;
	}

}
