package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class DemonstrativoFaturamentoInternacaoVO {

	private Integer cthSeq;
	private Integer prontuario;
	private String nome;
	private String leito;
	private String enfermaria;
	private Long nroAih;
	private Date dtInternacao;
	private Date dtSaida;
	private Integer tempoPermanencia;
	private String motivoInternacao;
	private String equipeResposavel;
	private BigDecimal totalServHosp;
	private BigDecimal totalServProf;
	private BigDecimal totalAnest;
	private BigDecimal totalProcedimento;
	private BigDecimal totalSADT;
	private BigDecimal totalAIH;
	private BigDecimal totalInternacao;
	private Integer indiceConta;
	private Integer numeroContas;
	
	private List<ItemProcedimentoHospitalarVO> listaProcedimento;
	
	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

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
	
	public Integer getTempoPermanencia() {
		return tempoPermanencia;
	}

	public void setTempoPermanencia(Integer tempoPermanencia) {
		this.tempoPermanencia = tempoPermanencia;
	}

	public List<ItemProcedimentoHospitalarVO> getListaProcedimento() {
		return listaProcedimento;
	}

	public void setListaProcedimento(
			List<ItemProcedimentoHospitalarVO> listaProcedimento) {
		this.listaProcedimento = listaProcedimento;
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
	

	public String getMotivoInternacao() {
		return motivoInternacao;
	}

	public void setMotivoInternacao(String motivoInternacao) {
		this.motivoInternacao = motivoInternacao;
	}

	public String getEquipeResposavel() {
		return equipeResposavel;
	}
	
	public void setEquipeResposavel(String equipeResposavel) {
		this.equipeResposavel = equipeResposavel;
	}
	
	public BigDecimal getTotalServHosp() {
		return totalServHosp;
	}
	
	public void setTotalServHosp(BigDecimal totalServHosp) {
		this.totalServHosp = totalServHosp;
	}
	
	public BigDecimal getTotalServProf() {
		return totalServProf;
	}
	
	public void setTotalServProf(BigDecimal totalServProf) {
		this.totalServProf = totalServProf;
	}
	
	public BigDecimal getTotalSADT() {
		return totalSADT;
	}
	
	public void setTotalSADT(BigDecimal totalSADT) {
		this.totalSADT = totalSADT;
	}
	
	public BigDecimal getTotalAIH() {
		return totalAIH;
	}
	
	public void setTotalAIH(BigDecimal totalAIH) {
		this.totalAIH = totalAIH;
	}
	
	public BigDecimal getTotalInternacao() {
		return totalInternacao;
	}
	
	public void setTotalInternacao(BigDecimal totalInternacao) {
		this.totalInternacao = totalInternacao;
	}

	public BigDecimal getTotalAnest() {
		return totalAnest;
	}

	public void setTotalAnest(BigDecimal totalAnest) {
		this.totalAnest = totalAnest;
	}

	public BigDecimal getTotalProcedimento() {
		return totalProcedimento;
	}

	public void setTotalProcedimento(BigDecimal totalProcedimento) {
		this.totalProcedimento = totalProcedimento;
	}
	
	public Integer getIndiceConta() {
		return indiceConta;
	}

	public void setIndiceConta(Integer indiceConta) {
		this.indiceConta = indiceConta;
	}

	public Integer getNumeroContas() {
		return numeroContas;
	}

	public void setNumeroContas(Integer numeroContas) {
		this.numeroContas = numeroContas;
	}		
}
