package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ConsultaMaterialRequisicaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8268800000455972114L;

	private String matDscr;
	private String matLict;
	private Integer qtdSolc;
	private Integer qtdAutr;
	private BigDecimal vlrUnit;
	private BigDecimal vlrTotl;
	private BigDecimal vlrSus;
	private BigDecimal vlrDifr;
	private Integer matSeqn;

	public String getMatDscr() {
		return matDscr;
	}

	public void setMatDscr(String matDscr) {
		this.matDscr = matDscr;
	}

	public String getMatLict() {
		return matLict;
	}

	public void setMatLict(String matLict) {
		this.matLict = matLict;
	}

	public Integer getQtdSolc() {
		return qtdSolc;
	}

	public void setQtdSolc(Integer qtdSolc) {
		this.qtdSolc = qtdSolc;
	}

	public Integer getQtdAutr() {
		return qtdAutr;
	}

	public void setQtdAutr(Integer qtdAutr) {
		this.qtdAutr = qtdAutr;
	}

	public BigDecimal getVlrUnit() {
		return vlrUnit;
	}

	public void setVlrUnit(BigDecimal vlrUnit) {
		this.vlrUnit = vlrUnit;
	}

	public BigDecimal getVlrTotl() {
		return vlrTotl;
	}

	public void setVlrTotl(BigDecimal vlrTotl) {
		this.vlrTotl = vlrTotl;
	}

	public BigDecimal getVlrSus() {
		return vlrSus;
	}

	public void setVlrSus(BigDecimal vlrSus) {
		this.vlrSus = vlrSus;
	}

	public BigDecimal getVlrDifr() {
		return vlrDifr;
	}

	public void setVlrDifr(BigDecimal vlrDifr) {
		this.vlrDifr = vlrDifr;
	}

	public Integer getMatSeqn() {
		return matSeqn;
	}

	public void setMatSeqn(Integer matSeqn) {
		this.matSeqn = matSeqn;
	}

	public enum Fields {
		PCI_SEQ("pciSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}