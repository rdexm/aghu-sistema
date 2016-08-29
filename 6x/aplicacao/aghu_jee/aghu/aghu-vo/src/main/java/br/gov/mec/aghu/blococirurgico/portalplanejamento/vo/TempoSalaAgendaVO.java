package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;

import br.gov.mec.aghu.core.exception.Severity;


public class TempoSalaAgendaVO {

	private Date tempo;
	private Severity info;
	private String mensagem;
	private String tempoSalaFormatada;
	private String dataFormatada;
	private String descricaoProcedimento;
	
	public Date getTempo() {
		return tempo;
	}
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	public Severity getInfo() {
		return info;
	}
	public void setInfo(Severity info) {
		this.info = info;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public String getTempoSalaFormatada() {
		return tempoSalaFormatada;
	}
	public void setTempoSalaFormatada(String tempoSalaFormatada) {
		this.tempoSalaFormatada = tempoSalaFormatada;
	}
	public String getDataFormatada() {
		return dataFormatada;
	}
	public void setDataFormatada(String dataFormatada) {
		this.dataFormatada = dataFormatada;
	}
	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}
	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}
}
