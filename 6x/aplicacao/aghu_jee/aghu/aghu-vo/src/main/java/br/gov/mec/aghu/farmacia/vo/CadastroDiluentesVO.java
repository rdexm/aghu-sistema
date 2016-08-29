package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;


public class CadastroDiluentesVO implements Serializable {

	private static final long serialVersionUID = 4072196797243152579L;
	
	private Integer seq;
	private String seqMedicamento;
	private String descricao;
	private Boolean usualPrescricao;
	private String situacao;

	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Boolean getUsualPrescricao() {
		return usualPrescricao;
	}
	
	public void setUsualPrescricao(Boolean usualPrescricao) {
		this.usualPrescricao = usualPrescricao;
	}
	
	public String getSituacao() {
		return situacao;
	}
	
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getSeqMedicamento() {
		return seqMedicamento;
	}

	public void setSeqMedicamento(String seqMedicamento) {
		this.seqMedicamento = seqMedicamento;
	}
	
}
