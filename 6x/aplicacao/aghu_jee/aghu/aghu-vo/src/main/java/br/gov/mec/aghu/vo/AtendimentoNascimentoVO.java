package br.gov.mec.aghu.vo;

import java.util.Date;

public class AtendimentoNascimentoVO {

	private Integer gsoPacCodigo; 
	private Short gsoSeqp;
	private Integer pacCodigo;
	private Integer internacaoSeq;
	private Date dataInicio;
	private Integer equipeSeq; 
	private String tipo; 
	private String rnClassificacao; 
	private Date dataNascimento;
	
	public AtendimentoNascimentoVO(){}
	
	public AtendimentoNascimentoVO(Integer gsoPacCodigo, Short gsoSeqp,
			Integer pacCodigo, Integer internacaoSeq, Date dataInicio,
			Integer equipeSeq, String tipo, String rnClassificacao,
			Date dataNascimento) {
		super();
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.pacCodigo = pacCodigo;
		this.internacaoSeq = internacaoSeq;
		this.dataInicio = dataInicio;
		this.equipeSeq = equipeSeq;
		this.tipo = tipo;
		this.rnClassificacao = rnClassificacao;
		this.dataNascimento = dataNascimento;
	}

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Integer getEquipeSeq() {
		return equipeSeq;
	}

	public void setEquipeSeq(Integer equipeSeq) {
		this.equipeSeq = equipeSeq;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getRnClassificacao() {
		return rnClassificacao;
	}

	public void setRnClassificacao(String rnClassificacao) {
		this.rnClassificacao = rnClassificacao;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
}
