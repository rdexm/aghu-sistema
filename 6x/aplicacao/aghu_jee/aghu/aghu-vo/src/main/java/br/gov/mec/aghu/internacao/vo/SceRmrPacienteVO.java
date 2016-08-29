package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

public class SceRmrPacienteVO {
	
	private Integer crgSeq;
	private Integer phiSeq;
	private Integer intSeq;
	private Date dtUtilizacao;
	
	private Integer rmpSeq;
	private Short numero;
	private Integer quantidade;	
	
	private Double valorUnitario;
	
	//############
	//Construtores
	//############	
	public SceRmrPacienteVO() {
	
	}
	
	public SceRmrPacienteVO(Integer crgSeq, Integer phiSeq, Integer intSeq, Date dtUtilizacao) {
		this.crgSeq = crgSeq;
		this.phiSeq = phiSeq;
		this.intSeq = intSeq;
		this.dtUtilizacao = dtUtilizacao;
	}
	
	
	//#################
	//Getters e Setters
	//#################	
	public Integer getCrgSeq() {
		return crgSeq;
	}
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	public Integer getIntSeq() {
		return intSeq;
	}
	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}
	
	public Date getDtUtilizacao() {
		return dtUtilizacao;
	}
	public void setDtUtilizacao(Date dtUtilizacao) {
		this.dtUtilizacao = dtUtilizacao;
	}
	
	//######
	//Fields
	//######
	public enum Fields {
		
		CRG_SEQ("crgSeq"),
		PHI_SEQ("phiSeq"),
		INT_SEQ("intSeq"),
		DT_UTILIZACAO("dtUtilizacao"),
		NUMERO("numero"), 
		RMP_SEQ("rmpSeq"),
		QUANTIDADE("quantidade");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Integer getRmpSeq() {
		return rmpSeq;
	}

	public void setRmpSeq(Integer rmpSeq) {
		this.rmpSeq = rmpSeq;
	}

	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	
}
