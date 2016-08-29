package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndRespProc;

public class MbcCirurgiaVO {

	private Integer atdSeq;
	private Date dataFimCirg;
	private Byte quantidade;
	private Integer phiSeq;
	private Integer pacCodigo;
	private Integer crgSeq;
	private Short eprEspSeq;
	private Integer eprPciSeq;
	private DominioIndRespProc indRespProc;
	private Short unfSeq;
	private Date data;
	private Integer rmpSeq;
	private Short numero;
	private Integer quantidadeIps;
	
	
	public Date getDataFimCirg() {
		return dataFimCirg;
	}

	public void setDataFimCirg(Date dataFimCirg) {
		this.dataFimCirg = dataFimCirg;
	}

	public Byte getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Byte quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Short getEprEspSeq() {
		return eprEspSeq;
	}

	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}

	public Integer getEprPciSeq() {
		return eprPciSeq;
	}

	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}

	public DominioIndRespProc getIndRespProc() {
		return indRespProc;
	}

	public void setIndRespProc(DominioIndRespProc indRespProc) {
		this.indRespProc = indRespProc;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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

	public Integer getQuantidadeIps() {
		return quantidadeIps;
	}

	public void setQuantidadeIps(Integer quantidadeIps) {
		this.quantidadeIps = quantidadeIps;
	}

}
