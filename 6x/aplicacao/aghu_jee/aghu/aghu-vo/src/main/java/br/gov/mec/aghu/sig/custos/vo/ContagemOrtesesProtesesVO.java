package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ContagemOrtesesProtesesVO {
	

	private Integer cdcSeq;
	private Integer ccaSeq;
	private Integer phiSeq;
	private Integer matCodigo;
	private String rmpSeq;
	private BigDecimal qtdePrevisto;
	private BigDecimal qtdeDebitado;
	private BigDecimal qtdeConsumido;
	private Integer cctCodigo;
	
	public ContagemOrtesesProtesesVO(Integer cdcSeq, Integer ccaSeq,
			Integer phiSeq, Integer matCodigo, String rmpSeq,
			BigDecimal qtdePrevisto, BigDecimal qtdeDebitado,
			BigDecimal qtdeConsumido, Integer cctCodigo) {
		super();
		this.cdcSeq = cdcSeq;
		this.ccaSeq = ccaSeq;
		this.phiSeq = phiSeq;
		this.matCodigo = matCodigo;
		this.rmpSeq = rmpSeq;
		this.qtdePrevisto = qtdePrevisto;
		this.qtdeDebitado = qtdeDebitado;
		this.qtdeConsumido = qtdeConsumido;
		this.cctCodigo = cctCodigo;
	}
	
	public ContagemOrtesesProtesesVO(){}
	
	public Integer getCdcSeq() {
		return cdcSeq;
	}
	public void setCdcSeq(Integer cdcSeq) {
		this.cdcSeq = cdcSeq;
	}
	public Integer getCcaSeq() {
		return ccaSeq;
	}
	public void setCcaSeq(Integer ccaSeq) {
		this.ccaSeq = ccaSeq;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getRmpSeq() {
		return rmpSeq;
	}
	public void setRmpSeq(String rmpSeq) {
		this.rmpSeq = rmpSeq;
	}
	public BigDecimal getQtdePrevisto() {
		return qtdePrevisto;
	}
	public void setQtdePrevisto(BigDecimal qtdePrevisto) {
		this.qtdePrevisto = qtdePrevisto;
	}
	public BigDecimal getQtdeDebitado() {
		return qtdeDebitado;
	}
	public void setQtdeDebitado(BigDecimal qtdeDebitado) {
		this.qtdeDebitado = qtdeDebitado;
	}
	public BigDecimal getQtdeConsumido() {
		return qtdeConsumido;
	}
	public void setQtdeConsumido(BigDecimal qtdeConsumido) {
		this.qtdeConsumido = qtdeConsumido;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	
	public enum Fields{
		CDC_SEQ("cdcSeq"),
		CCA_SEQ("ccaSeq"),
		PHI_SEQ("phiSeq"),
		MAT_CODIGO("matCodigo"),
		RMP_SEQ("rmpSeq"),
		QTED_PREVISTO("qtdePrevisto"),
		QTDE_DEBITADO("qtdeDebitado"),
		QTDE_CONSUMIDO("qtdeConsumido"),
		CCT_CODIGO("cctCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
