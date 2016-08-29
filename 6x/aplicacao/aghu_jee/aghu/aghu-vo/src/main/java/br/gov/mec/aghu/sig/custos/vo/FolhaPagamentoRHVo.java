package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class FolhaPagamentoRHVo {

	private BigDecimal cctCodigoAtua;
	private BigDecimal cctCodigoLotado;
	private BigDecimal gocSeq;
	private String ocaCarCodigo;
	private BigDecimal ocaCodigo;
	private BigDecimal nroFuncionarios;
	private BigDecimal totHrContrato;
	private BigDecimal totHrDesconto;
	private BigDecimal totHrExcede;
	private BigDecimal totSalBase;
	private BigDecimal totSalarios;
	private BigDecimal totEncargos;
	private BigDecimal totProv13;
	private BigDecimal totProvFerias;
	private BigDecimal totProvEncargos;
	
	public FolhaPagamentoRHVo(){
		
	}
	
	public BigDecimal getCctCodigoAtua() {
		return cctCodigoAtua;
	}
	public void setCctCodigoAtua(BigDecimal cctCodigoAtua) {
		this.cctCodigoAtua = cctCodigoAtua;
	}
	public BigDecimal getCctCodigoLotado() {
		return cctCodigoLotado;
	}
	public void setCctCodigoLotado(BigDecimal cctCodigoLotado) {
		this.cctCodigoLotado = cctCodigoLotado;
	}
	public BigDecimal getGocSeq() {
		return gocSeq;
	}
	public void setGocSeq(BigDecimal gocSeq) {
		this.gocSeq = gocSeq;
	}
	public String getOcaCarCodigo() {
		return ocaCarCodigo;
	}
	public void setOcaCarCodigo(String ocaCarCodigo) {
		this.ocaCarCodigo = ocaCarCodigo;
	}
	public BigDecimal getOcaCodigo() {
		return ocaCodigo;
	}
	public void setOcaCodigo(BigDecimal ocaCodigo) {
		this.ocaCodigo = ocaCodigo;
	}
	public BigDecimal getTotHrContrato() {
		return totHrContrato;
	}
	public void setTotHrContrato(BigDecimal totHrContrato) {
		this.totHrContrato = totHrContrato;
	}
	public BigDecimal getTotHrDesconto() {
		return totHrDesconto;
	}
	public void setTotHrDesconto(BigDecimal totHrDesconto) {
		this.totHrDesconto = totHrDesconto;
	}
	public BigDecimal getTotHrExcede() {
		return totHrExcede;
	}
	public void setTotHrExcede(BigDecimal totHrExcede) {
		this.totHrExcede = totHrExcede;
	}
	public BigDecimal getTotSalBase() {
		return totSalBase;
	}
	public void setTotSalBase(BigDecimal totSalBase) {
		this.totSalBase = totSalBase;
	}
	public BigDecimal getTotSalarios() {
		return totSalarios;
	}
	public void setTotSalarios(BigDecimal totSalarios) {
		this.totSalarios = totSalarios;
	}
	public BigDecimal getTotEncargos() {
		return totEncargos;
	}
	public void setTotEncargos(BigDecimal totEncargos) {
		this.totEncargos = totEncargos;
	}
	public BigDecimal getTotProv13() {
		return totProv13;
	}
	public void setTotProv13(BigDecimal totProv13) {
		this.totProv13 = totProv13;
	}
	public BigDecimal getTotProvFerias() {
		return totProvFerias;
	}
	public void setTotProvFerias(BigDecimal totProvFerias) {
		this.totProvFerias = totProvFerias;
	}
	public BigDecimal getTotProvEncargos() {
		return totProvEncargos;
	}
	public void setTotProvEncargos(BigDecimal totProvEncargos) {
		this.totProvEncargos = totProvEncargos;
	}

	public BigDecimal getNroFuncionarios() {
		return nroFuncionarios;
	}

	public void setNroFuncionarios(BigDecimal nroFuncionarios) {
		this.nroFuncionarios = nroFuncionarios;
	}
	
	

}
