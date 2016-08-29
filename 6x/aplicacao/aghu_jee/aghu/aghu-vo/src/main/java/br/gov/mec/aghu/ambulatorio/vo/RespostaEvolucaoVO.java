package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class RespostaEvolucaoVO implements Serializable {

	private static final long serialVersionUID = 1973453830524934530L;
	
	private Integer pacCodigo;
	private Integer conNumero;
	private Short fueSeq;	
	private String textoFormatado;
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Short seqp;
	private Integer vvqQusQutSeq;
	private Short vvqQusSeqp;
	private Short vvqSeqp;
	private String resposta;
	
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	public Short getFueSeq() {
		return fueSeq;
	}
	public void setFueSeq(Short fueSeq) {
		this.fueSeq = fueSeq;
	}
	public String getTextoFormatado() {
		return textoFormatado;
	}
	public void setTextoFormatado(String textoFormatado) {
		this.textoFormatado = textoFormatado;
	}
	public Integer getQusQutSeq() {
		return qusQutSeq;
	}
	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}
	public Short getQusSeqp() {
		return qusSeqp;
	}
	public void setQusSeqp(Short qusSeqp) {
		this.qusSeqp = qusSeqp;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Integer getVvqQusQutSeq() {
		return vvqQusQutSeq;
	}
	public void setVvqQusQutSeq(Integer vvqQusQutSeq) {
		this.vvqQusQutSeq = vvqQusQutSeq;
	}
	public Short getVvqQusSeqp() {
		return vvqQusSeqp;
	}
	public void setVvqQusSeqp(Short vvqQusSeqp) {
		this.vvqQusSeqp = vvqQusSeqp;
	}
	public Short getVvqSeqp() {
		return vvqSeqp;
	}
	public void setVvqSeqp(Short vvqSeqp) {
		this.vvqSeqp = vvqSeqp;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	
	
}
