package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ProcedimentoNaoFaturadoVO {

	private Integer prontuario;
	private String nome;
	private String especialidade;
	private String siglaEspecialidade;
	private String leito;
	private String enfermaria;
	private Long nroAih;
	private Date dtInternacao;
	private Date dtSaida;
	private Long iphCodSusRealiz;
	private String descricaoProcedimento;
	private Long codTabela;
	private Long quantidadePerdida;
	private BigDecimal valorApresentado;
	private BigDecimal valorTotal;
	private BigDecimal valorServHosp;
	private BigDecimal valorServProf;
	private BigDecimal valorAnest;
	private BigDecimal valorProcedimento;
	private BigDecimal valorSADT;

	
	public Integer getProntuario() {
		return prontuario;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEspecialidade() {
		return especialidade;
	}
	
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	
	public String getLeito() {
		return leito;
	}
	
	public void setLeito(String leito) {
		this.leito = leito;
	}
	
	public String getEnfermaria() {
		return enfermaria;
	}
	
	public void setEnfermaria(String enfermaria) {
		this.enfermaria = enfermaria;
	}
	
	public Long getNroAih() {
		return nroAih;
	}
	
	public void setNroAih(Long nroAih) {
		this.nroAih = nroAih;
	}
	
	public Date getDtInternacao() {
		return dtInternacao;
	}
	
	public void setDtInternacao(Date dtInternacao) {
		this.dtInternacao = dtInternacao;
	}
	
	public Date getDtSaida() {
		return dtSaida;
	}
	
	public void setDtSaida(Date dtSaida) {
		this.dtSaida = dtSaida;
	}
	
	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}
	
	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}
	
	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}
	
	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}
	
	public Long getCodTabela() {
		return codTabela;
	}
	
	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}
	
	public Long getQuantidadePerdida() {
		return quantidadePerdida;
	}
	
	public void setQuantidadePerdida(Long quantidadePerdida) {
		this.quantidadePerdida = quantidadePerdida;
	}
	
	public BigDecimal getValorApresentado() {
		valorApresentado = BigDecimal.ZERO;
		if(valorServHosp!=null) {
			valorApresentado = valorApresentado.add(valorServHosp); 
		} 
		if(valorServProf!=null) {
			valorApresentado = valorApresentado.add(valorServProf); 
		} 
		if(valorSADT!=null) {
			valorApresentado = valorApresentado.add(valorSADT); 
		} 
		if(valorProcedimento!=null) {
			valorApresentado = valorApresentado.add(valorProcedimento); 
		} 
		if(valorAnest!=null) {
			valorApresentado = valorApresentado.add(valorAnest); 
		} 
		return valorApresentado;
	}
	
	public void setValorApresentado(BigDecimal valorApresentado) {
		this.valorApresentado = valorApresentado;
	}
	
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal getValorServHosp() {
		return valorServHosp;
	}
	
	public void setValorServHosp(BigDecimal valorServHosp) {
		this.valorServHosp = valorServHosp;
	}
	
	public BigDecimal getValorServProf() {
		return valorServProf;
	}
	
	public void setValorServProf(BigDecimal valorServProf) {
		this.valorServProf = valorServProf;
	}
	
	public BigDecimal getValorAnest() {
		return valorAnest;
	}
	
	public void setValorAnest(BigDecimal valorAnest) {
		this.valorAnest = valorAnest;
	}
	
	public BigDecimal getValorProcedimento() {
		return valorProcedimento;
	}
	
	public void setValorProcedimento(BigDecimal valorProcedimento) {
		this.valorProcedimento = valorProcedimento;
	}
	
	public BigDecimal getValorSADT() {
		return valorSADT;
	}

	public void setValorSADT(BigDecimal valorSADT) {
		this.valorSADT = valorSADT;
	}
}
