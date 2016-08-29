package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;


public class ResumoCobrancaAihServicosVO {

	private Integer eaicthseq;
	private Integer eaiseq;
	private Byte seqp;
	private Short iphphoseq;
	private Integer iphseq;
	private Long iphcodsus;
	private Short iphophoseq;
	private Integer iphoseq;
	private Integer fcfseq;
	private Long codigo;
	private String codigopad;
	private String descricao;
	private Short quantidade;
	private BigDecimal valorsh;
	private BigDecimal valorsp;
	private BigDecimal valorsadt;
	private BigDecimal valorprocedimento;
	private BigDecimal valorprincipal;
	private BigDecimal valortotal;
	private String registro;
	private String registroorder;
	private String financ;
	private String complexidade;
	private String sequencia;
	private Short seqarqsus;
	private BigDecimal valvalorsh;
	private BigDecimal valvalorsp;
	private BigDecimal valvalorsadt;
	private BigDecimal valvalorprocedimento;
	private String competenciauti;
	private Integer tivseq;
	private Integer taoseq;
	
	public Long getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Short getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorsh() {
		return valorsh;
	}

	public void setValorsh(BigDecimal valorsh) {
		this.valorsh = valorsh;
	}

	public BigDecimal getValorsp() {
		return valorsp;
	}

	public void setValorsp(BigDecimal valorsp) {
		this.valorsp = valorsp;
	}

	public BigDecimal getValorsadt() {
		return valorsadt;
	}

	public void setValorsadt(BigDecimal valorsadt) {
		this.valorsadt = valorsadt;
	}

	public BigDecimal getValorprocedimento() {
		return valorprocedimento;
	}

	public void setValorprocedimento(BigDecimal valorprocedimento) {
		this.valorprocedimento = valorprocedimento;
	}

	public BigDecimal getValortotal() {
		valortotal = BigDecimal.ZERO;
		
		if(valorsh!=null) {
			valortotal = valortotal.add(valorsh); 
		} 
		if(valorsp!=null) {
			valortotal = valortotal.add(valorsp); 
		} 

		if(valorprocedimento!=null) {
			valortotal = valortotal.add(valorprocedimento); 
		} 

		if(valorsadt!=null) {
			valortotal = valortotal.add(valorsadt); 
		} 

		return valortotal;
	}

	public void setValortotal(BigDecimal valortotal) {
		this.valortotal = valortotal;
	}
		
	public Byte getSeqp() {
		return seqp;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	public Integer getEaiseq() {
		return eaiseq;
	}

	public void setEaiseq(Integer eaiseq) {
		this.eaiseq = eaiseq;
	}

	public Integer getEaicthseq() {
		return eaicthseq;
	}

	public void setEaicthseq(Integer eaicthseq) {
		this.eaicthseq = eaicthseq;
	}

	public Short getIphphoseq() {
		return iphphoseq;
	}

	public void setIphphoseq(Short iphphoseq) {
		this.iphphoseq = iphphoseq;
	}

	public Integer getIphseq() {
		return iphseq;
	}

	public void setIphseq(Integer iphseq) {
		this.iphseq = iphseq;
	}

	public Long getIphcodsus() {
		return iphcodsus;
	}

	public void setIphcodsus(Long iphcodsus) {
		this.iphcodsus = iphcodsus;
	}

	public Short getIphophoseq() {
		return iphophoseq;
	}

	public void setIphophoseq(Short iphophoseq) {
		this.iphophoseq = iphophoseq;
	}

	public Integer getIphoseq() {
		return iphoseq;
	}

	public void setIphoseq(Integer iphoseq) {
		this.iphoseq = iphoseq;
	}

	public Integer getFcfseq() {
		return fcfseq;
	}

	public void setFcfseq(Integer fcfseq) {
		this.fcfseq = fcfseq;
	}

	public BigDecimal getValorprincipal() {
		return valorprincipal;
	}

	public void setValorprincipal(BigDecimal valorprincipal) {
		this.valorprincipal = valorprincipal;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getFinanc() {
		return financ;
	}

	public void setFinanc(String financ) {
		this.financ = financ;
	}

	public String getComplexidade() {
		return complexidade;
	}

	public void setComplexidade(String complexidade) {
		this.complexidade = complexidade;
	}

	public String getSequencia() {
		return sequencia;
	}

	public void setSequencia(String sequencia) {
		this.sequencia = sequencia;
	}

	public BigDecimal getValvalorsh() {
		return valvalorsh;
	}

	public void setValvalorsh(BigDecimal valvalorsh) {
		this.valvalorsh = valvalorsh;
	}

	public BigDecimal getValvalorsp() {
		return valvalorsp;
	}

	public void setValvalorsp(BigDecimal valvalorsp) {
		this.valvalorsp = valvalorsp;
	}

	public BigDecimal getValvalorsadt() {
		return valvalorsadt;
	}

	public void setValvalorsadt(BigDecimal valvalorsadt) {
		this.valvalorsadt = valvalorsadt;
	}

	public BigDecimal getValvalorprocedimento() {
		return valvalorprocedimento;
	}

	public void setValvalorprocedimento(BigDecimal valvalorprocedimento) {
		this.valvalorprocedimento = valvalorprocedimento;
	}

	public Short getSeqarqsus() {
		return seqarqsus;
	}

	public void setSeqarqsus(Short seqarqsus) {
		this.seqarqsus = seqarqsus;
	}

	public String getRegistroorder() {
		return registroorder;
	}

	public void setRegistroorder(String registroorder) {
		this.registroorder = registroorder;
	}

	public String getCodigopad() {
		return codigopad;
	}

	public void setCodigopad(String codigopad) {
		this.codigopad = codigopad;
	}
	
	public String getCompetenciaUti() {
		return competenciauti;
	}

	public void setCompetenciaUti(String competenciaUti) {
		this.competenciauti = competenciaUti;
	}
	
	public Integer getTivseq() {
		return tivseq;
	}

	public void setTivseq(Integer tivSeq) {
		this.tivseq = tivSeq;
	}

	public Integer getTaoseq() {
		return taoseq;
	}

	public void setTaoseq(Integer taoSeq) {
		this.taoseq = taoSeq;
	}
}