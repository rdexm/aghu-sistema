package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.util.Date;

public class DadosInternacaoVO {
	private Date dataInternacao;
	private Byte codigoPlanoSaude;
	private Short codigoConvenioPlano;
	private String descricaoConvenioPlanoSaude;
	private Integer codigoInternacao;
	
	
	public Date getDataInternacao() {
		return dataInternacao;
	}
	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}
	public Byte getCodigoPlanoSaude() {
		return codigoPlanoSaude;
	}
	public void setCodigoPlanoSaude(Byte codigoPlanoSaude) {
		this.codigoPlanoSaude = codigoPlanoSaude;
	}
	public Short getCodigoConvenioPlano() {
		return codigoConvenioPlano;
	}
	public void setCodigoConvenioPlano(Short codigoConvenioPlano) {
		this.codigoConvenioPlano = codigoConvenioPlano;
	}
	public String getDescricaoConvenioPlanoSaude() {
		return descricaoConvenioPlanoSaude;
	}
	public void setDescricaoConvenioPlanoSaude(String descricaoConvenioPlanoSaude) {
		this.descricaoConvenioPlanoSaude = descricaoConvenioPlanoSaude;
	}
	public void setCodigoInternacao(Integer codigoInternacao) {
		this.codigoInternacao = codigoInternacao;
	}
	public Integer getCodigoInternacao() {
		return codigoInternacao;
	}
	
	
}
