package br.gov.mec.aghu.compras.vo;

import java.util.Date;

public class ImprimirJulgamentoPropostasLicitacaoRodapeVO {

	
	private String qFornecRazaoSocial; //38
	private Long qForneCgc; //39
	private Date qFornecDtValidadeFgts; //40
	private Date qFornecDtValidadeInss; //41
	private Date qFornecDtValidadeDau; //42
	private Date qFornecDtValidadeTcf; //43
	private Long qFornecCpf; //possivel valor para campo 39
	
	public String getqFornecRazaoSocial() {
		return qFornecRazaoSocial;
	}
	public void setqFornecRazaoSocial(String qFornecRazaoSocial) {
		this.qFornecRazaoSocial = qFornecRazaoSocial;
	}
	public Long getqForneCgc() {
		return qForneCgc;
	}
	public void setqForneCgc(Long qForneCgc) {
		this.qForneCgc = qForneCgc;
	}
	public Date getqFornecDtValidadeFgts() {
		return qFornecDtValidadeFgts;
	}
	public void setqFornecDtValidadeFgts(Date qFornecDtValidadeFgts) {
		this.qFornecDtValidadeFgts = qFornecDtValidadeFgts;
	}
	public Date getqFornecDtValidadeInss() {
		return qFornecDtValidadeInss;
	}
	public void setqFornecDtValidadeInss(Date qFornecDtValidadeInss) {
		this.qFornecDtValidadeInss = qFornecDtValidadeInss;
	}
	public Date getqFornecDtValidadeDau() {
		return qFornecDtValidadeDau;
	}
	public void setqFornecDtValidadeDau(Date qFornecDtValidadeDau) {
		this.qFornecDtValidadeDau = qFornecDtValidadeDau;
	}
	public Date getqFornecDtValidadeTcf() {
		return qFornecDtValidadeTcf;
	}
	public void setqFornecDtValidadeTcf(Date qFornecDtValidadeTcf) {
		this.qFornecDtValidadeTcf = qFornecDtValidadeTcf;
	}
	public Long getqFornecCpf() {
		return qFornecCpf;
	}
	public void setqFornecCpf(Long qFornecCpf) {
		this.qFornecCpf = qFornecCpf;
	}
	
}
