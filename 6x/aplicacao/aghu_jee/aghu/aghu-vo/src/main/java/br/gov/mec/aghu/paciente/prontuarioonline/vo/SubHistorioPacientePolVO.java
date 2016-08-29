package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

public class SubHistorioPacientePolVO {
	
	private Date data;
	private Date dataFim;
	private String descricao;
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	
}
