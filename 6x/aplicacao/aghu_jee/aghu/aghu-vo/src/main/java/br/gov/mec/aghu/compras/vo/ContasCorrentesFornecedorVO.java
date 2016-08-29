package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class ContasCorrentesFornecedorVO implements Serializable {
	
	private static final long serialVersionUID = -7620204295459637684L;

	private Integer fornecedorNumero;
	private Long fornecedorCGC;
	private Long fornecedorCPF;
	private String fornecedorRazaoSocial;
	private Integer bancoCodigo;
	private String bancoNome;
	private Integer agenciaCodigo;
	private String agenciaDescricao;
	private String contaCorrente;
	private DominioSimNao indPreferencial;
	private boolean selectedPref;
	
	public Integer getFornecedorNumero() {
		return fornecedorNumero;
	}
	public void setFornecedorNumero(Integer fornecedorNumero) {
		this.fornecedorNumero = fornecedorNumero;
	}
	public Long getFornecedorCGC() {
		return fornecedorCGC;
	}
	public void setFornecedorCGC(Long fornecedorCGC) {
		this.fornecedorCGC = fornecedorCGC;
	}
	public Long getFornecedorCPF() {
		return fornecedorCPF;
	}
	public void setFornecedorCPF(Long fornecedorCPF) {
		this.fornecedorCPF = fornecedorCPF;
	}
	public String getFornecedorRazaoSocial() {
		return fornecedorRazaoSocial;
	}
	public void setFornecedorRazaoSocial(String fornecedorRazaoSocial) {
		this.fornecedorRazaoSocial = fornecedorRazaoSocial;
	}
	public Integer getBancoCodigo() {
		return bancoCodigo;
	}
	public void setBancoCodigo(Integer bancoCodigo) {
		this.bancoCodigo = bancoCodigo;
	}
	public String getBancoNome() {
		return bancoNome;
	}
	public void setBancoNome(String bancoNome) {
		this.bancoNome = bancoNome;
	}
	public Integer getAgenciaCodigo() {
		return agenciaCodigo;
	}
	public void setAgenciaCodigo(Integer agenciaCodigo) {
		this.agenciaCodigo = agenciaCodigo;
	}
	public String getAgenciaDescricao() {
		return agenciaDescricao;
	}
	public void setAgenciaDescricao(String agenciaDescricao) {
		this.agenciaDescricao = agenciaDescricao;
	}
	public String getContaCorrente() {
		return contaCorrente;
	}
	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}
	public DominioSimNao getIndPreferencial() {
		return indPreferencial;
	}
	public void setIndPreferencial(DominioSimNao indPreferencial) {
		this.indPreferencial = indPreferencial;
	}
	public void setSelectedPref(boolean selectedPref) {
		this.selectedPref = selectedPref;
	}
	public boolean isSelectedPref() {
		return DominioSimNao.S.equals(indPreferencial);
	}
	public boolean obterSelectedPref(){
		return this.selectedPref;
	}
}
