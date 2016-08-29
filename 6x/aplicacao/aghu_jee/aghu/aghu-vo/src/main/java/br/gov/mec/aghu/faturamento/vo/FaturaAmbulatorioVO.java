package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FaturaAmbulatorioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1880649885559402644L;
	
	private Integer mes;
	private Integer ano;
	private Date dtFim;
	private Short grupoSeq;
	private String grupo;
	private Byte subGrupoSeq;
	private String subGrupo;
	private Integer caracteristicaFinanciamentoSeq;
	private String caracteristicaFinanciamento;
	private Byte formaOrganizacaoCodigo;
	private String formaOrganizacao;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Long procedimentoHospitalar;
	private BigDecimal valorProcedimento;
	private Long quantidade;
	private Long quantidadeTeto;
	private BigDecimal valorTeto;
	private Long quantidadeTetoGeral;
	private BigDecimal valorTetoGeral;
	private Long diferencaQuantidadeTeto;
	private BigDecimal diferencaValorTeto;
	
	public Integer getAno() {
		return ano;
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Short getGrupoSeq() {
		return grupoSeq;
	}

	public void setGrupoSeq(Short grupoSeq) {
		this.grupoSeq = grupoSeq;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Byte getSubGrupoSeq() {
		return subGrupoSeq;
	}

	public void setSubGrupoSeq(Byte subGrupoSeq) {
		this.subGrupoSeq = subGrupoSeq;
	}

	public String getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(String subGrupo) {
		this.subGrupo = subGrupo;
	}

	public Byte getFormaOrganizacaoCodigo() {
		return formaOrganizacaoCodigo;
	}

	public void setFormaOrganizacaoCodigo(Byte formaOrganizacaoCodigo) {
		this.formaOrganizacaoCodigo = formaOrganizacaoCodigo;
	}

	public String getFormaOrganizacao() {
		return formaOrganizacao;
	}

	public void setFormaOrganizacao(String formaOrganizacao) {
		this.formaOrganizacao = formaOrganizacao;
	}

	public Long getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(Long procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public BigDecimal getValorProcedimento() {
		return valorProcedimento;
	}

	public void setValorProcedimento(BigDecimal valorProcedimento) {
		this.valorProcedimento = valorProcedimento;
	}

	public Long getQuantidadeTeto() {
		return quantidadeTeto;
	}

	public void setQuantidadeTeto(Long quantidadeTeto) {
		this.quantidadeTeto = quantidadeTeto;
	}

	public BigDecimal getValorTeto() {
		return valorTeto;
	}

	public void setValorTeto(BigDecimal valorTeto) {
		this.valorTeto = valorTeto;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getCaracteristicaFinanciamentoSeq() {
		return caracteristicaFinanciamentoSeq;
	}

	public void setCaracteristicaFinanciamentoSeq(
			Integer caracteristicaFinanciamentoSeq) {
		this.caracteristicaFinanciamentoSeq = caracteristicaFinanciamentoSeq;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Long getQuantidadeTetoGeral() {
		return quantidadeTetoGeral;
	}

	public void setQuantidadeTetoGeral(Long quantidadeTetoGeral) {
		this.quantidadeTetoGeral = quantidadeTetoGeral;
	}

	public BigDecimal getValorTetoGeral() {
		return valorTetoGeral;
	}

	public void setValorTetoGeral(BigDecimal valorTetoGeral) {
		this.valorTetoGeral = valorTetoGeral;
	}

	public String getCaracteristicaFinanciamento() {
		return caracteristicaFinanciamento;
	}

	public void setCaracteristicaFinanciamento(String caracteristicaFinanciamento) {
		this.caracteristicaFinanciamento = caracteristicaFinanciamento;
	}

	public Long getDiferencaQuantidadeTeto() {
		return diferencaQuantidadeTeto;
	}

	public void setDiferencaQuantidadeTeto(Long diferencaQuantidadeTeto) {
		this.diferencaQuantidadeTeto = diferencaQuantidadeTeto;
	}

	public BigDecimal getDiferencaValorTeto() {
		return diferencaValorTeto;
	}

	public void setDiferencaValorTeto(BigDecimal diferencaValorTeto) {
		this.diferencaValorTeto = diferencaValorTeto;
	}

}
