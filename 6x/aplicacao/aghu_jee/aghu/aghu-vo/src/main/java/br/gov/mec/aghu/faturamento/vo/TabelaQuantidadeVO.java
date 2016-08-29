package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class TabelaQuantidadeVO {

	private Integer pacCodigo;
	private Integer phoSeq;
	private Integer iphSeq;
	private Date dtRealiz;
	private Integer qtd;
	private Integer qtdInsDia;
	private Integer qtdInsMes;
	private String tipo;
	
	public TabelaQuantidadeVO() {
	}
	
	public TabelaQuantidadeVO(Integer pacCodigo, Integer phoSeq,
			Integer iphSeq, Date dtRealiz, Integer qtd, Integer qtdInsDia,
			Integer qtdInsMes, String tipo) {
		super();
		this.pacCodigo = pacCodigo;
		this.phoSeq = phoSeq;
		this.iphSeq = iphSeq;
		this.dtRealiz = dtRealiz;
		this.qtd = qtd;
		this.qtdInsDia = qtdInsDia;
		this.qtdInsMes = qtdInsMes;
		this.tipo = tipo;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getPhoSeq() {
		return phoSeq;
	}
	public void setPhoSeq(Integer phoSeq) {
		this.phoSeq = phoSeq;
	}
	public Integer getIphSeq() {
		return iphSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	public Date getDtRealiz() {
		return dtRealiz;
	}
	public void setDtRealiz(Date dtRealiz) {
		this.dtRealiz = dtRealiz;
	}
	public Integer getQtd() {
		return qtd;
	}
	public void setQtd(Integer qtd) {
		this.qtd = qtd;
	}
	public Integer getQtdInsDia() {
		return qtdInsDia;
	}
	public void setQtdInsDia(Integer qtdInsDia) {
		this.qtdInsDia = qtdInsDia;
	}
	public Integer getQtdInsMes() {
		return qtdInsMes;
	}
	public void setQtdInsMes(Integer qtdInsMes) {
		this.qtdInsMes = qtdInsMes;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}
