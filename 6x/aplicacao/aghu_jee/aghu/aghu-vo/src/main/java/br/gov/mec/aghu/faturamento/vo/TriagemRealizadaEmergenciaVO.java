package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class TriagemRealizadaEmergenciaVO {

	
	private Date criadoEm;
	private Integer serMatricula;
	private Integer serVinCodigo; //precisa ser integer pq tem um case na consulta que seta um nro (inteiro)
	private Short unfSeq;
	private Integer pacCodigo;
	
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
}
