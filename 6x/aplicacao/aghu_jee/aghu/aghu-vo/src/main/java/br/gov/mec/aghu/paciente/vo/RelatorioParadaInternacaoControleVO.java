package br.gov.mec.aghu.paciente.vo;

import java.util.Date;


public class RelatorioParadaInternacaoControleVO {

	private String anotacoes;			//cp_anotacoes de Q_CONTROLE
	private Integer hrsControleSumario; //hrs_controle_sumario de Q_CONTROLE
	private Date dataHora;				//data_hora_controle de Q_CONTROLE
	private String sigla;				//sigla_controle de Q_CONTROLE
	private String unidade;				//unidade_controle de Q_CONTROLE
	private String descricaoControle;	//descricao_controle de Q_CONTROLE
	
	public String getAnotacoes() {
		return anotacoes;
	}
	public void setAnotacoes(String anotacoes) {
		this.anotacoes = anotacoes;
	}
	public Integer getHrsControleSumario() {
		return hrsControleSumario;
	}
	public void setHrsControleSumario(Integer hrsControleSumario) {
		this.hrsControleSumario = hrsControleSumario;
	}
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getDescricaoControle() {
		return descricaoControle;
	}
	public void setDescricaoControle(String descricaoControle) {
		this.descricaoControle = descricaoControle;
	}

}
