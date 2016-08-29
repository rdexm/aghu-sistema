package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;


public class FichaFarmacoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4687960870667449404L;
	
	private Integer seq;
	private Long ficSeq;
	private Boolean infusao;
	private String vadSigla;	
	private String medicamento;
	private String doseTotal;
	
	//Getters e setters
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Long getFicSeq() {
		return ficSeq;
	}
	public void setFicSeq(Long ficSeq) {
		this.ficSeq = ficSeq;
	}
	public Boolean getInfusao() {
		return infusao;
	}
	public void setInfusao(Boolean infusao) {
		this.infusao = infusao;
	}
	public String getVadSigla() {
		return vadSigla;
	}
	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}
	public String getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}
	public String getDoseTotal() {
		return doseTotal;
	}
	public void setDoseTotal(String doseTotal) {
		this.doseTotal = doseTotal;
	}

}
