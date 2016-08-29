package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;


public class ResumoAIHEmLoteServicosVO implements Serializable {

	//  ATENÇÃO AOS MÉTODOS SETs e GETs OS MESMOS POSSUEM CERTA LÓGICA...
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7246628073999849456L;
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
	private Integer taoseq;
	private Integer tivseq;
	
	public enum Fields {
		EAI_CTH_SEQ("eaicthseq"),
		EAI_SEQ("eaiseq"),
		SEQ_P("seqp"),
		IPH_PHO_SEQ("iphphoseq"),
		IPH_SEQ("iphseq"),
		IPH_COD_SUS("iphcodsus"),
		IPHO_PHO_SEQ("iphophoseq"),
		IPHO_SEQ("iphoseq"),
		COMPETENCIA_UTI("competenciauti"),
		FCF_SEQ("fcfseq"),
		CODIGO("codigo"),
		CODIGO_PAD("codigopad"),
		DESCRICAO("descricao"),
		QUANTIDADE("quantidade"),
		VALOR_SH("valorsh"),
		VALOR_SP("valorsp"),
		VALOR_SADT("valorsadt"),
		VALOR_PROCEDIMENTO("valorprocedimento"),
		VALOR_PRINCIPAL("valorprincipal"),
		VALOR_TOTAL("valortotal"),
		REGISTRO("registro"),
		REGISTRO_ORDER("registroorder"),
		FINANC("financ"),
		COMPLEXIDADE("complexidade"),
		SEQUENCIA("sequencia"),
		SEQ_ARQ_SUS("seqarqsus"),
		VAL_VALOR_SH("valvalorsh"),
		VAL_VALOR_SP("valvalorsp"),
		VAL_VALOR_SADT("valvalorsadt"),
		VAL_VALOR_PROCEDIMENTO("valvalorprocedimento"),
		TAO_SEQ("taoseq"),
		TIV_SEQ("tivseq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}
	
	
	public Long getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
		
		if(codigo != null) {
			final String codigoPad = StringUtils.leftPad(codigo.toString(), 10, "0");
			
			if(codigoPad.substring(0, 2).equals("07")) {
				setCodigopad("00");
			} else {
				setCodigopad(codigoPad.substring(0, 2));
			}
		}
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
		
		if( fcfseq !=null){
			switch (fcfseq) {
				case 2: setFinanc("1FAEC");  break;
				case 3: setFinanc("3MEDIA"); break;
				case 4: 
					setFinanc("2ALTA"); 
					setComplexidade("ALTA");
					break;
					
				case 5:
				case 6:
				case 7:
				case 8: 
					setFinanc("4XXXXX");
					setComplexidade("MEDIA");
					break;
				default: 
					setFinanc("4XXXXX");
					setComplexidade("XXXXXX");
					break;
			}
		} else {
			setFinanc("4XXXXX");
			setComplexidade("XXXXXX");
		}
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
		
		if(valvalorsh != null){
			if(getValorprincipal() == null){
				valorprincipal = BigDecimal.ZERO;
			}
			valorprincipal.add(valvalorsh);
		}
	}

	public BigDecimal getValvalorsp() {
		return valvalorsp;
	}

	public void setValvalorsp(BigDecimal valvalorsp) {
		this.valvalorsp = valvalorsp;
		
		if(valvalorsp != null){
			if(getValorprincipal() == null){
				valorprincipal = BigDecimal.ZERO;
			}
			valorprincipal.add(valvalorsp);
		}
	}

	public BigDecimal getValvalorsadt() {
		return valvalorsadt;
	}

	public void setValvalorsadt(BigDecimal valvalorsadt) {
		this.valvalorsadt = valvalorsadt;
		
		if(valvalorsadt != null){
			if(getValorprincipal() == null){
				valorprincipal = BigDecimal.ZERO;
			}
			valorprincipal.add(valvalorsadt);
		}
	}

	public BigDecimal getValvalorprocedimento() {
		return valvalorprocedimento;
	}

	public void setValvalorprocedimento(BigDecimal valvalorprocedimento) {
		this.valvalorprocedimento = valvalorprocedimento;
		
		if(valvalorprocedimento != null){
			if(getValorprincipal() == null){
				valorprincipal = BigDecimal.ZERO;
			}
			valorprincipal.add(valvalorprocedimento);
		}
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
	
	public Integer getTaoseq() {
		return taoseq;
	}

	public void setTaoseq(Integer taoseq) {
		this.taoseq = taoseq;
	}

	public Integer getTivseq() {
		return tivseq;
	}

	public void setTivseq(Integer tivseq) {
		this.tivseq = tivseq;
	}
}