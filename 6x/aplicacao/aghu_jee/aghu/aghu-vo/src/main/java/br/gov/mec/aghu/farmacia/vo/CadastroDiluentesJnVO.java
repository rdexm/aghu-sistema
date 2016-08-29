package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.util.Date;



public class CadastroDiluentesJnVO implements Serializable {

	private static final long serialVersionUID = 4072196797243152579L;
	
	private Integer seq;
	private String seqMedicamento;
	private String descricao;
	private Boolean usualPrescricao;
	private String situacao;
	private String operacao;
	private String nomeUsuario;
	private Date dataAlteracao;
	private Date criadoEm;
	private String nomeResponsavel;

	
	
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

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	
}
