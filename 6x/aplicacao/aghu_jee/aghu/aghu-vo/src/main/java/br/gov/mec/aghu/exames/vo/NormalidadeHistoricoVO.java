package br.gov.mec.aghu.exames.vo;

import java.util.Date;



public class NormalidadeHistoricoVO implements java.io.Serializable{

	private static final long serialVersionUID = -465415034975272711L;
	private Integer soeSeq;
	private Short seqp;
	private String resultado;
	private String descricao;
	private Date dataEvento;
	
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Date getDataEvento() {
		return dataEvento;
	}
	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}	
	
	
}